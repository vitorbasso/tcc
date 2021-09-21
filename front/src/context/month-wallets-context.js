import React, { useCallback, useEffect, useMemo, useState } from "react";
import { WALLET_MONTH_URL } from "../constants/paths";
import useHttp from "../hooks/useHttp";

const INITIAL_WALLET_MONTHS = [
  {
    id: -1,
    month: "2021-09-01",
  },
];

export const DEFAULT_WALLET_MONTH = {
  walletMonths: INITIAL_WALLET_MONTHS,
  error: null,
  isLoading: null,
  invalidateCache: () => {},
  resetContext: () => {},
  fetchWalletMonthsList: () => {},
};

const FIVE_MINUTES = 5 * 60000;

export function MonthWalletsContextProvider(props) {
  const [walletMonths, setWalletMonths] = useState(INITIAL_WALLET_MONTHS);
  const [lastSuccess, setLastSuccess] = useState(0.0);
  const [lastUpdated, setLastUpdated] = useState(0.0);
  const [isCacheValid, setIsCacheValid] = useState(false);
  const { result, error, isLoading, sendRequest } = useHttp();

  const getWalletMonthsHandler = useCallback(async () => {
    const last = lastSuccess + FIVE_MINUTES;
    const now = new Date().getTime();
    if ((!isLoading && last < now) || !isCacheValid) {
      setLastSuccess(now);
      setIsCacheValid(true);
      sendRequest({
        url: WALLET_MONTH_URL,
      });
    }
  }, [isLoading, lastSuccess, isCacheValid, sendRequest]);

  const invalidateCache = useCallback(() => {
    setIsCacheValid(false);
  }, [setIsCacheValid]);

  const resetContext = useCallback(() => {
    setWalletMonths(INITIAL_WALLET_MONTHS);
    setLastSuccess(0.0);
    setLastUpdated(0.0);
    setIsCacheValid(false);
  }, []);

  useEffect(() => {
    if (lastUpdated !== lastSuccess && !isLoading && result) {
      setWalletMonths(result);
      setLastUpdated(lastSuccess);
    }
  }, [lastUpdated, lastSuccess, result, isLoading]);

  const provided = useMemo(() => {
    return {
      walletMonths,
      error,
      isLoading,
      fetchWalletMonthsList: getWalletMonthsHandler,
      invalidateCache,
      resetContext,
    };
  }, [
    walletMonths,
    error,
    isLoading,
    getWalletMonthsHandler,
    invalidateCache,
    resetContext,
  ]);

  return (
    <WalletMonths.Provider value={provided}>
      {props.children}
    </WalletMonths.Provider>
  );
}

const WalletMonths = React.createContext(DEFAULT_WALLET_MONTH);

export default WalletMonths;
