import { useContext, useEffect, useState } from "react";
import Header from "../../components/header/Header";
import Money from "../../components/money/Money";
import baseStyles from "../../css/base.module.css";
import { getMoneyClass } from "../../utils/cssUtils";
import styles from "./PerformanceReport.module.css";
import { BsArrowDown, BsArrowUp } from "react-icons/bs";
import { moneyFormatter, percentFormatter } from "../../utils/formatterUtils";
import useHttp from "../../hooks/useHttp";
import { STOCKS_URL } from "../../constants/paths";
import WalletContext from "../../context/wallet-context";
import AssetTable from "../../components/table/assets/AssetTable";

const DAY = "day";
const WEEK = "week";
const MONTH = "month";
const YEAR = "year";

function updateNavSelected(id) {
  document.querySelector(`#${id}`).classList.add(styles.selected);
}

function getVariationStyle(variation) {
  return variation > 0
    ? [<BsArrowUp />, styles.green]
    : variation < 0
    ? [<BsArrowDown />, styles.red]
    : [null, ""];
}

function PerformanceReport(props) {
  const { wallet, fetchWallet } = useContext(WalletContext);
  const { result: resultStock, sendRequest: sendRequestStock } = useHttp();
  const [ibov, setIbov] = useState(0.0163);
  useEffect(() => {
    fetchWallet();
  }, [fetchWallet]);
  let assets = [];
  let assetNames = "";
  let paidForAssets = 0;
  if (wallet) {
    assets = wallet.stockAssets;
    assetNames = assets
      .filter((asset) => asset.amount !== 0)
      .map((asset) => asset.stockSymbol)
      .join();
    paidForAssets = assets
      .filter((asset) => asset.amount > 0)
      .reduce((total, asset) => total + asset.amount * asset.averageCost, 0);
  }
  useEffect(() => {
    if (assetNames !== "")
      sendRequestStock({
        url: `${STOCKS_URL}?symbols=${assetNames}`,
      });
  }, [assetNames, sendRequestStock]);
  let walletWorth = 0;
  if (resultStock) {
    walletWorth = resultStock.reduce((total, asset) => {
      return (
        total +
        asset.currentValue *
          (assets.find(
            (ass) =>
              ass.stockSymbol.toUpperCase() === asset.ticker.toUpperCase()
          )?.amount ?? 0)
      );
    }, 0);
  }
  const variation =
    walletWorth !== 0 ? (walletWorth - paidForAssets) / walletWorth : 0;
  function filter(filterBy) {
    const lastSelected = document.querySelector(`.${styles.selected}`);
    lastSelected.classList.remove(styles.selected);
    switch (filterBy) {
      case DAY:
        setIbov(0.0001);
        updateNavSelected(DAY);
        break;
      case WEEK:
        setIbov(0.0005);
        updateNavSelected(WEEK);
        break;
      case MONTH:
        setIbov(0.0163);
        updateNavSelected(MONTH);
        break;
      case YEAR:
        setIbov(0.0634);
        updateNavSelected(YEAR);
        break;
      default:
        console.error(`cannot filter by ${filterBy} `);
    }
  }
  const moneyClass = getMoneyClass(walletWorth);
  const [arrow, css] = getVariationStyle(variation);
  const ibovDiff = variation - ibov;
  const [, ibovCss] = getVariationStyle(ibovDiff);
  return (
    <div className={baseStyles.container}>
      <Header backButton>
        <h2>Desempenho</h2>
      </Header>
      <main>
        <nav className={styles.nav}>
          <ul>
            <li>
              <button id={DAY} onClick={filter.bind(this, DAY)}>
                dia
              </button>
            </li>
            <li>
              <button id={WEEK} onClick={filter.bind(this, WEEK)}>
                semana
              </button>
            </li>
            <li>
              <button
                id={MONTH}
                className={styles.selected}
                onClick={filter.bind(this, MONTH)}
              >
                mês
              </button>
            </li>
            <li>
              <button id={YEAR} onClick={filter.bind(this, YEAR)}>
                ano
              </button>
            </li>
          </ul>
        </nav>
        <section className={styles.overall}>
          <Money
            value={walletWorth}
            className={`${styles.money} ${baseStyles[moneyClass]}`}
          />
          <p className={`${styles.variation} ${css}`}>
            <span>
              {arrow} {percentFormatter.format(variation)}
            </span>
            <span>({moneyFormatter.format(walletWorth * variation)})</span>
          </p>
          <p className={styles.ibov}>
            <span>IBOV </span>
            <span className={ibovCss}>{percentFormatter.format(ibovDiff)}</span>
          </p>
        </section>
        <section className={styles["section__assets"]}>
          <AssetTable
            assets={assets}
            className={styles["asset-table"]}
            caller={"/performance"}
          />
        </section>
      </main>
    </div>
  );
}

export default PerformanceReport;
