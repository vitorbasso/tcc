import PieChart from "../../components/piechart/PieChart";
import PieSelected from "../../components/piechart/PieSelected";
import Header from "../../components/header/Header";
import baseStyles from "../../css/base.module.css";
import styles from "./Overview.module.css";
import { numberFormatter } from "../../utils/formatterUtils";
import Money from "../../components/money/Money";
import { useContext, useEffect } from "react";
import WalletContext from "../../context/wallet-context";
import AssetTable from "../../components/table/assets/AssetTable";

function Overview() {
  const { wallet, fetchWallet } = useContext(WalletContext);

  useEffect(() => {
    fetchWallet();
  }, [fetchWallet]);
  let assets = [];
  if (wallet) {
    assets = wallet.stockAssets;
  }
  const assetAmount = assets[0]?.amount;
  const assetAverageCost = assets[0]?.averageCost;
  const assetTotalValue = assetAmount * assetAverageCost;

  return (
    <div className={baseStyles.container}>
      <Header backButton>
        <h2>Visão Geral</h2>
      </Header>
      <main>
        <section className={styles["section__pie-chart"]}>
          <PieChart className={styles["pie-chart__pie-chart"]} />
          <div className={styles["pie-chart__selected-info"]}>
            <PieSelected
              caller="/overview"
              className={styles["pie-chart__selected-legend"]}
            />
            <div>
              <p>{numberFormatter.format(assetAmount)}</p>
              <div>
                <Money value={assetAverageCost} />
              </div>
              <div>
                <Money value={assetTotalValue} />
              </div>
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
