import { useState } from "react";
import { BsArrowDown, BsArrowUp } from "react-icons/bs";
import { useLocation, useParams } from "react-router-dom";
import Header from "../../components/header/Header";
import Money from "../../components/money/Money";
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

function TicketReport(props) {
  const [variation, setVariation] = useState(0.0242);
  const location = useLocation();
  const { id } = useParams();
  const money = 50_000;
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
      <Header backButton caller={location.state.caller || "/"}>
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
            {Intl.NumberFormat("pt-BR", { style: "percent" }).format(0.5)}
          </span>
        </section>
        <Money value={money} className={`${baseStyles[moneyClass]}`} />
        <section className={styles.info}>
          <div>
            <p>Qnt {Intl.NumberFormat("pt-BR").format(2_000)}</p>
            <p>
              <span>Preço médio </span>
              <span>{moneyFormatter.format(25)}</span>
            </p>
          </div>
          <div className={`${styles.variation} ${css}`}>
            <span>{arrow}</span>
            <span>{percentFormatter.format(variation)}</span>
            <span>({moneyFormatter.format(variation * money)})</span>
          </div>
        </section>
      </main>
    </div>
  );
}

export default TicketReport;
