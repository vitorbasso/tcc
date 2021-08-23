import styles from "./QuickSoldView.module.css";
import Money from "../money/Money";

function QuickSoldView(props) {
  return (
    <div>
      <p>Vendas:</p>
      <p>
        <Money value={props.sold} className={styles["money-size"]} />
      </p>
      <p>IR:</p>
      <p>
        <Money value={props.tax} className={styles["money-size"]} />
      </p>
    </div>
  );
}

export default QuickSoldView;
