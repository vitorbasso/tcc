import { useContext, useEffect, useState } from "react";
import { BsArrowDown, BsArrowUp } from "react-icons/bs";
import { useLocation, useParams } from "react-router-dom";
import Header from "../../components/header/Header";
import Money from "../../components/money/Money";
import TickerTable from "../../components/table/ticker/TickerTable";
import { ASSET_URL } from "../../constants/paths";
import StocksContext from "../../context/stock-context";
import WalletContext from "../../context/wallet-context";
import baseStyles from "../../css/base.module.css";
import useHttp from "../../hooks/useHttp";
import { getMoneyClass } from "../../utils/cssUtils";
import {
  moneyFormatter,
  percentFormatter,
  percentFormatterWithoutSign,
} from "../../utils/formatterUtils";
import styles from "./TicketReport.module.css";

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

function TicketReport() {
  const { wallet, fetchWallet } = useContext(WalletContext);
  const { stocks, fetchStocks } = useContext(StocksContext);
  const [variation, setVariation] = useState(0);
  const [selection, setSelection] = useState("dia");
  const location = useLocation();
  const [sentRequest, setSentRequest] = useState(false);
  const { result, error, isLoading, sendRequest } = useHttp();
  const id = useParams().id.toUpperCase();

  useEffect(() => {
    fetchWallet();
    fetchStocks();
    if (
      !sentRequest ||
      (result && result.stockSymbol.toLowerCase() !== id.toLowerCase())
    ) {
      setSentRequest(true);
      sendRequest({
        url: `${ASSET_URL}/${id}`,
      });
    }
  }, [fetchWallet, sendRequest, id, result, sentRequest, fetchStocks]);

  if (!isLoading && error && !result) setSentRequest(false);

  let averageValue = 0;
  let amount = 0;
  let assetTotalValue = 0;
  let percentOfWallet = 0;
  let lifetimeBalance = 0;
  let variationDay = 0;
  let variationWeek = 0;
  let variationMonth = 0;
  let variationYear = 0;
  let currentValue = 0;
  if (wallet) {
    const asset = wallet.stockAssets.find((asset) => asset.stockSymbol === id);
    if (asset) {
      assetTotalValue = asset.averageCost * asset.amount;
      amount = asset.amount;
      averageValue = asset.averageCost;
      percentOfWallet =
        assetTotalValue /
        wallet.stockAssets.reduce((total, asset) => {
          return (total +=
            asset.amount > 0 ? asset.averageCost * asset.amount : 0);
        }, 0);
      lifetimeBalance = asset.lifetimeBalance;
    }
  }
  if (stocks) {
    const stock = stocks.find((stock) => stock.ticker === id);
    if (stock) {
      currentValue = stock.currentValue;
      variationDay = currentValue / stock.lastClose - 1;
      variationWeek = currentValue / stock.lastWeekClose - 1;
      variationMonth = currentValue / stock.lastMonthClose - 1;
      variationYear = currentValue / stock.lastYearClose - 1;
    }
  }
  let transactions = [];
  if (result) {
    transactions = result.transactions;
  }

  useEffect(() => {
    setVariation(variationDay);
    updateNavSelected(DAY);
  }, [variationDay]);

  const profit = currentValue * amount - assetTotalValue;

  const moneyClass = getMoneyClass(currentValue);

  function filter(filterBy) {
    const lastSelected = document.querySelector(`.${styles.selected}`);
    lastSelected.classList.remove(styles.selected);
    switch (filterBy) {
      case DAY:
        setVariation(variationDay);
        updateNavSelected(DAY);
        setSelection("dia");
        break;
      case WEEK:
        setVariation(variationWeek);
        updateNavSelected(WEEK);
        setSelection("semana");
        break;
      case MONTH:
        setVariation(variationMonth);
        updateNavSelected(MONTH);
        setSelection("mês");
        break;
      case YEAR:
        setVariation(variationYear);
        updateNavSelected(YEAR);
        setSelection("ano");
        break;
      default:
        console.error(`cannot filter by ${filterBy} `);
    }
  }
  const [arrow, css] = getVariationStyle(variation);

  const [difArrow, difCss] = getVariationStyle(profit);

  return (
    <div className={baseStyles.container}>
      <Header backButton caller={location.state?.caller || "/"}>
        <h2>Ticker</h2>
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
        <section className={styles.overview}>
          <span>{id}</span>
          <span>{percentFormatterWithoutSign.format(percentOfWallet)}</span>
        </section>
        <Money value={currentValue} className={`${baseStyles[moneyClass]}`} />
        <section className={styles.info}>
          <div>
            <p>Qnt {Intl.NumberFormat("pt-BR").format(amount)}</p>
            <p>
              <span>Preço médio </span>
              <span>{moneyFormatter.format(averageValue)}</span>
            </p>
          </div>
          <div>
            <p>Valor Pago {moneyFormatter.format(assetTotalValue)}</p>
          </div>
          <div>
            <p>Valor Atual {moneyFormatter.format(currentValue * amount)}</p>
          </div>
          <div>
            <p>
              Diferença{" "}
              <span className={difCss}>
                {difArrow}
                {moneyFormatter.format(profit)}
              </span>
            </p>
          </div>
          <div>
            <p>Balanço Total {moneyFormatter.format(lifetimeBalance)}</p>
          </div>
          <div>
            <p>variação {selection}</p>
          </div>
          <div className={`${styles.variation} ${css}`}>
            <span>{arrow}</span>
            <span>{percentFormatter.format(variation)}</span>
            <span>
              (
              {moneyFormatter.format(
                currentValue - currentValue / (1 + variation)
              )}
              )
            </span>
          </div>
        </section>
        <section>
          <TickerTable transactions={transactions} />
        </section>
      </main>
    </div>
  );
}

export default TicketReport;
