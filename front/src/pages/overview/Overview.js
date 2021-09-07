import PieChart from "../../components/piechart/PieChart";
import PieSelected from "../../components/piechart/PieSelected";
import Header from "../../components/header/Header";
import baseStyles from "../../css/base.module.css";
import styles from "./Overview.module.css";
import { numberFormatter } from "../../utils/formatterUtils";
import Money from "../../components/money/Money";
import { useContext, useEffect, useMemo, useState } from "react";
import WalletContext from "../../context/wallet-context";
import AssetTable from "../../components/table/assets/AssetTable";

function compareAssetValue(first, second) {
  const firstValue = first.amount * first.averageCost;
  const secondValue = second.amount * second.averageCost;
  if (firstValue > secondValue) return -1;
  else if (firstValue < secondValue) return 1;
  else return 0;
}

function Overview() {
  const { wallet, fetchWallet } = useContext(WalletContext);
  const [selectedAsset, setSelectedAsset] = useState({});
  useEffect(() => {
    fetchWallet();
  }, [fetchWallet]);
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
      const restAsset = sortedAssets.slice(9)?.reduce(
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
      return [...assets.slice(0, 9), assetRest];
    }
  }, [wallet]);
  const labels = assetsMemo.map((asset) => asset.label);
  const data = assetsMemo.map((asset) => asset.value);

  const assetAmount = selectedAsset?.asset?.amount;
  const assetAverageCost = selectedAsset?.asset?.averageCost;
  const assetTotalValue = assetAmount * assetAverageCost;

  function chartClickHandler(selected) {
    setSelectedAsset(assetsMemo[selected[0]?.index ?? 0]);
  }

  return (
    <div className={baseStyles.container}>
      <Header backButton>
        <h2>Visão Geral</h2>
      </Header>
      <main>
        <section className={styles["section__pie-chart"]}>
          <PieChart
            data={data}
            labels={labels}
            onClick={chartClickHandler}
            className={styles["pie-chart__pie-chart"]}
          />
          <div className={styles["pie-chart__selected-info"]}>
            <PieSelected
              caller="/overview"
              selected={selectedAsset}
              className={styles["pie-chart__selected-legend"]}
            />
            <div>
              <p>{numberFormatter.format(assetAmount)}</p>
              <div>
                <Money value={assetAverageCost} />
              </div>
              <div>
                <Money value={assetTotalValue} />
              </div>
            </div>
          </div>
        </section>
        <section className={styles["section__assets"]}>
          <AssetTable
            assets={wallet.stockAssets}
            className={styles["asset-table"]}
            caller={"/overview"}
          />
        </section>
      </main>
    </div>
  );
}

export default Overview;
