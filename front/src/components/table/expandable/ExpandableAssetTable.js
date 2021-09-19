import { useContext, useEffect, useState } from "react";
import { BsArrowDown, BsArrowUp } from "react-icons/bs";
import { Link } from "react-router-dom";
import StocksContext from "../../../context/stock-context";
import {
  abbreviateNumber,
  percentFormatterClean,
} from "../../../utils/formatterUtils";
import ReactTooltip from "react-tooltip";
import styles from "./ExpandableAssetTable.module.css";

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
  const firstValue = (firstStock - first.averageCost) * first.amount;
  const secondValue = (secondStock - second.averageCost) * second.amount;
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

function ExpandableAssetTable(props) {
  const [sortBy, setSortBy] = useState(NAME);
  const [assetsToDisplay, setAssetsToDisplay] = useState([]);
  const { stocks } = useContext(StocksContext);
  useEffect(() => {
    setAssetsToDisplay(props.assets);
  }, [props.assets]);
  const assets = assetsToDisplay.sort((first, second) => {
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

  if (props.assets?.length === 0) {
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
            <div className={styles.th} data-tip="Ação">
              Ação
            </div>
            <div className={styles.th} data-tip="Quantidade">
              QNT
            </div>
            <div className={styles.th} data-tip="Preço Médio (R$)">
              PM
            </div>
            <div className={styles.th} data-tip="Valor Atual (R$)">
              VA
            </div>
            <div className={styles.th} data-tip="Variação">
              %
            </div>
            <div className={styles.th} data-tip="Valorização (R$)">
              Val
            </div>
          </div>
        </div>
        <div className={styles.tbody}>
          {assets.map((asset) => {
            const stock = stocks.find(
              (stock) => stock.ticker === asset.stockSymbol
            );
            const currentValue = stock?.currentValue ?? 0;
            const variation =
              (currentValue - asset.averageCost) / asset.averageCost;
            const css = getVariationStyle(variation);
            const valorization =
              (currentValue - asset.averageCost) * asset.amount;
            return (
              <Link
                key={asset.id}
                to={{
                  pathname: `/performance/${asset.stockSymbol}`,
                  state: {
                    caller: props.caller || "/",
                  },
                }}
              >
                <div className={styles.tr}>
                  <div
                    className={`${styles["table-ticker-name"]} ${styles.td}`}
                  >
                    {asset.stockSymbol}
                  </div>
                  <div className={styles.td}>{asset.amount}</div>
                  <div className={styles.td}>
                    {abbreviateNumber(asset.averageCost)}
                  </div>
                  <div className={`${css} ${styles.td}`}>
                    {abbreviateNumber(currentValue)}
                  </div>
                  <div className={`${css} ${styles.td}`}>
                    {percentFormatterClean(variation)}
                  </div>
                  <div className={`${css} ${styles.td}`}>
                    {abbreviateNumber(valorization)}
                  </div>
                </div>
              </Link>
            );
          })}
        </div>
      </div>
    </div>
  );
}

export default ExpandableAssetTable;
