import React, { useCallback, useEffect, useMemo, useState } from "react";
import { WALLET_MONTH_URL } from "../constants/paths";
import useHttp from "../hooks/useHttp";

const INITIAL_WALLET_MONTH = {
  id: -1,
  balanceDaytrade: 0,
  balance: 0,
  withdrawn: 0,
  withdrawnDaytrade: 0,
  walletId: 0,
  walletMonth: "2021-09-01",
};

export const DEFAULT_WALLET_MONTH = {
  monthWallet: INITIAL_WALLET_MONTH,
  walletId: -1,
  error: null,
  isLoading: null,
  invalidateCache: () => {},
  resetContext: () => {},
  query: () => {},
  fetchWalletMonth: () => {},
};

const FIVE_MINUTES = 5 * 60000;

export function MonthWalletContextProvider(props) {
  const [monthWallet, setMonthWallet] = useState(INITIAL_WALLET_MONTH);
  const [lastSuccess, setLastSuccess] = useState(0.0);
  const [lastUpdated, setLastUpdated] = useState(0.0);
  const [isCacheValid, setIsCacheValid] = useState(false);
  const { result, error, isLoading, sendRequest } = useHttp();
  const id = "-1";
  const [walletId, setWalletId] = useState(id);
  const [lastWalletId, setLastWalletId] = useState(walletId);

  const getWalletMonthHandler = useCallback(async () => {
    const last = lastSuccess + FIVE_MINUTES;
    const now = new Date().getTime();
    if (
      (!isLoading && last < now) ||
      !isCacheValid ||
      walletId !== lastWalletId
    ) {
      if (Number.parseInt(walletId) && Number.parseInt(walletId) > 0) {
        setLastWalletId(walletId);
        setLastSuccess(now);
        setIsCacheValid(true);
        sendRequest({
          url: `${WALLET_MONTH_URL}/${walletId}`,
        });
      }
    }
  }, [
    isLoading,
    lastSuccess,
    isCacheValid,
    sendRequest,
    walletId,
    lastWalletId,
  ]);

  const queryMonthWallet = useCallback(
    (newId) => {
      if (typeof newId === "number" && newId > 0) {
        if (lastWalletId !== Number.parseInt(newId)) {
          setWalletId(newId);
        }
      }
    },
    [lastWalletId]
  );

  const invalidateCache = useCallback(() => {
    setIsCacheValid(false);
  }, [setIsCacheValid]);

  const resetContext = useCallback(() => {
    setMonthWallet(INITIAL_WALLET_MONTH);
    setLastSuccess(0.0);
    setLastUpdated(0.0);
    setIsCacheValid(false);
  }, []);

  useEffect(() => {
    if (lastUpdated !== lastSuccess && !isLoading && result) {
      setMonthWallet(result);
      setLastUpdated(lastSuccess);
    }
  }, [lastUpdated, lastSuccess, result, isLoading]);

  const provided = useMemo(() => {
    return {
      monthWallet,
      walletId,
      error,
      isLoading,
      fetchWalletMonth: getWalletMonthHandler,
      query: queryMonthWallet,
      invalidateCache,
      resetContext,
    };
  }, [
    monthWallet,
    walletId,
    error,
    isLoading,
    getWalletMonthHandler,
    queryMonthWallet,
    invalidateCache,
    resetContext,
  ]);

  return (
    <MonthWallet.Provider value={provided}>
      {props.children}
    </MonthWallet.Provider>
  );
}

const MonthWallet = React.createContext(DEFAULT_WALLET_MONTH);

export default MonthWallet;
