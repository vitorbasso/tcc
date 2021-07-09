import styles from "./AssetTable.module.css";
import { numberFormatter, percentFormatter } from "../../utils/numberUtils";
import Money from "../money/Money";

function AssetTable(props) {
  return (
    <div className={`${props.className} ${styles.container}`}>
      {props.assets.map((asset) => {
        return (
          <table key={asset.ticker} className={`${styles.table}`}>
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
                <td>{asset.ticker}</td>
                <td>{numberFormatter.format(asset.quantity)}</td>
                <td>
                  <Money value={asset.averageValue} />
                </td>
                <td>
                  <Money value={asset.value} />
                </td>
                <td>
                  {percentFormatter.format(
                    asset.value / props.walletTotalValue
                  )}
                </td>
              </tr>
            </tbody>
          </table>
        );
      })}
    </div>
  );
}

export default AssetTable;
