import PieChart from "../piechart/PieChart";
import styles from "./AtAGlance.module.css";
import TopTickets from "./TopTickets";
import PieSelected from "../piechart/PieSelected";
import QuickSoldView from "./QuickSoldView";
import { useCallback, useContext, useMemo, useState } from "react";
import WalletContext from "../../context/wallet-context";
import StocksContext from "../../context/stock-context";

function AtAGlance(props) {
  const { wallet } = useContext(WalletContext);
  const { stocks } = useContext(StocksContext);
  const [selectedAsset, setSelectedAsset] = useState({});
  const compareCallback = useCallback(
    (first, second) => {
      const stock1 = stocks.find((stock) => stock.ticker === first.stockSymbol);
      const stock2 = stocks.find(
        (stock) => stock.ticker === second.stockSymbol
      );
      const firstValue =
        first.amount * (stock1?.currentValue ?? first.averageCost);
      const secondValue =
        second.amount * (stock2?.currentValue ?? second.averageCost);
      if (firstValue > secondValue) return -1;
      else if (firstValue < secondValue) return 1;
      else return 0;
    },
    [stocks]
  );
  const assetsMemo = useMemo(() => {
    if (wallet) {
      const sortedAssets = wallet.stockAssets
        .filter((asset) => asset.amount !== 0)
        .sort(compareCallback);
      const totalValue = sortedAssets.reduce((total, asset) => {
        const stock = stocks.find(
          (stock) => stock.ticker === asset.stockSymbol
        );
        return (
          total + asset.amount * (stock?.currentValue ?? asset.averageCost)
        );
      }, 0);
      const assets = sortedAssets.map((asset, index) => {
        const stock = stocks.find(
          (stock) => stock.ticker === asset.stockSymbol
        );
        const value = asset.amount * (stock?.currentValue ?? asset.averageCost);
        return {
          label: asset.stockSymbol,
          link: `/${asset.stockSymbol}`,
          value: value,
          asset: asset,
          index,
          percentage:
            totalValue && totalValue !== 0 && !Number.isNaN(totalValue)
              ? value / totalValue
              : 0,
        };
      });
      const restAsset = sortedAssets.slice(3)?.reduce(
          (assetRest, asset) => {
            const stock = stocks.find(
              (stock) => stock.ticker === asset.stockSymbol
            );
            const assetValue =
              asset.amount * (stock?.currentValue ?? asset.averageCost);
            const amount = assetRest.amount + asset.amount;
            const value = assetRest.value + assetValue;
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
          link: "",
          value: restAsset.value,
          asset: restAsset,
          index: 3,
          percentage:
            totalValue && totalValue !== 0 && !Number.isNaN(totalValue)
              ? restAsset.value / totalValue
              : 0,
        };
      setSelectedAsset(assets[0]);
      return [...assets.slice(0, 3), assetRest].filter(
        (asset) => asset.value && asset.value !== 0 && asset.percentage
      );
    }
  }, [wallet, stocks, compareCallback]);
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
