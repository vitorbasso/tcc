import styles from "./TopTickets.module.css";
import { BsArrowDown, BsArrowUp } from "react-icons/bs";
import { percentFormatter } from "../../utils/formatterUtils";
import { useContext } from "react";
import WalletContext from "../../context/wallet-context";
import { Link } from "react-router-dom";

function TopTickets() {
  const { wallet } = useContext(WalletContext);
  const topAssets = wallet.stockAssets
    .filter((asset) => asset.amount !== 0)
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
      {topAssets.length === 0 && (
        <Link to={`/register-operation`}>
          Registre transações para ver um resumo da performance de suas ações
          aqui.
        </Link>
      )}
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
