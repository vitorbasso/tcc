import PieChart from "../../components/piechart/PieChart";
import PieSelected from "../../components/piechart/PieSelected";
import Header from "../../components/header/Header";
import baseStyles from "../../css/base.module.css";
import styles from "./Overview.module.css";
import { numberFormatter } from "../../utils/numberUtils";
import Money from "../../components/money/Money";
import AssetTable from "../../components/asset/AssetTable";

const MOCK_ASSETS = [
  {
    ticker: "petr4",
    quantity: 2000,
    averageValue: 25.0,
    value: 50000,
  },
  {
    ticker: "vale3",
    quantity: 1000,
    averageValue: 60.0,
    value: 60000,
  },
  {
    ticker: "meli34",
    quantity: 500,
    averageValue: 20.15,
    value: 10075,
  },
  {
    ticker: "b2w",
    quantity: 250,
    averageValue: 15,
    value: 3750,
  },
  {
    ticker: "bbas3",
    quantity: 250,
    averageValue: 30.5,
    value: 7625,
  },
];

function Overview(props) {
  const walletTotalValue = MOCK_ASSETS.reduce(
    (total, asset) => (total += asset.value),
    0
  );
  return (
    <div className={baseStyles.container}>
      <Header backButton>
        <h2>Visão Geral</h2>
      </Header>
      <main>
        <section className={styles["section__pie-chart"]}>
          <PieChart className={styles["pie-chart__pie-chart"]} />
          <div className={styles["pie-chart__selected-info"]}>
            <PieSelected className={styles["pie-chart__selected-legend"]} />
            <div>
              <p>{numberFormatter.format(2_000)}</p>
              <p>
                <Money value={25} />
              </p>
              <p>
                <Money value={50_000} />
              </p>
            </div>
          </div>
        </section>
        <section className={styles["section__assets"]}>
          <AssetTable
            walletTotalValue={walletTotalValue}
            assets={MOCK_ASSETS}
            className={styles["asset-table"]}
          />
        </section>
      </main>
    </div>
  );
}

export default Overview;
