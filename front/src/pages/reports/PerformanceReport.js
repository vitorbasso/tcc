import { useContext, useEffect, useState } from "react";
import Header from "../../components/header/Header";
import Money from "../../components/money/Money";
import baseStyles from "../../css/base.module.css";
import { getMoneyClass } from "../../utils/cssUtils";
import styles from "./PerformanceReport.module.css";
import { BsArrowDown, BsArrowUp } from "react-icons/bs";
import { moneyFormatter, percentFormatter } from "../../utils/formatterUtils";
import WalletContext from "../../context/wallet-context";
import AssetTable from "../../components/table/assets/AssetTable";
import StocksContext from "../../context/stock-context";
import { Link } from "react-router-dom";

const DAY = "day";
const WEEK = "week";
const MONTH = "month";
const YEAR = "year";
const IBOVESPA = "^BVSP";

function updateNavSelected(id) {
  document.querySelector(`#${id}`).classList.add(styles.selected);
}

function getWalletWorth(stocks, assets, period) {
  let value;
  switch (period) {
    case DAY:
      value = "lastClose";
      break;
    case WEEK:
      value = "lastWeekClose";
      break;
    case MONTH:
      value = "lastMonthClose";
      break;
    case YEAR:
      value = "lastYearClose";
      break;
    default:
      value = "currentValue";
  }
  return stocks
    .filter((stock) => stock.ticker !== IBOVESPA)
    .reduce((total, stock) => {
      return (
        total +
          stock[value] *
            assets.find((asset) => asset.stockSymbol === stock.ticker)
              ?.amount ?? 0
      );
    }, 0);
}

function getVariationStyle(variation) {
  return variation > 0
    ? [<BsArrowUp />, styles.green]
    : variation < 0
    ? [<BsArrowDown />, styles.red]
    : [null, ""];
}

function PerformanceReport() {
  const { wallet, fetchWallet } = useContext(WalletContext);
  const [variation, setVariation] = useState(0);
  const [selection, setSelection] = useState("dia");
  const { stocks, fetchStocks } = useContext(StocksContext);
  const [ibovVariation, setIbovVariation] = useState(0);
  useEffect(() => {
    fetchWallet();
    fetchStocks();
  }, [fetchWallet, fetchStocks]);
  useEffect(() => {
    window.scrollTo(0, 0);
  }, []);
  let assets = [];
  let paidForAssets = 0;
  if (wallet) {
    assets = wallet.stockAssets;
    paidForAssets = assets
      .filter((asset) => asset.amount > 0)
      .reduce((total, asset) => total + asset.amount * asset.averageCost, 0);
  }
  let walletWorth = 0;
  let walletVariationDay = 0;
  let walletVariationWeek = 0;
  let walletVariationMonth = 0;
  let walletVariationYear = 0;

  let ibov = {};
  let ibovVariationDay = 0;
  let ibovVariationWeek = 0;
  let ibovVariationMonth = 0;
  let ibovVariationYear = 0;

  if (stocks) {
    walletWorth = getWalletWorth(stocks, assets);
    const worthDay = getWalletWorth(stocks, assets, DAY);
    walletVariationDay = (walletWorth - worthDay) / walletWorth;
    const worthWeek = getWalletWorth(stocks, assets, WEEK);
    walletVariationWeek = (walletWorth - worthWeek) / walletWorth;
    const worthMonth = getWalletWorth(stocks, assets, MONTH);
    walletVariationMonth = (walletWorth - worthMonth) / walletWorth;
    const worthYear = getWalletWorth(stocks, assets, YEAR);
    walletVariationYear = (walletWorth - worthYear) / walletWorth;
    ibov = stocks.find((stock) => stock.ticker === IBOVESPA);
    ibovVariationDay =
      (ibov?.currentValue - ibov?.lastClose) / ibov?.currentValue;
    ibovVariationWeek =
      (ibov?.currentValue - ibov?.lastWeekClose) / ibov?.currentValue;
    ibovVariationMonth =
      (ibov?.currentValue - ibov?.lastMonthClose) / ibov?.currentValue;
    ibovVariationYear =
      (ibov?.currentValue - ibov?.lastYearClose) / ibov?.currentValue;
  }
  useEffect(() => {
    setVariation(walletVariationDay);
    setIbovVariation(ibovVariationDay);
  }, [walletVariationDay, ibovVariationDay]);
  function filter(filterBy) {
    const lastSelected = document.querySelector(`.${styles.selected}`);
    lastSelected.classList.remove(styles.selected);
    switch (filterBy) {
      case DAY:
        setVariation(walletVariationDay);
        setIbovVariation(ibovVariationDay);
        updateNavSelected(DAY);
        setSelection("dia");
        break;
      case WEEK:
        setVariation(walletVariationWeek);
        setIbovVariation(ibovVariationWeek);
        updateNavSelected(WEEK);
        setSelection("semana");
        break;
      case MONTH:
        setVariation(walletVariationMonth);
        setIbovVariation(ibovVariationMonth);
        updateNavSelected(MONTH);
        setSelection("mês");
        break;
      case YEAR:
        setVariation(walletVariationYear);
        setIbovVariation(ibovVariationYear);
        updateNavSelected(YEAR);
        setSelection("ano");
        break;
      default:
        console.error(`cannot filter by ${filterBy} `);
    }
  }
  const moneyClass = getMoneyClass(walletWorth);
  const [arrow, css] = getVariationStyle(variation);
  const ibovDiff = variation - ibovVariation;
  const [, ibovCss] = getVariationStyle(ibovDiff);
  const [difArrow, difCss] = getVariationStyle(walletWorth - paidForAssets);
  return (
    <div className={baseStyles.container}>
      <Header backButton>
        <h2>Desempenho</h2>
      </Header>
      <main>
        <nav className={styles.nav}>
          <ul>
            <li>
              <button
                id={DAY}
                className={styles.selected}
                onClick={filter.bind(this, DAY)}
              >
                dia
              </button>
            </li>
            <li>
              <button id={WEEK} onClick={filter.bind(this, WEEK)}>
                semana
              </button>
            </li>
            <li>
              <button id={MONTH} onClick={filter.bind(this, MONTH)}>
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
        {assets.length !== 0 && (
          <section className={styles.overall}>
            <Money
              value={walletWorth}
              className={`${styles.money} ${baseStyles[moneyClass]}`}
            />
            <div className={styles.info}>
              <p>Valor Pago {moneyFormatter.format(paidForAssets)}</p>
            </div>
            <div className={styles.info}>
              <p>
                Diferença{" "}
                <span className={difCss}>
                  {difArrow}{" "}
                  {moneyFormatter.format(walletWorth - paidForAssets)}
                </span>
              </p>
            </div>
            <div className={styles.info}>
              <p>variação {selection}</p>
            </div>
            <p className={`${styles.variation} ${css}`}>
              <span>
                {arrow} {percentFormatter.format(variation)}
              </span>
              <span>({moneyFormatter.format(walletWorth * variation)})</span>
            </p>
            <p className={styles.ibov}>
              <span>IBOV </span>
              <span className={ibovCss}>
                {percentFormatter.format(ibovDiff)}
              </span>
            </p>
          </section>
        )}
        {assets.length === 0 && (
          <section
            className={`${styles["empty-transaction-text"]} ${styles.overall}`}
          >
            <Link to={`/register-operation`}>
              Registre transações para ver a performance da sua carteira aqui.
            </Link>
          </section>
        )}
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
