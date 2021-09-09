import { Link } from "react-router-dom";
import { percentFormatterWithoutSign } from "../../utils/formatterUtils";
import styles from "./PieSelected.module.css";
import baseStyles from "../../css/base.module.css";

function PieSelected(props) {
  const link = props.selected ? `performance/${props.selected?.label}` : "";

  const percentOfWallet = percentFormatterWithoutSign.format(
    props.selected?.percentage
  );
  const bar = `${styles.bar} ${
    baseStyles[`chart-color-${props.selected?.index}`]
  }`;
  if (!props.selected) return <div></div>;
  return (
    <Link
      to={{
        pathname: link,
        state: {
          caller: props.caller || "/",
        },
      }}
      className={`${styles.selected} ${props.className}`}
    >
      <div>
        <div className={bar}></div>
      </div>
      <p>{percentOfWallet}</p>
      {props.selected?.label && <p>{props.selected.label}</p>}
    </Link>
  );
}

export default PieSelected;
