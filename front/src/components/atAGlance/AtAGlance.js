import PieChart from "../piechart/PieChart";
import styles from "./AtAGlance.module.css";
import TopTickets from "./TopTickets";
import PieSelected from "../piechart/PieSelected";
import QuickSoldView from "./QuickSoldView";
import { useContext, useMemo, useState } from "react";
import WalletContext from "../../context/wallet-context";

function compareAssetValue(first, second) {
  const firstValue = first.amount * first.averageCost;
  const secondValue = second.amount * second.averageCost;
  if (firstValue > secondValue) return -1;
  else if (firstValue < secondValue) return 1;
  else return 0;
}

function AtAGlance(props) {
  const { wallet } = useContext(WalletContext);
  const [selectedAsset, setSelectedAsset] = useState({});
  const assetsMemo = useMemo(() => {
    if (wallet) {
      const sortedAssets = wallet.stockAssets
        .filter((asset) => asset.amount !== 0)
        .sort(compareAssetValue);
      const totalValue = sortedAssets.reduce((total, asset) => {
        return total + asset.amount * asset.averageCost;
      }, 0);
      const assets = sortedAssets.map((asset, index) => {
        return {
          label: asset.stockSymbol,
          value: asset.amount * asset.averageCost,
          asset: asset,
          index,
          percentage: (asset.amount * asset.averageCost) / totalValue,
        };
      });
      const restAsset = sortedAssets.slice(3)?.reduce(
          (assetRest, asset) => {
            const totalValue = asset.amount * asset.averageCost;
            const amount = assetRest.amount + asset.amount;
            const value = assetRest.value + totalValue;
            return {
              amount,
              value,
              averageCost: value / amount,
            };
          },
          { amount: 0, value: 0 }
        ),
        assetRest = {
          label: "Outros",
          value: restAsset.value,
          asset: restAsset,
          index: 3,
          percentage: restAsset.value / totalValue,
        };
      setSelectedAsset(assets[0]);
      return [...assets.slice(0, 3), assetRest];
    }
  }, [wallet]);
  const labels = assetsMemo.map((asset) => asset.label);
  const data = assetsMemo.map((asset) => asset.value);

  function chartClickHandler(selected) {
    setSelectedAsset(assetsMemo[selected[0]?.index ?? 0]);
  }

  return (
    <section className={`${styles.section} ${props.className}`}>
      <PieChart
        data={data}
        labels={labels}
        className={styles["pie-chart"]}
        onClick={chartClickHandler}
      />
      <TopTickets assets={assetsMemo.slice(0, 3)} />
      <PieSelected
        selected={selectedAsset}
        className={styles["pie-chart-selected"]}
      />
      <QuickSoldView />
    </section>
  );
}

export default AtAGlance;
