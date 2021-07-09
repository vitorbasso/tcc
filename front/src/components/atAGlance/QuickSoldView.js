import styles from "./QuickSoldView.module.css";
import Money from "../money/Money";

function QuickSoldView() {
  return (
    <div>
      <p>Vendas:</p>
      <p>
        <Money value={15_000} className={styles["money-size"]} />
      </p>
      <p>IR:</p>
      <p>
        <Money value={0} className={styles["money-size"]} />
      </p>
    </div>
  );
}

export default QuickSoldView;
