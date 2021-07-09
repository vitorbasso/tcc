import React from "react";
import PieChart from "../piechart/PieChart";
import styles from "./AtAGlance.module.css";
import TopTickets from "./TopTickets";
import PieSelected from "../piechart/PieSelected";
import QuickSoldView from "./QuickSoldView";

function AtAGlance(props) {
  return (
    <section className={`${styles.section} ${props.className}`}>
      <PieChart className={styles["pie-chart"]} />
      <TopTickets />
      <PieSelected className={styles["pie-chart-selected"]} />
      <QuickSoldView />
    </section>
  );
}

export default AtAGlance;
