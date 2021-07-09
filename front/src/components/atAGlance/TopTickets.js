import styles from "./TopTickets.module.css";
import { BsArrowDown, BsArrowUp } from "react-icons/bs";
import { percentFormatter } from "../../utils/numberUtils";

function TopTickets() {
  const tickets = [
    { id: "t1", ticket: "petr4", valueChange: 0.0235 },
    { id: "t2", ticket: "mglu", valueChange: 0.015 },
    { id: "t3", ticket: "vale3", valueChange: -0.0067 },
  ];
  return (
    <div className={styles["top-tickets"]}>
      {tickets.map((ticket) => {
        const [arrow, css] =
          ticket.valueChange > 0
            ? [<BsArrowUp />, styles.green]
            : ticket.valueChange < 0
            ? [<BsArrowDown />, styles.red]
            : [null, ""];
        return (
          <p key={ticket.id} className={`${styles["value-change"]} ${css}`}>
            <span>
              {arrow} {ticket.ticket}
            </span>
            <span>{percentFormatter.format(ticket.valueChange)}</span>
          </p>
        );
      })}
    </div>
  );
}

export default TopTickets;
