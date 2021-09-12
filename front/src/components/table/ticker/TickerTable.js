import styles from "./TickerTable.module.css";
import {
  dateFormatter,
  moneyFormatter,
  numberFormatter,
} from "../../../utils/formatterUtils";
import Money from "../../money/Money";
import { useEffect, useState } from "react";
import { BsArrowDown, BsArrowUp, BsTrash } from "react-icons/bs";
import baseStyles from "../../../css/base.module.css";
import { TRANSACTION_URL } from "../../../constants/paths";
import useDeleteConfirmation from "../../../hooks/useDeleteConfirmation";

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

function TickerTable(props) {
  const [sortBy, setSortBy] = useState(DATE);
  const [transactionsToDisplay, setTransactionsToDisplay] = useState([]);
  const confirmDelete = useDeleteConfirmation();
  useEffect(() => {
    setTransactionsToDisplay(props.transactions);
  }, [props.transactions]);
  const assets = transactionsToDisplay.sort((first, second) => {
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
  function toggleSellTransactions(event) {
    if (event.currentTarget.checked)
      setTransactionsToDisplay((state) =>
        state.filter((transaction) => transaction.type !== "SELL")
      );
    else
      setTransactionsToDisplay((state) => {
        const thereIsSellTransactions = state.find(
          (transaction) => transaction.type === "BUY"
        );
        if (thereIsSellTransactions) return props.transactions;
        else
          return props.transactions.filter(
            (transaction) => transaction.type === "SELL"
          );
      });
  }

  function toggleBuyTransactions(event) {
    if (event.currentTarget.checked)
      setTransactionsToDisplay((state) =>
        state.filter((transaction) => transaction.type !== "BUY")
      );
    else
      setTransactionsToDisplay((state) => {
        const thereIsSellTransactions = state.find(
          (transaction) => transaction.type === "SELL"
        );
        if (thereIsSellTransactions) return props.transactions;
        else
          return props.transactions.filter(
            (transaction) => transaction.type === "BUY"
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

  function deleteHandler(event) {
    event.preventDefault();
    const transaction = assets.find(
      (transaction) =>
        "" + transaction.id === event.target.closest("i").dataset.id
    );
    if (!transaction) return;
    confirmDelete({
      title: `Remover?`,
      message: `Tem certeza que deseja remover a transação de ${
        transaction.type === "BUY" ? "COMPRA" : "VENDA"
      } de ${transaction.quantity} por ${moneyFormatter.format(
        transaction.value / transaction.quantity
      )} cada do dia ${dateFormatter.format(
        new Date(transaction.transactionDate)
      )}?`,
      url: `${TRANSACTION_URL}/${transaction.id}`,
      onDelete: props?.onDelete,
    });
  }

  if (assets.length === 0) {
    return [];
  }

  return (
    <div className={`${props.className} ${baseStyles.table}`}>
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
      </div>
      {assets.map((transaction) => {
        const type = transaction.type === "BUY" ? "Compra" : "Venda";
        return (
          <table key={transaction.id}>
            <thead>
              <tr>
                <th>Tipo</th>
                <th>Qnt</th>
                <th>Valor Médio</th>
                <th>Valor</th>
                <th>Data</th>
              </tr>
            </thead>
            <tbody>
              <tr>
                <td className={baseStyles["table-title"]}>
                  {type}{" "}
                  <i onClick={deleteHandler} data-id={transaction.id}>
                    <BsTrash />
                  </i>
                </td>
                <td>{numberFormatter.format(transaction.quantity)}</td>
                <td>
                  <Money value={transaction.value / transaction.quantity} />
                </td>
                <td>
                  <Money value={transaction.value} />
                </td>
                <td>
                  {dateFormatter.format(new Date(transaction.transactionDate))}
                </td>
              </tr>
            </tbody>
          </table>
        );
      })}
    </div>
  );
}

export default TickerTable;
