import PieChart from "../../components/piechart/PieChart";
import PieSelected from "../../components/piechart/PieSelected";
import Header from "../../components/header/Header";
import baseStyles from "../../css/base.module.css";
import styles from "./Overview.module.css";
import {
  moneyFormatter,
  numberFormatter,
  percentFormatter,
} from "../../utils/formatterUtils";
import Money from "../../components/money/Money";
import { useCallback, useContext, useEffect, useMemo, useState } from "react";
import WalletContext from "../../context/wallet-context";
import { Link } from "react-router-dom";
import StocksContext from "../../context/stock-context";
import TopTickets from "../../components/atAGlance/TopTickets";
import { getMoneyClass } from "../../utils/cssUtils";
import { BsArrowDown, BsArrowUp } from "react-icons/bs";

function getVariationStyle(variation) {
  return variation > 0
    ? [<BsArrowUp />, styles.green]
    : variation < 0
    ? [<BsArrowDown />, styles.red]
    : [null, ""];
}

function Overview() {
  const { wallet, fetchWallet } = useContext(WalletContext);
  const { stocks, fetchStocks } = useContext(StocksContext);
  const [selectedAsset, setSelectedAsset] = useState({});
  useEffect(() => {
    fetchWallet();
    fetchStocks();
  }, [fetchWallet, fetchStocks]);
  useEffect(() => {
    window.scrollTo(0, 0);
  }, []);

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
          currentPrice: stock?.currentValue ?? 0,
          lastPrice: (stock?.lastClose ?? 0) * asset.amount,
          asset: asset,
          index,
          percentage:
            totalValue && totalValue !== 0 && !Number.isNaN(totalValue)
              ? value / totalValue
              : 0,
        };
      });
      const restAsset = sortedAssets.slice(9)?.reduce(
          (assetRest, asset) => {
            const stock = stocks.find(
              (stock) => stock.ticker === asset.stockSymbol
            );
            const assetValue =
              asset.amount * (stock?.currentValue ?? asset.averageCost);
            const amount = assetRest.amount + asset.amount;
            const value = assetRest.value + assetValue;
            const lastPrice =
              assetRest.lastPrice + (stock?.lastClose ?? 0) * asset.amount;
            return {
              amount,
              value,
              currentPrice: value / amount,
              lastPrice,
            };
          },
          { amount: 0, value: 0, lastPrice: 0 }
        ),
        assetRest = {
          label: "Outros",
          link: "",
          value: restAsset.value,
          asset: restAsset,
          currentPrice: restAsset.currentPrice,
          lastPrice: restAsset.lastPrice,
          index: 9,
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
  }, [wallet, stocks, compareCallback]);
  const labels = assetsMemo.map((asset) => asset.label);
  const data = assetsMemo.map((asset) => asset.value);

  const assetAmount = selectedAsset?.asset?.amount;
  const assetAverageCost = selectedAsset?.currentPrice;
  const assetTotalValue = selectedAsset?.value;
  const walletValue = assetsMemo.reduce(
    (total, asset) => total + asset.value,
    0
  );
  const lastWalletValue = assetsMemo.reduce(
    (total, asset) => total + asset.lastPrice,
    0
  );
  const walletVariation = (walletValue - lastWalletValue) / lastWalletValue;
  const [walletArrow, walletCss] = getVariationStyle(walletVariation);
  const moneyStyle = getMoneyClass(walletValue);

  function chartClickHandler(selected) {
    setSelectedAsset(assetsMemo[selected[0]?.index ?? 0]);
  }

  return (
    <div className={baseStyles.container}>
      <Header backButton>
        <h2>Visão Geral</h2>
      </Header>
      <main>
        <Money className={baseStyles[moneyStyle]} value={walletValue} />
        {data.length !== 0 && (
          <section className={styles.overview}>
            <div className={walletCss}>
              <p>
                {walletArrow} {percentFormatter.format(walletVariation)}
              </p>
              <p>({moneyFormatter.format(walletValue - lastWalletValue)})</p>
            </div>
          </section>
        )}
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
                <p>
                  <Money value={assetAverageCost} />
                </p>
                <p>
                  <Money value={assetTotalValue} />
                </p>
              </div>
            )}
          </div>
          <TopTickets
            className={styles["top-tickets__container"]}
            assets={assetsMemo.slice(0, 9)}
            caller={"/overview"}
          />
        </section>
      </main>
    </div>
  );
}

export default Overview;
