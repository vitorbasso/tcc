import styles from "./QuickSoldView.module.css";
import Money from "../money/Money";
import { useContext, useEffect } from "react";
import taxContext from "../../context/tax-context";
import WalletContext from "../../context/wallet-context";

function QuickSoldView() {
  const { tax, fetchTax } = useContext(taxContext);
  const { wallet } = useContext(WalletContext);
  useEffect(() => {
    fetchTax();
  }, [fetchTax]);
  let totalTax = 0;
  if (tax) {
    totalTax = tax.normalTax + tax.daytradeTax;
  }
  let totalBalance = 0;
  if (wallet) {
    totalBalance = wallet.balance + wallet.balanceDaytrade;
  }

  return (
    <div>
      <p>Lucro:</p>
      <div>
        <Money value={totalBalance} className={styles["money-size"]} />
      </div>
      <p>IR:</p>
      <div>
        <Money value={totalTax} className={styles["money-size"]} />
      </div>
    </div>
  );
}

export default QuickSoldView;
