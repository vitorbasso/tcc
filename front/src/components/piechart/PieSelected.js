import { percentFormatter } from "../../utils/numberUtils";
import styles from "./PieSelected.module.css";

function PieSelected(props) {
  const percentOfWallet = percentFormatter.format(0.5);
  const bar = `${styles.bar} ${styles["rebeccapurple"]}`;
  const ticker = "petr4";
  return (
    <div className={`${styles.selected} ${props.className}`}>
      <div>
        <div className={bar}></div>
      </div>
      <p className={styles.percent}>{percentOfWallet}</p>
      <p>{ticker}</p>
    </div>
  );
}

export default PieSelected;
