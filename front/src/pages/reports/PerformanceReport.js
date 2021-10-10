import { Fragment, useContext, useEffect, useState } from "react";
import Header from "../../components/header/Header";
import Money from "../../components/money/Money";
import baseStyles from "../../css/base.module.css";
import { getMoneyClass } from "../../utils/cssUtils";
import styles from "./PerformanceReport.module.css";
import { BsArrowDown, BsArrowUp } from "react-icons/bs";
import {
  moneyFormatter,
  percentFormatterWithoutSign,
} from "../../utils/formatterUtils";
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
  let lifetimeBalance = 0;
  if (wallet) {
    assets = wallet.stockAssets;
    paidForAssets = assets
      .filter((asset) => asset.amount > 0)
      .reduce((total, asset) => total + asset.amount * asset.averageCost, 0);
    lifetimeBalance =
      Number.parseFloat(paidForAssets.toFixed(2)) +
      assets
        .filter((asset) => asset.amount > 0)
        .reduce((total, asset) => total + asset.lifetimeBalance, 0);
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
    walletVariationDay = walletWorth / worthDay - 1;
    const worthWeek = getWalletWorth(stocks, assets, WEEK);
    walletVariationWeek = walletWorth / worthWeek - 1;
    const worthMonth = getWalletWorth(stocks, assets, MONTH);
    walletVariationMonth = walletWorth / worthMonth - 1;
    const worthYear = getWalletWorth(stocks, assets, YEAR);
    walletVariationYear = walletWorth / worthYear - 1;
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
  const profit = walletWorth - paidForAssets;
  const profitVariation = profit / paidForAssets;
  const [ibovArrow, ibovCss] = getVariationStyle(ibovDiff);
  const [difArrow, difCss] = getVariationStyle(walletWorth - paidForAssets);
  const [balanceArrow, balanceCss] = getVariationStyle(lifetimeBalance);
  return (
    <div className={baseStyles.container}>
      <Header backButton>
        <h2>Desempenho</h2>
      </Header>
      <main>
        {assets.length !== 0 && (
          <Fragment>
            <section className={styles.overall}>
              <Money
                value={walletWorth}
                className={`${styles.money} ${baseStyles[moneyClass]}`}
              />
              <div className={styles.info}>
                <p>Valor Pago {moneyFormatter.format(paidForAssets)}</p>
              </div>
              <div className={styles.info}>
                <p>Situação Atual </p>
              </div>
              <div className={`${difCss} ${styles.info}`}>
                <span>
                  {difArrow}{" "}
                  {percentFormatterWithoutSign.format(profitVariation)} (
                  {moneyFormatter.format(profit)})
                </span>
              </div>
              <div className={styles.info}>
                <p>Balanço Histórico </p>
              </div>
              <div className={`${styles.info}`}>
                <span className={balanceCss}>
                  {balanceArrow}
                  {moneyFormatter.format(lifetimeBalance)}
                </span>
              </div>
            </section>
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
            <section className={styles.overall}>
              <div className={styles.info}>Valor Fechamento</div>
              <div className={styles.info}>
                {moneyFormatter.format(walletWorth / (1 + variation))}
              </div>
              <div className={styles.info}>
                <p>variação {selection}</p>
              </div>
              <p className={`${styles.variation} ${css}`}>
                <span>
                  {arrow} {percentFormatterWithoutSign.format(variation)}
                </span>
                <span>
                  (
                  {moneyFormatter.format(
                    walletWorth - walletWorth / (1 + variation)
                  )}
                  )
                </span>
              </p>
              <div className={styles.info}>Comparação IBOV</div>
              <div className={`${styles.info} ${ibovCss}`}>
                <span>
                  {ibovArrow}
                  {percentFormatterWithoutSign.format(ibovDiff)}
                </span>
              </div>
            </section>
          </Fragment>
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
          <h3 className={styles["table-title"]}>Ações</h3>
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
