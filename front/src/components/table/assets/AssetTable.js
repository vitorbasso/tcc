import styles from "./AssetTable.module.css";
import baseStyles from "../../../css/base.module.css";
import {
  numberFormatter,
  percentFormatterWithoutSign,
} from "../../../utils/formatterUtils";
import Money from "../../money/Money";
import { Link } from "react-router-dom";
import { useContext, useEffect, useState } from "react";
import { BsArrowDown, BsArrowUp } from "react-icons/bs";
import StocksContext from "../../../context/stock-context";

function sortByPercentage(first, second, weight = 1) {
  const firstValue = first.amount * first.averageCost;
  const secondValue = second.amount * second.averageCost;
  const value =
    firstValue > secondValue ? -1 : firstValue < secondValue ? 1 : 0;
  return value * weight;
}

function sortByAverageValue(first, second, weight = 1) {
  const firstValue = first.averageCost;
  const secondValue = second.averageCost;
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
const AVERAGE_VALUE = "averageValue";
const AVERAGE_VALUE_INVERSE = "-averageValue";

function AssetTable(props) {
  const [sortBy, setSortBy] = useState(PERCENT);
  const [assetsToDisplay, setAssetsToDisplay] = useState([]);
  const { stocks } = useContext(StocksContext);
  useEffect(() => {
    setAssetsToDisplay(props.assets);
  }, [props.assets]);
  const assets = assetsToDisplay.sort((first, second) => {
    switch (sortBy) {
      case NAME:
        return sortByName(first, second);
      case NAME_INVERSE:
        return sortByName(first, second, -1);
      case AVERAGE_VALUE:
        return sortByAverageValue(first, second);
      case AVERAGE_VALUE_INVERSE:
        return sortByAverageValue(first, second, -1);
      case PERCENT_INVERSE:
        return sortByPercentage(first, second, -1);
      default:
        return sortByPercentage(first, second);
    }
  });
  const totalValue = props.assets
    .filter((asset) => asset.amount > 0)
    .reduce((total, asset) => {
      return (total += asset.averageCost * asset.amount);
    }, 0);
  function toggleZeroQuantityTransactions(event) {
    if (event.currentTarget.checked)
      setAssetsToDisplay(props.assets.filter((asset) => asset.amount > 0));
    else setAssetsToDisplay(props.assets);
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

  return (
    <div className={`${props.className} ${baseStyles.table}`}>
      <label htmlFor="hide-zero">
        <input
          id="hide-zero"
          type="checkbox"
          onChange={toggleZeroQuantityTransactions}
        />
        Esconder ações com quantidade zero
      </label>
      <div onClick={handleSorting}>
        <button className={styles.selected} data-sort={PERCENT} type="button">
          %{" "}
          {sortBy === PERCENT ? (
            <BsArrowDown />
          ) : sortBy === PERCENT_INVERSE ? (
            <BsArrowUp />
          ) : (
            ""
          )}
        </button>
        <button data-sort={NAME} type="button">
          Nome{" "}
          {sortBy === NAME ? (
            <BsArrowUp />
          ) : sortBy === NAME_INVERSE ? (
            <BsArrowDown />
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
      {assets.map((asset) => {
        const stock = stocks.find(
          (stock) => stock.ticker === asset.stockSymbol
        );
        const currentValue = stock?.currentValue ?? 0;
        const value = asset.averageCost * asset.amount;
        return (
          <Link
            key={asset.id}
            to={{
              pathname: `performance/${asset.stockSymbol}`,
              state: {
                caller: props.caller || "/",
              },
            }}
          >
            <table>
              <thead>
                <tr>
                  <th>Ação</th>
                  <th>Qnt</th>
                  <th>Valor Pago</th>
                  <th>Valor atual</th>
                  <th>Valor Total</th>
                  <th>%</th>
                </tr>
              </thead>
              <tbody>
                <tr>
                  <td>{asset.stockSymbol}</td>
                  <td>{numberFormatter.format(asset.amount)}</td>
                  <td>
                    <Money value={asset.averageCost} />
                  </td>
                  <td>
                    <Money value={currentValue} />
                  </td>
                  <td>
                    <Money value={value} />
                  </td>
                  <td>
                    {percentFormatterWithoutSign.format(
                      totalValue !== 0 ? value / totalValue : 0
                    )}
                  </td>
                </tr>
              </tbody>
            </table>
          </Link>
        );
      })}
    </div>
  );
}

export default AssetTable;
