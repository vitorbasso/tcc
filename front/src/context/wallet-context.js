import React, { useCallback, useEffect, useState } from "react";
import { WALLETS_URL } from "../constants/paths";
import useHttp from "../hooks/useHttp";

const INITIAL_WALLET = {
  id: -1,
  balanceDaytrade: 0,
  balance: 0,
  withdrawn: 0,
  withdrawnDaytrade: 0,
  walletMonth: new Date(),
  stockAssets: [
    {
      id: -1,
      stockSymbol: "",
      averageCost: 0,
      amount: 0,
      lifetimeBalance: 0,
    },
  ],
};

export const DEFAULT_WALLET = {
  wallet: INITIAL_WALLET,
  error: null,
  isLoading: null,
  invalidateCache: () => {},
  resetContext: () => {},
  fetchWallet: () => {},
};

const FIVE_MINUTES = 5 * 60000;

export function WalletContextProvider(props) {
  const [wallet, setWallet] = useState(INITIAL_WALLET);
  const [lastSuccess, setLastSuccess] = useState(0.0);
  const [lastUpdated, setLastUpdated] = useState(0.0);
  const [isCacheValid, setIsCacheValid] = useState(false);
  const { result, error, isLoading, sendRequest } = useHttp();

  const getWalletHandler = useCallback(async () => {
    const last = lastSuccess + FIVE_MINUTES;
    const now = new Date().getTime();

    if ((!isLoading && last < now) || !isCacheValid) {
      setLastSuccess(now);
      setIsCacheValid(true);
      sendRequest({
        url: WALLETS_URL,
      });
    }
  }, [lastSuccess, isLoading, isCacheValid, sendRequest]);

  const invalidateCache = useCallback(() => {
    setIsCacheValid(false);
  }, [setIsCacheValid]);

  const resetContext = useCallback(() => {
    setWallet(INITIAL_WALLET);
    setLastSuccess(0.0);
    setLastUpdated(0.0);
    setIsCacheValid(false);
  }, []);

  useEffect(() => {
    if (lastUpdated !== lastSuccess && !isLoading && result) {
      setWallet(result);
      setLastUpdated(lastSuccess);
    }
  }, [lastUpdated, lastSuccess, result, isLoading]);

  return (
    <WalletContext.Provider
      value={{
        wallet,
        error,
        isLoading,
        fetchWallet: getWalletHandler,
        invalidateCache,
        resetContext,
      }}
    >
      {props.children}
    </WalletContext.Provider>
  );
}

const WalletContext = React.createContext(DEFAULT_WALLET);

export default WalletContext;
