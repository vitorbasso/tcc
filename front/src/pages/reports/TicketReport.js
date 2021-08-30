import { useContext, useEffect, useState } from "react";
import { BsArrowDown, BsArrowUp } from "react-icons/bs";
import { useLocation, useParams } from "react-router-dom";
import Header from "../../components/header/Header";
import Money from "../../components/money/Money";
import TickerTable from "../../components/table/ticker/TickerTable";
import WalletContext from "../../context/wallet-context";
import baseStyles from "../../css/base.module.css";
import { getMoneyClass } from "../../utils/cssUtils";
import { moneyFormatter, percentFormatter } from "../../utils/numberUtils";
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
  const [variation, setVariation] = useState(0.0242);
  const location = useLocation();
  const id = useParams().id.toUpperCase();

  useEffect(() => {
    fetchWallet();
  }, [fetchWallet]);

  let averageValue = 0;
  let amount = 0;
  let money = 0;
  let percentOfWallet = 0;
  if (wallet) {
    const asset = wallet.stockAssets.find((asset) => asset.stockSymbol === id);
    if (asset) {
      money = asset.averageCost * asset.amount;
      amount = asset.amount;
      averageValue = asset.averageCost;
      percentOfWallet =
        money /
        wallet.stockAssets.reduce((total, asset) => {
          return (total +=
            asset.amount > 0 ? asset.averageCost * asset.amount : 0);
        }, 0);
    }
  }

  const moneyClass = getMoneyClass(money);

  function filter(filterBy) {
    const lastSelected = document.querySelector(`.${styles.selected}`);
    lastSelected.classList.remove(styles.selected);
    switch (filterBy) {
      case DAY:
        setVariation(-0.0021);
        updateNavSelected(DAY);
        break;
      case WEEK:
        setVariation(0.0098);
        updateNavSelected(WEEK);
        break;
      case MONTH:
        setVariation(0.0242);
        updateNavSelected(MONTH);
        break;
      case YEAR:
        setVariation(0.1876);
        updateNavSelected(YEAR);
        break;
      default:
        console.error(`cannot filter by ${filterBy} `);
    }
  }
  const [arrow, css] = getVariationStyle(variation);
  return (
    <div className={baseStyles.container}>
      <Header backButton caller={location.state?.caller || "/"}>
        <h2>Ticker</h2>
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
        <section className={styles.overview}>
          <span>{id}</span>
          <span>
            {Intl.NumberFormat("pt-BR", { style: "percent" }).format(
              percentOfWallet
            )}
          </span>
        </section>
        <Money value={money} className={`${baseStyles[moneyClass]}`} />
        <section className={styles.info}>
          <div>
            <p>Qnt {Intl.NumberFormat("pt-BR").format(amount)}</p>
            <p>
              <span>Preço médio </span>
              <span>{moneyFormatter.format(averageValue)}</span>
            </p>
          </div>
          <div className={`${styles.variation} ${css}`}>
            <span>{arrow}</span>
            <span>{percentFormatter.format(variation)}</span>
            <span>({moneyFormatter.format(variation * money)})</span>
          </div>
        </section>
        <section>
          <TickerTable assets={wallet.stockAssets} />
        </section>
      </main>
    </div>
  );
}

export default TicketReport;
