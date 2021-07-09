import styles from "./PieChart.module.css";

function PieChart(props) {
  return <div className={`${styles.fake} ${props.className}`}></div>;
}

export default PieChart;
