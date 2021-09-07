import React, { useCallback, useEffect, useState } from "react";
import { STOCKS_URL } from "../constants/paths";
import useHttp from "../hooks/useHttp";

const INITIAL_STOCKS = [
  {
    ticker: "",
    currentValue: 0.0,
    lastClose: 0.0,
    lastWeekClose: 0.0,
    lastMonthClose: 0.0,
    lastYearClose: 0.0,
    dateUpdated: new Date(),
  },
];

export const DEFAULT_STOCKS = {
  stocks: INITIAL_STOCKS,
  error: null,
  isLoading: null,
  invalidateCache: () => {},
  resetContext: () => {},
  fetchStocks: () => {},
};

const FIVE_MINUTES = 5 * 60000;

export function StocksContextProvider(props) {
  const [stocks, setStocks] = useState(INITIAL_STOCKS);
  const [lastSuccess, setLastSuccess] = useState(0.0);
  const [lastUpdated, setLastUpdated] = useState(0.0);
  const [isCacheValid, setIsCacheValid] = useState(false);
  const { result, error, isLoading, sendRequest } = useHttp();

  const getStocksHandler = useCallback(async () => {
    const last = lastSuccess + FIVE_MINUTES;
    const now = new Date().getTime();

    if ((!isLoading && last < now) || !isCacheValid) {
      setLastSuccess(now);
      setIsCacheValid(true);
      sendRequest({
        url: STOCKS_URL,
      });
    }
  }, [lastSuccess, isLoading, isCacheValid, sendRequest]);

  const invalidateCache = useCallback(() => {
    setIsCacheValid(false);
  }, [setIsCacheValid]);

  const resetContext = useCallback(() => {
    setStocks(INITIAL_STOCKS);
    setLastSuccess(0.0);
    setLastUpdated(0.0);
    setIsCacheValid(false);
  }, []);

  useEffect(() => {
    if (lastUpdated !== lastSuccess && !isLoading && result) {
      setStocks(result);
      setLastUpdated(lastSuccess);
    }
  }, [lastUpdated, lastSuccess, result, isLoading]);

  return (
    <StocksContext.Provider
      value={{
        stocks,
        error,
        isLoading,
        fetchStocks: getStocksHandler,
        invalidateCache,
        resetContext,
      }}
    >
      {props.children}
    </StocksContext.Provider>
  );
}

const StocksContext = React.createContext(DEFAULT_STOCKS);

export default StocksContext;
