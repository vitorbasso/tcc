import { useEffect, useState } from "react";
import { Doughnut } from "react-chartjs-2";

function PieChart(props) {
  const [animated, setAnimated] = useState(false);
  const colors = [
    "#6f69ac",
    "#ECD662",
    "#ea5455",
    "#95dac1",
    "#2d4059",
    "#fd6f96",
    "#0F52BA",
    "#ffd460",
    "#865439",
    "#f07b3f",
    "#57CC99",
  ];

  useEffect(() => {
    const timeout = setTimeout(() => {
      setAnimated(true);
    }, 900);
    return () => {
      clearTimeout(timeout);
    };
  }, []);

  return (
    <div className={props.className}>
      <Doughnut
        redraw={false}
        data={{
          labels: props.labels,
          datasets: [
            {
              label: "My First Dataset",
              data: props.data,
              backgroundColor: colors,
              cutout: "70%",
              locale: "pt-BR",
              borderWidth: 0,
            },
          ],
        }}
        options={{
          animation: !animated && {},
          plugins: {
            legend: {
              display: false,
            },
            tooltip: {
              enabled: false,
            },
          },
          onClick: (_, selected) => {
            props.onClick(selected);
          },
          onHover: (event, chartElement) => {
            event.native.target.style.cursor = chartElement[0]
              ? "pointer"
              : "default";
          },
        }}
      ></Doughnut>
    </div>
  );
}

export default PieChart;
