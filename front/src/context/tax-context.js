import React, { useCallback, useEffect, useState } from "react";
import { TAX_URL } from "../constants/paths";
import useHttp from "../hooks/useHttp";

const INITIAL_TAX = {
  normalTax: 0,
  baseForCalculation: 0,
  withdrawn: 0,
  daytradeWithdrawn: 0,
  daytradeTax: 0,
  daytradeBaseForCalculation: 0,
  availableToDeduct: 0,
  daytradeAvailableToDeduct: 0,
  deducted: 0,
  daytradeDeducted: 0,
};

export const DEFAULT_TAX = {
  tax: INITIAL_TAX,
  error: null,
  isLoading: null,
  invalidateCache: () => {},
  fetchTax: () => {},
};

const FIVE_MINUTES = 5 * 60000;

export function TaxContextProvider(props) {
  const [tax, setTax] = useState(INITIAL_TAX);
  const [lastSuccess, setLastSuccess] = useState(0.0);
  const [lastUpdated, setLastUpdated] = useState(0.0);
  const [isCacheValid, setIsCacheValid] = useState(false);
  const { result, error, isLoading, sendRequest } = useHttp();

  const getTaxHandler = useCallback(async () => {
    const last = lastSuccess + FIVE_MINUTES;
    const now = new Date().getTime();
    if ((!isLoading && last < now) || !isCacheValid) {
      setLastSuccess(now);
      setIsCacheValid(true);
      sendRequest({
        url: TAX_URL,
      });
    }
  }, [isLoading, lastSuccess, isCacheValid, sendRequest]);

  const invalidateCache = useCallback(() => {
    setIsCacheValid(false);
  }, [setIsCacheValid]);

  const resetContext = useCallback(() => {
    setTax(INITIAL_TAX);
    setLastSuccess(0.0);
    setLastUpdated(0.0);
    setIsCacheValid(false);
  }, []);

  useEffect(() => {
    if (lastUpdated !== lastSuccess && !isLoading && result) {
      setTax(result);
      setLastUpdated(lastSuccess);
    }
  }, [lastUpdated, lastSuccess, result, isLoading]);

  return (
    <taxContext.Provider
      value={{
        tax,
        error,
        isLoading,
        fetchTax: getTaxHandler,
        invalidateCache,
        resetContext,
      }}
    >
      {props.children}
    </taxContext.Provider>
  );
}

const taxContext = React.createContext(DEFAULT_TAX);

export default taxContext;
