import { useContext, useEffect, useState } from "react";
import { BsArrowDown, BsArrowUp, BsTrash } from "react-icons/bs";
import StocksContext from "../../context/stock-context";
import {
  abbreviateNumber,
  dateFormatter,
  moneyFormatter,
} from "../../utils/formatterUtils";
import ReactTooltip from "react-tooltip";
import styles from "./TickerTable.module.css";
import { BUY } from "../../constants/constants";
import { TRANSACTION_URL } from "../../constants/paths";
import useDeleteConfirmation from "../../hooks/useDeleteConfirmation";

function sortByPercentage(first, second, stocks, weight = 1) {
  const firstStock =
    stocks.find((stock) => stock.ticker === first.stockSymbol)?.currentValue ??
    0;
  const secondStock =
    stocks.find((stock) => stock.ticker === second.stockSymbol)?.currentValue ??
    0;
  const firstValue = (firstStock - first.averageCost) / first.averageCost;
  const secondValue = (secondStock - second.averageCost) / second.averageCost;
  const value =
    firstValue > secondValue ? -1 : firstValue < secondValue ? 1 : 0;
  return value * weight;
}

function sortByValorization(first, second, stocks, weight = 1) {
  const firstStock =
    stocks.find((stock) => stock.ticker === first.stockSymbol)?.currentValue ??
    0;
  const secondStock =
    stocks.find((stock) => stock.ticker === second.stockSymbol)?.currentValue ??
    0;
  const firstValue = (firstStock - first.averageCost) * first.quantity;
  const secondValue = (secondStock - second.averageCost) * second.quantity;
  const value =
    firstValue > secondValue ? -1 : firstValue < secondValue ? 1 : 0;
  return value * weight;
}

function sortByName(first, second, weight = 1) {
  const firstName = first.stockSymbol;
  const secondName = second.stockSymbol;
  return firstName.localeCompare(secondName, "pt-BR") * weight;
}

const PERCENT = "%";
const PERCENT_INVERSE = "-%";
const NAME = "name";
const NAME_INVERSE = "-name";
const VALORIZATION = "averageValue";
const VALORIZATION_INVERSE = "-averageValue";

function getVariationStyle(variation) {
  return variation > 0 ? styles.green : variation < 0 ? styles.red : "";
}

function TickerTable(props) {
  const confirmDelete = useDeleteConfirmation();
  const [sortBy, setSortBy] = useState(NAME);
  const [transactionsToDisplay, setTransactionsToDisplay] = useState([]);
  const { stocks } = useContext(StocksContext);
  useEffect(() => {
    setTransactionsToDisplay(props.transactions);
  }, [props.transactions]);
  const transactions = transactionsToDisplay.sort((first, second) => {
    switch (sortBy) {
      case NAME_INVERSE:
        return sortByName(first, second, -1);
      case VALORIZATION:
        return sortByValorization(first, second, stocks);
      case VALORIZATION_INVERSE:
        return sortByValorization(first, second, stocks, -1);
      case PERCENT_INVERSE:
        return sortByPercentage(first, second, stocks, -1);
      case PERCENT:
        return sortByPercentage(first, second, stocks);
      default:
        return sortByName(first, second);
    }
  });
  function toggleZeroQuantityTransactions(event) {
    if (event.currentTarget.checked)
      setTransactionsToDisplay(
        props.transactions.filter((transaction) => transaction.quantity > 0)
      );
    else setTransactionsToDisplay(props.transactions);
  }

  function deleteHandler(event) {
    event.preventDefault();
    const transaction = transactions.find(
      (transaction) =>
        "" + transaction.id === event.target.closest("i").dataset.id
    );
    if (!transaction) return;
    confirmDelete({
      title: `Remover?`,
      message: `Tem certeza que deseja remover a transação de ${
        transaction.type === BUY ? "COMPRA" : "VENDA"
      } de ${transaction.quantity} por ${moneyFormatter.format(
        transaction.value / transaction.quantity
      )} cada do dia ${dateFormatter.format(
        new Date(transaction.transactionDate)
      )}?`,
      url: `${TRANSACTION_URL}/${transaction.id}`,
      onDelete: props?.onDelete,
    });
  }

  function handleSorting(event) {
    const btn = event.target.closest("button");
    if (!btn) return;
    document
      .querySelector(`.${styles.selected}`)
      .classList.remove(styles.selected);
    btn.classList.add(styles.selected);
    if (btn.dataset.sort !== sortBy) setSortBy(btn.dataset.sort);
    else setSortBy(`-${btn.dataset.sort}`);
  }

  if (props.transactions?.length === 0) {
    return [];
  }

  return (
    <div className={`${props.className} ${styles.container}`}>
      <label htmlFor="hide-zero">
        <span className={styles["hide-zero"]}>
          <input
            id="hide-zero"
            type="checkbox"
            onChange={toggleZeroQuantityTransactions}
          />
          Esconder ações com quantidade zero
        </span>
      </label>
      <div onClick={handleSorting}>
        <button className={styles.selected} data-sort={NAME} type="button">
          Ação{" "}
          {sortBy === NAME ? (
            <BsArrowUp />
          ) : sortBy === NAME_INVERSE ? (
            <BsArrowDown />
          ) : (
            ""
          )}
        </button>
        <button data-sort={PERCENT} type="button">
          %{" "}
          {sortBy === PERCENT ? (
            <BsArrowDown />
          ) : sortBy === PERCENT_INVERSE ? (
            <BsArrowUp />
          ) : (
            ""
          )}
        </button>
        <button data-sort={VALORIZATION} type="button">
          VAL{" "}
          {sortBy === VALORIZATION ? (
            <BsArrowDown />
          ) : sortBy === VALORIZATION_INVERSE ? (
            <BsArrowUp />
          ) : (
            ""
          )}
        </button>
      </div>
      <div className={styles.table}>
        <ReactTooltip />
        <div className={styles.thead}>
          <div className={styles.tr}>
            <div className={styles.th} data-tip="Compra ou Venda">
              C/V
            </div>
            <div className={styles.th} data-tip="Quantidade">
              QNT
            </div>
            <div className={styles.th} data-tip="Preço Médio (R$)">
              PM
            </div>
            <div className={styles.th} data-tip="Valor Atual (R$)">
              DATA
            </div>
          </div>
        </div>
        <div className={styles.tbody}>
          {transactions.map((transaction) => {
            const stock = stocks.find((stock) => stock.ticker === props.symbol);
            const currentValue = stock?.currentValue ?? 0;
            const variation =
              (currentValue - transaction.averageCost) /
              transaction.averageCost;
            const css = getVariationStyle(variation);
            return (
              <div key={transaction.id} className={styles.tr}>
                <div className={styles.td}>
                  {transaction.type === BUY ? "C" : "V"}
                </div>
                <div className={styles.td}>{transaction.quantity}</div>
                <div className={styles.td}>
                  {abbreviateNumber(transaction.value / transaction.quantity)}
                </div>
                <div className={`${css} ${styles.td}`}>
                  {dateFormatter.format(new Date(transaction.transactionDate))}
                </div>
                <i onClick={deleteHandler} data-id={transaction.id}>
                  <BsTrash />
                </i>
              </div>
            );
          })}
        </div>
      </div>
    </div>
  );
}

export default TickerTable;
