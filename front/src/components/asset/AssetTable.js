import styles from "./AssetTable.module.css";
import { numberFormatter, percentFormatter } from "../../utils/numberUtils";
import Money from "../money/Money";
import { Link } from "react-router-dom";

function AssetTable(props) {
  const totalValue = props.assets.reduce((total, asset) => {
    return (total += asset.amount > 0 ? asset.averageCost * asset.amount : 0);
  }, 0);
  return (
    <div className={`${props.className} ${styles.container}`}>
      {props.assets.map((asset) => {
        const value = asset.averageCost * asset.amount;
        if (asset.amount !== 0) {
          return (
            <Link
              key={asset.id}
              className={styles["no-style-link"]}
              to={{
                pathname: `performance/${asset.stockSymbol}`,
                state: {
                  caller: props.caller || "/",
                },
              }}
            >
              <table className={`${styles.table}`}>
                <thead className={styles["table-head"]}>
                  <tr>
                    <th>Ação</th>
                    <th>Qnt</th>
                    <th>Valor Médio</th>
                    <th>Total</th>
                    <th>%</th>
                  </tr>
                </thead>
                <tbody className={styles["table-body"]}>
                  <tr>
                    <td>{asset.stockSymbol}</td>
                    <td>{numberFormatter.format(asset.amount)}</td>
                    <td>
                      <Money value={asset.averageCost} />
                    </td>
                    <td>
                      <Money value={value} />
                    </td>
                    <td>
                      {asset.amount > 0 &&
                        percentFormatter.format(value / totalValue)}
                    </td>
                  </tr>
                </tbody>
              </table>
            </Link>
          );
        } else {
          return {};
        }
      })}
    </div>
  );
}

export default AssetTable;
