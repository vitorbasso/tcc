import React, { useRef, useState } from "react";
import { CgLogOut, CgMenu } from "react-icons/cg";
import { BsTrash } from "react-icons/bs";
import useLogout from "../../hooks/useLogout";
import styles from "./DropdownMenu.module.css";
import Overlay from "../overlay/Overlay";
import { CLIENTS_URL } from "../../constants/paths";
import useDeleteConfirmation from "../../hooks/useDeleteConfirmation";
import baseStyles from "../../css/base.module.css";

function DropdownMenu() {
  const logout = useLogout();
  const [visible, setVisible] = useState(false);
  const animateRef = useRef();
  const confirmDelete = useDeleteConfirmation();

  function handleDeleteAccount() {
    confirmDelete({
      title: "Deletar Conta?",
      message:
        "Tem Certeza que deseja deletar sua conta? Essa ação é irreversível.",
      url: `${CLIENTS_URL}`,
      method: "DELETE",
      onDelete: () => {
        logout();
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
          <li onClick={logout}>
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
