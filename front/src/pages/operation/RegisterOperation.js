import { useContext, useEffect, useRef, useState } from "react";
import Header from "../../components/header/Header";
import baseStyles from "../../css/base.module.css";
import styles from "./RegisterOperation.module.css";
import Notification, {
  ERROR_NOTIFICATION,
  SUCCESS_NOTIFICATION,
} from "../../components/notification/Notification";
import useHttp from "../../hooks/useHttp";
import { TRANSACTION_URL } from "../../constants/paths";
import LoadingOverlay from "../../components/loading-overlay/LoadingOverlay";
import WalletContext from "../../context/wallet-context";
import taxContext from "../../context/tax-context";

const BUY = "BUY";
const SELL = "SELL";

function RegisterOperation() {
  const { result, error, isLoading, sendRequest } = useHttp();
  const [type, setType] = useState(BUY);
  const [showNotification, setShowNotification] = useState(false);
  const tickerRef = useRef();
  const quantityRef = useRef();
  const valueRef = useRef();
  const dateRef = useRef();
  const [notificationType, setNotificationType] =
    useState(SUCCESS_NOTIFICATION);
  const [notificationMessage, setNotificationMessage] = useState("SUCESSO");
  const [resetNotification, setResetNotification] = useState(false);
  const { invalidateCache: invalidateWalletCache } = useContext(WalletContext);
  const { invalidateCache: invalidateTaxCache } = useContext(taxContext);
  function onCloseNotification() {
    setShowNotification(false);
  }
  function onSubmitHandler(event) {
    event.preventDefault();
    sendRequest({
      url: TRANSACTION_URL,
      method: "POST",
      body: {
        value: valueRef.current.value,
        quantity: quantityRef.current.value,
        ticker: tickerRef.current.value,
        type,
        date: dateRef.current.value || new Date().toISOString(),
      },
    });
  }

  useEffect(() => {
    if (!isLoading && (result || error)) {
      setShowNotification(true);
      if (result) {
        setNotificationType(SUCCESS_NOTIFICATION);
        setNotificationMessage("SUCESSO");
        invalidateTaxCache();
        invalidateWalletCache();
      } else if (error) {
        setNotificationType(ERROR_NOTIFICATION);
        setNotificationMessage("ERRO");
      }
    }
    const timeout = setTimeout(() => {
      setShowNotification(false);
    }, 5000);
    return () => {
      clearTimeout(timeout);
      setResetNotification(true);
    };
  }, [isLoading, result, error, invalidateTaxCache, invalidateWalletCache]);

  function onTypeClick(event) {
    event.preventDefault();
    const btn = event.target.closest("button");
    if (!btn) return;
    document
      .querySelector(`.${styles.selected}`)
      .classList.remove(styles.selected);
    btn.classList.add(styles.selected);
    if (btn.dataset.type !== type) setType(btn.dataset.type);
  }
  return (
    <div className={baseStyles.container}>
      {isLoading && <LoadingOverlay />}
      <Header backButton>
        <h2>Operação</h2>
      </Header>
      <main>
        <form onSubmit={onSubmitHandler} className={styles.form}>
          <div
            className={`${styles["form-type"]} ${baseStyles["form-control"]}`}
          >
            <span>Tipo</span>
            <div onClick={onTypeClick}>
              <button
                type="button"
                data-type={BUY}
                className={`${styles.btn} ${styles.selected}`}
              >
                COMPRA
              </button>
              <button type="button" data-type={SELL} className={styles.btn}>
                VENDA
              </button>
            </div>
          </div>
          <div className={baseStyles["form-control"]}>
            <input
              ref={tickerRef}
              type="text"
              id="ticker"
              name="ticker"
              placeholder="Ação"
              required
            />
          </div>
          <div className={baseStyles["form-control"]}>
            <input
              ref={quantityRef}
              type="number"
              name="quantity"
              min="0"
              placeholder="Quantidade"
              required
            />
          </div>
          <div className={baseStyles["form-control"]}>
            <input
              ref={valueRef}
              type="text"
              name="value"
              placeholder="Valor Total"
              required
            />
          </div>
          <div className={baseStyles["form-control"]}>
            <input ref={dateRef} type="datetime-local" name="date" />
          </div>
          <div className={baseStyles["form-control"]}>
            <button type="submit" className={baseStyles.btn}>
              Cadastrar
            </button>
          </div>
        </form>
        <Notification
          message={notificationMessage}
          type={notificationType}
          show={showNotification}
          onClose={onCloseNotification}
          reset={resetNotification}
        />
      </main>
    </div>
  );
}

export default RegisterOperation;
