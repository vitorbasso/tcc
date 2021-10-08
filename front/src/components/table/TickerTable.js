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
import { BUY, SELL } from "../../constants/constants";
import { TRANSACTION_URL } from "../../constants/paths";
import useDeleteConfirmation from "../../hooks/useDeleteConfirmation";

function sortByDate(first, second, weight = 1) {
  const firstValue = first.transactionDate;
  const secondValue = second.transactionDate;
  const value =
    firstValue > secondValue ? -1 : firstValue < secondValue ? 1 : 0;
  return value * weight;
}

function sortByValue(first, second, weight = 1) {
  const firstValue = first.value;
  const secondValue = second.value;
  const value =
    firstValue > secondValue ? -1 : firstValue < secondValue ? 1 : 0;
  return value * weight;
}

function sortByAverageValue(first, second, weight = 1) {
  const firstValue = first.value / first.quantity;
  const secondValue = second.value / second.quantity;
  const value =
    firstValue > secondValue ? -1 : firstValue < secondValue ? 1 : 0;
  return value * weight;
}

const DATE = "date";
const DATE_INVERSE = "-date";
const VALUE = "value";
const VALUE_INVERSE = "-value";
const AVERAGE_VALUE = "averageValue";
const AVERAGE_VALUE_INVERSE = "-averageValue";

function getVariationStyle(variation) {
  return variation > 0 ? styles.green : variation < 0 ? styles.red : "";
}

function TickerTable(props) {
  const confirmDelete = useDeleteConfirmation();
  const [sortBy, setSortBy] = useState(DATE);
  const [transactionsToDisplay, setTransactionsToDisplay] = useState([]);
  const { stocks } = useContext(StocksContext);
  useEffect(() => {
    setTransactionsToDisplay(props.transactions);
  }, [props.transactions]);
  const transactions = transactionsToDisplay.sort((first, second) => {
    switch (sortBy) {
      case AVERAGE_VALUE:
        return sortByAverageValue(first, second);
      case AVERAGE_VALUE_INVERSE:
        return sortByAverageValue(first, second, -1);
      case VALUE:
        return sortByValue(first, second);
      case VALUE_INVERSE:
        return sortByValue(first, second, -1);
      case DATE_INVERSE:
        return sortByDate(first, second, -1);
      default:
        return sortByDate(first, second);
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

  function toggleSellTransactions(event) {
    if (event.currentTarget.checked)
      setTransactionsToDisplay((state) =>
        state.filter((transaction) => transaction.type !== SELL)
      );
    else
      setTransactionsToDisplay((state) => {
        const thereIsSellTransactions = state.find(
          (transaction) => transaction.type === BUY
        );
        if (thereIsSellTransactions) return props.transactions;
        else
          return props.transactions.filter(
            (transaction) => transaction.type === SELL
          );
      });
  }

  function toggleBuyTransactions(event) {
    if (event.currentTarget.checked)
      setTransactionsToDisplay((state) =>
        state.filter((transaction) => transaction.type !== BUY)
      );
    else
      setTransactionsToDisplay((state) => {
        const thereIsSellTransactions = state.find(
          (transaction) => transaction.type === SELL
        );
        if (thereIsSellTransactions) return props.transactions;
        else
          return props.transactions.filter(
            (transaction) => transaction.type === BUY
          );
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
      <label htmlFor="hide-sell">
        <input
          id="hide-sell"
          type="checkbox"
          onChange={toggleSellTransactions}
        />
        Esconder transações de venda
      </label>
      <br />
      <label htmlFor="hide-buy">
        <input id="hide-buy" type="checkbox" onChange={toggleBuyTransactions} />
        Esconder transações de compra
      </label>
      <div onClick={handleSorting}>
        <button className={styles.selected} data-sort={DATE} type="button">
          Data{" "}
          {sortBy === DATE ? (
            <BsArrowDown />
          ) : sortBy === DATE_INVERSE ? (
            <BsArrowUp />
          ) : (
            ""
          )}
        </button>
        <button data-sort={VALUE} type="button">
          Valor{" "}
          {sortBy === VALUE ? (
            <BsArrowDown />
          ) : sortBy === VALUE_INVERSE ? (
            <BsArrowUp />
          ) : (
            ""
          )}
        </button>
        <button data-sort={AVERAGE_VALUE} type="button">
          Avg{" "}
          {sortBy === AVERAGE_VALUE ? (
            <BsArrowDown />
          ) : sortBy === AVERAGE_VALUE_INVERSE ? (
            <BsArrowUp />
          ) : (
            ""
          )}
        </button>
        {/* <button className={styles.selected} data-sort={NAME} type="button">
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
        </button> */}
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
                <i
                  onClick={deleteHandler}
                  className={styles.red}
                  data-id={transaction.id}
                >
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
