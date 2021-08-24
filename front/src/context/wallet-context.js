import React, { useEffect, useState } from "react";
import { WALLETS_URL } from "../constants/paths";
import useHttp from "../hooks/useHttp";

const INITIAL_WALLET = {
  id: 0,
  balanceDaytrade: 0,
  balance: 0,
  withdrawn: 0,
  withdrawnDaytrade: 0,
  walletMonth: new Date(),
  stockAssets: [
    {
      id: 0,
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
  fetchWallet: () => {},
};

const FIVE_MINUTES = 5 * 60000;

export function WalletContextProvider(props) {
  const [wallet, setWallet] = useState(INITIAL_WALLET);
  const [lastSuccess, setLastSuccess] = useState(0);
  const { result, error, isLoading, sendRequest } = useHttp();

  async function getWalletHandler(force) {
    if (
      (!isLoading &&
        new Date(lastSuccess + FIVE_MINUTES) < new Date().getTime()) ||
      force
    ) {
      sendRequest({
        url: WALLETS_URL,
      });
    }
  }

  useEffect(() => {
    if (!isLoading && result) {
      setWallet(result);
      setLastSuccess(new Date().getTime());
    }
  }, [result, isLoading]);

  return (
    <WalletContext.Provider
      value={{
        wallet,
        error,
        isLoading,
        fetchWallet: getWalletHandler,
      }}
    >
      {props.children}
    </WalletContext.Provider>
  );
}

const WalletContext = React.createContext(DEFAULT_WALLET);

export default WalletContext;
