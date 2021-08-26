import React, { useEffect, useState } from "react";
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
  fetchTax: () => {},
};

const FIVE_MINUTES = 5 * 60000;

export function TaxContextProvider(props) {
  const [tax, setTax] = useState(INITIAL_TAX);
  const [lastSuccess, setLastSuccess] = useState(0);
  const { result, error, isLoading, sendRequest } = useHttp();

  async function getTaxHandler(force) {
    if (
      (!isLoading &&
        new Date(lastSuccess + FIVE_MINUTES) < new Date().getTime()) ||
      force
    ) {
      sendRequest({
        url: TAX_URL,
      });
    }
  }

  useEffect(() => {
    if (!isLoading && result) {
      setTax(result);
      setLastSuccess(new Date().getTime());
    }
  }, [result, isLoading]);

  return (
    <taxContext.Provider
      value={{
        tax,
        error,
        isLoading,
        fetchTax: getTaxHandler,
      }}
    >
      {props.children}
    </taxContext.Provider>
  );
}

const taxContext = React.createContext(DEFAULT_TAX);

export default taxContext;
