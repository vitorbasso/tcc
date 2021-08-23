import { BsCheck, BsExclamationCircle } from "react-icons/bs";
import styles from "./Notification.module.css";
import baseStyles from "../../css/base.module.css";
import { useEffect, useState } from "react";

export const SUCCESS_NOTIFICATION = "success";
export const ERROR_NOTIFICATION = "error";

function Notification(props) {
  const animationCss = props.show ? styles["move-up"] : styles["move-down"];
  const typeCss =
    props.type === SUCCESS_NOTIFICATION
      ? styles["bg-success"]
      : styles["bg-error"];
  return (
    <div className={`${styles.container} ${animationCss}`}>
      <div className={`${styles.rounded} ${typeCss}`}></div>
      <div className={`${styles.content} ${typeCss} `}>
        <div className={styles["close-button"]}>
          <button onClick={props.onClose}>fechar</button>
        </div>
        <div className={styles.message}>
          <span>
            {props.message}{" "}
            {props.type === SUCCESS_NOTIFICATION ? (
              <BsCheck />
            ) : props.type === ERROR_NOTIFICATION ? (
              <BsExclamationCircle />
            ) : (
              ""
            )}
          </span>
        </div>
      </div>
    </div>
  );
}

export default Notification;
