import { useState } from "react";
import Header from "../../components/header/Header";
import Money from "../../components/money/Money";
import baseStyles from "../../css/base.module.css";
import { getMoneyClass } from "../../utils/cssUtils";
import styles from "./PerformanceReport.module.css";
import { BsArrowDown, BsArrowUp } from "react-icons/bs";
import { moneyFormatter, percentFormatter } from "../../utils/numberUtils";
import AssetTable from "../../components/asset/AssetTable";
import { MOCK_ASSETS } from "../../constants/mocks";

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

function PerformanceReport(props) {
  const [money, setMoney] = useState(534_549.85);
  const [variation, setVariation] = useState(0.036);
  const [ibov, setIbov] = useState(0.0163);
  function filter(filterBy) {
    const lastSelected = document.querySelector(`.${styles.selected}`);
    lastSelected.classList.remove(styles.selected);
    switch (filterBy) {
      case DAY:
        setMoney(15_246.46);
        setVariation(-0.004);
        setIbov(0.0001);
        updateNavSelected(DAY);
        break;
      case WEEK:
        setMoney(55_530.5);
        setVariation(0.005);
        setIbov(0.0005);
        updateNavSelected(WEEK);
        break;
      case MONTH:
        setMoney(534_549.85);
        setVariation(0.036);
        setIbov(0.0163);
        updateNavSelected(MONTH);
        break;
      case YEAR:
        setMoney(13_358_387.58);
        setVariation(0.1756);
        setIbov(0.0634);
        updateNavSelected(YEAR);
        break;
      default:
        console.error(`cannot filter by ${filterBy} `);
    }
  }
  const moneyClass = getMoneyClass(money);
  const [arrow, css] = getVariationStyle(variation);
  const ibovDiff = variation - ibov;
  const [, ibovCss] = getVariationStyle(ibovDiff);
  return (
    <div className={baseStyles.container}>
      <Header backButton>
        <h2>Desempenho</h2>
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
        <section className={styles.overall}>
          <Money
            value={money}
            className={`${styles.money} ${baseStyles[moneyClass]}`}
          />
          <p className={`${styles.variation} ${css}`}>
            <span>
              {arrow} {percentFormatter.format(variation)}
            </span>
            <span>({moneyFormatter.format(money * variation)})</span>
          </p>
          <p className={styles.ibov}>
            <span>IBOV </span>
            <span className={ibovCss}>{percentFormatter.format(ibovDiff)}</span>
          </p>
        </section>
        <section className={styles["section__assets"]}>
          <AssetTable
            walletTotalValue={money}
            assets={MOCK_ASSETS}
            className={styles["asset-table"]}
            caller={"/performance"}
          />
        </section>
      </main>
    </div>
  );
}

export default PerformanceReport;
