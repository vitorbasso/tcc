import { moneyFormatter } from "../../utils/numberUtils";
import styles from "./Money.module.css";

function Money(props) {
  const valueParts = moneyFormatter.formatToParts(props.value);
  const currency = valueParts.slice(0, 2).map((item) => item.value);
  const value = valueParts.slice(2, -2).map((item) => item.value);
  const cents = valueParts.slice(-2).map((item) => item.value);
  return (
    <div className={`${styles.money} ${props.className}`}>
      <span>
        <span>{currency}</span>
        <span>{value}</span>
        <span>{cents}</span>
      </span>
    </div>
  );
}

export default Money;
