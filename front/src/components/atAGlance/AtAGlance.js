import React from "react";
import PieChart from "../piechart/PieChart";
import styles from "./AtAGlance.module.css";
import TopTickets from "./TopTickets";
import PieSelected from "../piechart/PieSelected";
import QuickSoldView from "./QuickSoldView";

function AtAGlance(props) {
  const sold = props.wallet
    ? props.wallet.withdrawn + props.wallet.withdrawnDaytrade
    : 0;
  const tax = props.tax ? props.tax.normalTax + props.tax.daytradeTax : 0;
  console.log(props.tax);
  console.log(tax);
  return (
    <section className={`${styles.section} ${props.className}`}>
      <PieChart className={styles["pie-chart"]} />
      <TopTickets />
      <PieSelected className={styles["pie-chart-selected"]} />
      <QuickSoldView sold={sold} tax={tax} />
    </section>
  );
}

export default AtAGlance;
