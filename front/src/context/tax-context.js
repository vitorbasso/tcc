import React, { useCallback, useEffect, useMemo, useState } from "react";
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
  resetContext: () => {},
  query: () => {},
  fetchTax: () => {},
};

const FIVE_MINUTES = 5 * 60000;

export function TaxContextProvider(props) {
  const [tax, setTax] = useState(INITIAL_TAX);
  const [lastSuccess, setLastSuccess] = useState(0.0);
  const [lastUpdated, setLastUpdated] = useState(0.0);
  const [isCacheValid, setIsCacheValid] = useState(false);
  const { result, error, isLoading, sendRequest } = useHttp();
  const today = new Date().toISOString().slice(0, 7) + "-02";
  const [month, setMonth] = useState(today);
  const [lastMonth, setLastMonth] = useState(month);

  const getTaxHandler = useCallback(async () => {
    const last = lastSuccess + FIVE_MINUTES;
    const now = new Date().getTime();
    if ((!isLoading && last < now) || !isCacheValid || month !== lastMonth) {
      let query = "";
      if (month !== lastMonth) {
        setLastMonth(month);
        query = `?month=${month}`;
      }
      setLastSuccess(now);
      setIsCacheValid(true);
      sendRequest({
        url: `${TAX_URL}${query}`,
      });
    }
  }, [isLoading, lastSuccess, isCacheValid, sendRequest, month, lastMonth]);

  const queryMonth = useCallback(
    (newMonth) => {
      if (typeof newMonth === "string" && newMonth.length === 7) {
        const date = new Date(newMonth + "-02").toISOString().slice(0, 10);
        if (date !== month) setMonth(date);
      }
    },
    [month]
  );

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

  const provided = useMemo(() => {
    return {
      tax,
      error,
      isLoading,
      fetchTax: getTaxHandler,
      query: queryMonth,
      invalidateCache,
      resetContext,
    };
  }, [
    tax,
    error,
    isLoading,
    getTaxHandler,
    queryMonth,
    invalidateCache,
    resetContext,
  ]);

  return (
    <TaxContext.Provider value={provided}>{props.children}</TaxContext.Provider>
  );
}

const TaxContext = React.createContext(DEFAULT_TAX);

export default TaxContext;
