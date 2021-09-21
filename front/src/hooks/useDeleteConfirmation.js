import { useCallback, useContext } from "react";
import { confirmAlert } from "react-confirm-alert";
import baseStyles from "../css/base.module.css";
import TaxContext from "../context/tax-context";
import WalletContext from "../context/wallet-context";
import useHttp from "./useHttp";
import StocksContext from "../context/stock-context";
import MonthWallet from "../context/month-wallet-context";
import WalletMonths from "../context/month-wallets-context";

function useDeleteConfirmation() {
  const { invalidateCache: invalidateWalletCache } = useContext(WalletContext);
  const { invalidateCache: invalidateTaxCache } = useContext(TaxContext);
  const { invalidateCache: invalidateStocksCache } = useContext(StocksContext);
  const { invalidateCache: invalidateMonthWalletCache } =
    useContext(MonthWallet);
  const { invalidateCache: invalidateWalletMonthsCache } =
    useContext(WalletMonths);
  const { sendRequest } = useHttp();
  const confirmDelete = useCallback(
    (
      config = {
        title: "",
        message: "",
        url: "",
        onDelete: () => {},
      }
    ) => {
      confirmAlert({
        customUI: ({ onClose }) => {
          return (
            <div className={baseStyles["delete-prompt"]}>
              <h1>{config.title}</h1>
              <p>{config.message}</p>
              <div>
                <button
                  className={baseStyles.btn}
                  onClick={() => {
                    sendRequest({
                      url: config.url,
                      method: "DELETE",
                    })
                      .then(() => {
                        invalidateTaxCache();
                        invalidateWalletCache();
                        invalidateStocksCache();
                        invalidateMonthWalletCache();
                        invalidateWalletMonthsCache();
                      })
                      .then(() => {
                        config.onDelete?.();
                      });
                    onClose();
                  }}
                >
                  Sim
                </button>
                <button className={baseStyles.btn} onClick={onClose}>
                  Não
                </button>
              </div>
            </div>
          );
        },
        overlayClassName: `${baseStyles["delete-prompt-overlay"]}`,
      });
    },
    [
      invalidateTaxCache,
      invalidateWalletCache,
      invalidateStocksCache,
      sendRequest,
      invalidateMonthWalletCache,
      invalidateWalletMonthsCache,
    ]
  );
  return confirmDelete;
}

export default useDeleteConfirmation;
