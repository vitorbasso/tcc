import { useContext } from "react";
import { Link } from "react-router-dom";
import WalletContext from "../../context/wallet-context";
import { percentFormatter } from "../../utils/numberUtils";
import styles from "./PieSelected.module.css";

function PieSelected(props) {
  const { wallet } = useContext(WalletContext);
  const assetValues = wallet.stockAssets
    .filter((asset) => asset.amount > 0)
    .map((asset) => ({
      id: asset.id,
      value: asset.amount * asset.averageCost,
    }))
    .sort((first, second) => {
      if (first.value > second.value) return -1;
      else if (first.value < second.value) return 1;
      else return 0;
    });
  const walletTotalValue = assetValues.reduce(
    (total, asset) => (total += asset.value),
    0
  );
  const topTicker = wallet.stockAssets.find(
    (asset) => asset.id === assetValues?.[0]?.id
  );
  const link = topTicker ? `performance/${topTicker.stockSymbol}` : "";
  const formatterOptions = Object.assign(percentFormatter.resolvedOptions(), {
    signDisplay: "never",
  });
  const percentFormatterWithoutSign = Intl.NumberFormat(
    "pt-BR",
    formatterOptions
  );
  const percentOfWallet = percentFormatterWithoutSign.format(
    topTicker && walletTotalValue !== 0
      ? (topTicker.amount * topTicker.averageCost) / walletTotalValue
      : 0
  );
  const bar = `${styles.bar} ${styles["rebeccapurple"]}`;
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
      {topTicker && <p>{topTicker.stockSymbol}</p>}
    </Link>
  );
}

export default PieSelected;
