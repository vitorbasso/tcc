import PieChart from "../../components/piechart/PieChart";
import PieSelected from "../../components/piechart/PieSelected";
import Header from "../../components/header/Header";
import baseStyles from "../../css/base.module.css";
import styles from "./Overview.module.css";
import { numberFormatter } from "../../utils/numberUtils";
import Money from "../../components/money/Money";
import AssetTable from "../../components/asset/AssetTable";
import { MOCK_ASSETS } from "../../constants/mocks";

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
            caller={"/overview"}
          />
        </section>
      </main>
    </div>
  );
}

export default Overview;
