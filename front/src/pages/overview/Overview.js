import PieChart from "../../components/piechart/PieChart";
import PieSelected from "../../components/piechart/PieSelected";
import Header from "../../components/header/Header";
import baseStyles from "../../css/base.module.css";
import styles from "./Overview.module.css";
import { numberFormatter } from "../../utils/formatterUtils";
import Money from "../../components/money/Money";
import { useContext, useEffect, useMemo, useState } from "react";
import WalletContext from "../../context/wallet-context";
import { Link } from "react-router-dom";
import ExpendableAssetTable from "../../components/table/expandable/ExpandableAssetTable";
import StocksContext from "../../context/stock-context";
import AssetTable from "../../components/table/assets/AssetTable";
import ExpandableAssetTable from "../../components/table/expandable/ExpandableAssetTable";

function compareAssetValue(first, second) {
  const firstValue = first.amount * first.averageCost;
  const secondValue = second.amount * second.averageCost;
  if (firstValue > secondValue) return -1;
  else if (firstValue < secondValue) return 1;
  else return 0;
}

function Overview() {
  const { wallet, fetchWallet } = useContext(WalletContext);
  const { fetchStocks } = useContext(StocksContext);
  const [selectedAsset, setSelectedAsset] = useState({});
  useEffect(() => {
    fetchWallet();
    fetchStocks();
  }, [fetchWallet, fetchStocks]);
  useEffect(() => {
    window.scrollTo(0, 0);
  }, []);
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
          link: `/${asset.stockSymbol}`,
          value: asset.amount * asset.averageCost,
          asset: asset,
          index,
          percentage:
            totalValue && totalValue !== 0 && !Number.isNaN(totalValue)
              ? (asset.amount * asset.averageCost) / totalValue
              : 0,
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
      return [...assets.slice(0, 9), assetRest].filter(
        (asset) => asset.value && asset.value !== 0 && asset.percentage
      );
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
          {data.length !== 0 && (
            <PieChart
              data={data}
              labels={labels}
              onClick={chartClickHandler}
              className={styles["pie-chart__pie-chart"]}
            />
          )}
          {data.length === 0 && (
            <div className={styles["pie-chart__empty-phrase"]}>
              <Link to={`/register-operation`}>
                Registre transações para ter uma visão geral de suas ações aqui.
              </Link>
            </div>
          )}
          <div className={styles["pie-chart__selected-info"]}>
            <PieSelected
              caller="/overview"
              selected={selectedAsset}
              className={styles["pie-chart__selected-legend"]}
            />
            {wallet.stockAssets.length !== 0 && (
              <div>
                <p>{numberFormatter.format(assetAmount ?? 0)}</p>
                <div>
                  <Money value={assetAverageCost} />
                </div>
                <div>
                  <Money value={assetTotalValue} />
                </div>
              </div>
            )}
          </div>
        </section>
        <section className={styles["section__assets"]}>
          <ExpandableAssetTable
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
