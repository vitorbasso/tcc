import { useContext } from "react";
import AuthContext from "../context/auth-context";
import MonthWallet from "../context/month-wallet-context";
import WalletMonths from "../context/month-wallets-context";
import StocksContext from "../context/stock-context";
import TaxContext from "../context/tax-context";
import WalletContext from "../context/wallet-context";

export default function useLogout() {
  const authCtx = useContext(AuthContext);
  const { resetContext: resetWalletContext } = useContext(WalletContext);
  const { resetContext: resetTaxContext } = useContext(TaxContext);
  const { resetContext: resetStockContext } = useContext(StocksContext);
  const { resetContext: resetMonthWalletContext } = useContext(MonthWallet);
  const { resetContext: resetWalletMonthsContext } = useContext(WalletMonths);

  return () => {
    authCtx.onLogout(() => {
      resetTaxContext();
      resetWalletContext();
      resetStockContext();
      resetMonthWalletContext();
      resetWalletMonthsContext();
    });
  };
}
