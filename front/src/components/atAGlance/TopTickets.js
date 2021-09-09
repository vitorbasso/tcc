import styles from "./TopTickets.module.css";
import baseStyles from "../../css/base.module.css";
import { BsArrowDown, BsArrowUp } from "react-icons/bs";
import { percentFormatter } from "../../utils/formatterUtils";
import { useContext } from "react";
import { Link } from "react-router-dom";
import StocksContext from "../../context/stock-context";

function TopTickets(props) {
  const { stocks } = useContext(StocksContext);
  return (
    <div className={styles["top-tickets"]}>
      {props.assets.length === 0 && (
        <Link to={`/register-operation`}>
          Registre transações para ver um resumo da performance de suas ações
          aqui.
        </Link>
      )}
      {props.assets.map((assetInfo, index) => {
        const stock = stocks.find(
          (stock) => stock.ticker === assetInfo.asset.stockSymbol
        );
        const colorCss = baseStyles[`chart-color-${index}`];
        assetInfo.asset.valueChange =
          stock?.currentValue / stock?.lastClose - 1;
        const [arrow, css] =
          assetInfo.asset.valueChange > 0
            ? [<BsArrowUp />, styles.green]
            : assetInfo.asset.valueChange < 0
            ? [<BsArrowDown />, styles.red]
            : [null, ""];
        return (
          <Link
            to={`performance/${assetInfo.asset.stockSymbol}`}
            key={`top-tickets-${assetInfo.asset.id}`}
            className={`${styles["value-change"]} ${css}`}
          >
            <span>
              <div className={`${styles["chart-legend"]} ${colorCss}`}></div>
              {arrow} {assetInfo.asset.stockSymbol}
            </span>
            <span>
              {percentFormatter.format(
                assetInfo.asset.valueChange &&
                  !Number.isNaN(assetInfo.asset.valueChange)
                  ? assetInfo.asset.valueChange
                  : 0
              )}
            </span>
          </Link>
        );
      })}
    </div>
  );
}

export default TopTickets;
