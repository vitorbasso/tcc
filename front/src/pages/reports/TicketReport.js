import { useCallback, useContext, useEffect, useState } from "react";
import { BsArrowDown, BsArrowUp, BsTrash } from "react-icons/bs";
import { useLocation, useParams, Redirect } from "react-router-dom";
import Header from "../../components/header/Header";
import Money from "../../components/money/Money";
import TickerTable from "../../components/table/ticker/TickerTable";
import { ASSET_URL } from "../../constants/paths";
import StocksContext from "../../context/stock-context";
import WalletContext from "../../context/wallet-context";
import baseStyles from "../../css/base.module.css";
import useDeleteConfirmation from "../../hooks/useDeleteConfirmation";
import useHttp from "../../hooks/useHttp";
import useLogout from "../../hooks/useLogout";
import { getMoneyClass } from "../../utils/cssUtils";
import {
  moneyFormatter,
  percentFormatterWithoutSign,
} from "../../utils/formatterUtils";
import styles from "./TicketReport.module.css";

const DAY = "day";
const WEEK = "week";
const MONTH = "month";
const YEAR = "year";

function updateNavSelected(id) {
  document.querySelector(`#${id}`)?.classList?.add(styles.selected);
}

function getVariationStyle(variation) {
  if (typeof variation !== "number") return [null, ""];
  return variation > 0
    ? [<BsArrowUp />, styles.green]
    : variation < 0
    ? [<BsArrowDown />, styles.red]
    : [null, ""];
}

