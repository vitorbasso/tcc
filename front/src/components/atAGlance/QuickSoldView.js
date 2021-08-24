import styles from "./QuickSoldView.module.css";
import Money from "../money/Money";

function QuickSoldView(props) {
  return (
    <div>
      <p>Vendas:</p>
      <div>
        <Money value={props.sold} className={styles["money-size"]} />
      </div>
      <p>IR:</p>
      <div>
        <Money value={props.tax} className={styles["money-size"]} />
      </div>
    </div>
  );
}

export default QuickSoldView;
