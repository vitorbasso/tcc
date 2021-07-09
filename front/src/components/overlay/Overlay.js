import reactDom from "react-dom";
import styles from "./Overlay.module.css";

function Overlay(props) {
  return reactDom.createPortal(
    <div onClick={props.onClick} className={styles.overlay}>
      {props.children}
    </div>,
    document.querySelector("body")
  );
}

export default Overlay;
