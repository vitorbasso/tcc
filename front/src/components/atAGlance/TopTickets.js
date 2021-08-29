import styles from "./TopTickets.module.css";
import { BsArrowDown, BsArrowUp } from "react-icons/bs";
import { percentFormatter } from "../../utils/numberUtils";
import { useContext } from "react";
import WalletContext from "../../context/wallet-context";
import { Link } from "react-router-dom";

function TopTickets() {
  const { wallet } = useContext(WalletContext);
  const topAssets = wallet.stockAssets
    .sort((first, second) => {
      const firstValue = first.amount * first.averageCost;
      const secondValue = second.amount * second.averageCost;
      if (firstValue > secondValue) return -1;
      else if (firstValue < secondValue) return 1;
      else return 0;
    })
    .slice(0, 3);
  return (
    <div className={styles["top-tickets"]}>
      {topAssets.map((asset) => {
        asset.valueChange = 0.0235;
        const [arrow, css] =
          asset.valueChange > 0
            ? [<BsArrowUp />, styles.green]
            : asset.valueChange < 0
            ? [<BsArrowDown />, styles.red]
            : [null, ""];
        return (
          <Link
            to={`performance/${asset.stockSymbol}`}
            key={asset.id}
            className={`${styles["value-change"]} ${css}`}
          >
            <span>
              {arrow} {asset.stockSymbol}
            </span>
            <span>{percentFormatter.format(asset.valueChange)}</span>
          </Link>
        );
      })}
    </div>
  );
}

export default TopTickets;
