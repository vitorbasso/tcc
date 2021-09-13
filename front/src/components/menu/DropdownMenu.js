import React, { useContext, useRef, useState } from "react";
import { CgLogOut, CgMenu } from "react-icons/cg";
import { BsTrash } from "react-icons/bs";
import AuthContext from "../../context/auth-context";
import StocksContext from "../../context/stock-context";
import TaxContext from "../../context/tax-context";
import WalletContext from "../../context/wallet-context";
import styles from "./DropdownMenu.module.css";
import Overlay from "../overlay/Overlay";
import { CLIENTS_URL } from "../../constants/paths";
import useDeleteConfirmation from "../../hooks/useDeleteConfirmation";
import baseStyles from "../../css/base.module.css";

function DropdownMenu() {
  const authCtx = useContext(AuthContext);
  const { resetContext: resetTaxContext } = useContext(TaxContext);
  const { resetContext: resetWalletContext } = useContext(WalletContext);
  const { resetContext: resetStockContext } = useContext(StocksContext);
  const [visible, setVisible] = useState(false);
  const animateRef = useRef();
  const confirmDelete = useDeleteConfirmation();
  function handleLogout() {
    return authCtx.onLogout.bind(this, () => {
      resetTaxContext();
      resetWalletContext();
      resetStockContext();
    });
  }

  function handleDeleteAccount() {
    confirmDelete({
      title: "Deletar Conta?",
      message:
        "Tem Certeza que deseja deletar sua conta? Essa ação é irreversível.",
      url: `${CLIENTS_URL}`,
      method: "DELETE",
      onDelete: () => {
        handleLogout()();
      },
    });
  }

  return (
    <div className={styles.menu}>
      {visible && (
        <Overlay
          onClick={() => {
            animateRef.current.classList.toggle(styles["show-menu"]);
            setVisible(false);
          }}
          className={styles.overlay}
        />
      )}
      <i
        onClick={() => {
          animateRef.current.classList.toggle(styles["show-menu"]);
          setVisible((state) => !state);
        }}
        title="Menu"
      >
        <CgMenu />
      </i>
      <nav ref={animateRef} className={styles["menu-options"]}>
        <ul>
          <li
            className={baseStyles["error-text"]}
            onClick={handleDeleteAccount}
          >
            Deletar Conta
            <i title="Deletar Conta">
              <BsTrash />
            </i>
          </li>
          <li onClick={handleLogout()}>
            Logout{" "}
            <i title="Logout">
              <CgLogOut />
            </i>
          </li>
        </ul>
      </nav>
    </div>
  );
}

export default DropdownMenu;
