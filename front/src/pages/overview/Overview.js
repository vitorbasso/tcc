import PieChart from "../../components/piechart/PieChart";
import PieSelected from "../../components/piechart/PieSelected";
import Header from "../../components/header/Header";
import baseStyles from "../../css/base.module.css";
import styles from "./Overview.module.css";
import { numberFormatter } from "../../utils/numberUtils";
import Money from "../../components/money/Money";
import AssetTable from "../../components/asset/AssetTable";
import { MOCK_ASSETS } from "../../constants/mocks";
import useHttp from "../../hooks/useHttp";
import { useEffect } from "react";
import { WALLETS_URL } from "../../constants/paths";

function Overview(props) {
  const { result, sendRequest } = useHttp();

  useEffect(() => {
    sendRequest({
      url: WALLETS_URL,
    });
  }, [sendRequest]);
  let assets = [];
  if (result) {
    assets = result.stockAssets;
  }
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
            assets={assets}
            className={styles["asset-table"]}
            caller={"/overview"}
          />
        </section>
      </main>
    </div>
  );
}

export default Overview;