function TicketReport() {
  const logout = useLogout();
  const confirmDelete = useDeleteConfirmation();
  const { wallet, fetchWallet } = useContext(WalletContext);
  const { stocks, fetchStocks } = useContext(StocksContext);
  const [deleted, setDeleted] = useState(false);
  const [variation, setVariation] = useState(0);
  const location = useLocation();
  const [sentRequest, setSentRequest] = useState(false);
  const { result, error, isLoading, sendRequest } = useHttp();
  const id = useParams().id.toUpperCase();

  useEffect(() => {
    window.scrollTo(0, 0);
  }, []);

  if (error?.status === 403) {
    logout();
  }

  const fetchTransactions = useCallback(() => {
    setSentRequest(true);
    sendRequest({
      url: `${ASSET_URL}/${id}`,
    });
  }, [id, sendRequest]);

  useEffect(() => {
    fetchWallet();
    fetchStocks();
    const asset = wallet.stockAssets.find((asset) => asset.stockSymbol === id);
    if (
      (!sentRequest ||
        (result && result.stockSymbol.toLowerCase() !== id.toLowerCase())) &&
      asset
    ) {
      fetchTransactions();
    }
  }, [
    fetchWallet,
    sendRequest,
    id,
    result,
    sentRequest,
    wallet.stockAssets,
    fetchStocks,
    fetchTransactions,
  ]);

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
  let currentTotalValue = 0;
  let asset;
  if (wallet) {
    asset = wallet.stockAssets.find((asset) => asset.stockSymbol === id);
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
      lifetimeBalance =
        Number.parseFloat(assetTotalValue.toFixed(2)) + asset.lifetimeBalance;
    }
  }
  if (stocks) {
    const stock = stocks.find((stock) => stock.ticker === id);
    if (stock) {
      currentValue = stock.currentValue;
      currentTotalValue = currentValue * amount;
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

  if (wallet && wallet.id !== -1 && !asset) {
    return <Redirect to={deleted ? "/" : "/not-found"} />;
  }

  function deleteHandler(event) {
    event.preventDefault();
    if (!id && wallet && wallet.id !== -1 && !asset) return;
    confirmDelete({
      title: `Deletar ${id}?`,
      message: `Tem certeza que deseja deletar a ação ${id} da sua carteira?`,
      url: `${ASSET_URL}/${id}`,
      onDelete: () => {
        setDeleted(true);
      },
    });
  }

  const profit = currentValue * amount - assetTotalValue;
  const profitVariation = profit / assetTotalValue;

  const moneyClass = getMoneyClass(currentTotalValue);

  function filter(filterBy) {
    const lastSelected = document.querySelector(`.${styles.selected}`);
    if (!lastSelected) return;
    lastSelected.classList.remove(styles.selected);
    switch (filterBy) {
      case DAY:
        setVariation(variationDay);
        updateNavSelected(DAY);
        break;
      case WEEK:
        setVariation(variationWeek);
        updateNavSelected(WEEK);
        break;
      case MONTH:
        setVariation(variationMonth);
        updateNavSelected(MONTH);
        break;
      case YEAR:
        setVariation(variationYear);
        updateNavSelected(YEAR);
        break;
      default:
        console.error(`cannot filter by ${filterBy} `);
    }
  }
  const [arrow, css] = getVariationStyle(variation);
  const [balanceArrow, balanceCss] = getVariationStyle(lifetimeBalance);

  const [difArrow, difCss] = getVariationStyle(profit);

  return (
    <div className={baseStyles.container}>
      <Header backButton caller={location.state?.caller || "/"}>
        <h2>Ação</h2>
      </Header>
      <main>
        <section className={styles.overview}>
          <span>{id}</span>
          <span>
            {percentFormatterWithoutSign.format(
              percentOfWallet && !Number.isNaN(percentOfWallet)
                ? percentOfWallet
                : 0
            )}
          </span>
          <span>
            <i
              onClick={deleteHandler}
              className={`${styles.red} ${styles.delete}`}
              data-id={id}
            >
              <BsTrash />
            </i>
          </span>
        </section>
        <div className={styles.main}>
          <Money
            value={currentTotalValue}
            className={`${baseStyles[moneyClass]}`}
          />
          <span className={`${styles.superscribed} ${difCss}`}>
            {difArrow} {percentFormatterWithoutSign.format(profitVariation)}
          </span>
        </div>
        <section className={styles.info}>
          <div>
            <p>Custo Total</p>
            <p>{moneyFormatter.format(assetTotalValue)}</p>
          </div>
          <div>
            <p>Custo Médio</p>
            <p>{moneyFormatter.format(averageValue)}</p>
          </div>
          <div>
            <p>Quantidade</p>
            <p>{Intl.NumberFormat("pt-BR").format(amount)}</p>
          </div>
        </section>
        <section className={styles.info}>
          <div>
            <p>Preço Atual</p>
            <p>{moneyFormatter.format(currentValue)}</p>
          </div>
          <div>
            <p>Valorização (%)</p>
            <p className={difCss}>
              {difArrow}
              {percentFormatterWithoutSign.format(profitVariation)}
            </p>
          </div>
          <div>
            <p>Valorização (R$)</p>
            <p className={difCss}>{moneyFormatter.format(profit)}</p>
          </div>
          <div>
            <p>Balanço Histórico </p>
            <p className={balanceCss}>
              {balanceArrow}
              {moneyFormatter.format(lifetimeBalance)}
            </p>
          </div>
        </section>
        <section className={styles.info}>
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
          <div>
            <p>Valor fechamento</p>
            <p>{moneyFormatter.format(currentValue / (1 + variation))}</p>
          </div>
          <div>
            <p>Variação Ticker (%)</p>
            <p className={css}>
              <span>{arrow}</span>
              <span>{percentFormatterWithoutSign.format(variation)}</span>
            </p>
          </div>
          <div>
            <span>Variação Ticker (R$)</span>
            <span className={css}>
              {moneyFormatter.format(
                currentValue - currentValue / (1 + variation)
              )}
            </span>
          </div>

          <div>
            <p>Variação Posição</p>
            <p className={css}>
              <span>{arrow}</span>
              <span>
                {moneyFormatter.format(
                  currentTotalValue - currentTotalValue / (1 + variation)
                )}
              </span>
            </p>
          </div>
        </section>
        <section>
          <h3 className={styles["table-title"]}>Transações</h3>
          <TickerTable
            transactions={transactions}
            symbol={id}
            onDelete={fetchTransactions}
          />
        </section>
      </main>
    </div>
  );
}

export default TicketReport;
