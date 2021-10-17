import { useContext, useEffect, useRef, useState } from "react";
import Header from "../../components/header/Header";
import Money from "../../components/money/Money";
import MonthWallet from "../../context/month-wallet-context";
import WalletMonths from "../../context/month-wallets-context";
import TaxContext from "../../context/tax-context";
import WalletContext from "../../context/wallet-context";
import baseStyles from "../../css/base.module.css";
import { getMoneyClass } from "../../utils/cssUtils";
import ReactToPrint from "react-to-print";
import styles from "./TaxReport.module.css";
import { BiCalendarCheck } from "react-icons/bi";

function TaxReport() {
  const { tax, fetchTax, query: queryTax } = useContext(TaxContext);
  const { wallet, fetchWallet } = useContext(WalletContext);
  const [balance, setBalance] = useState(0);
  const [balanceDaytrade, setBalanceDaytrade] = useState(0);
  const printRef = useRef();
  const { walletMonths, walletId, fetchWalletMonthsList } =
    useContext(WalletMonths);
  const {
    monthWallet,
    fetchWalletMonth,
    query: queryMonthWallet,
  } = useContext(MonthWallet);
  const [month, setMonth] = useState(new Date().toISOString().slice(0, 7));
  const [today] = useState(new Date().toISOString().slice(0, 7));
  useEffect(() => {
    window.scrollTo(0, 0);
  }, []);

  useEffect(() => {
    fetchTax();
    fetchWallet();
    fetchWalletMonthsList();
  }, [fetchTax, fetchWallet, fetchWalletMonthsList]);

  useEffect(() => {
    fetchWalletMonth();
  }, [fetchWalletMonth, walletId]);

  useEffect(() => {
    if (wallet.id !== -1 && month + "-01" === wallet.walletMonth) {
      setWalletBalances({
        balance: wallet.balance,
        balanceDaytrade: wallet.balanceDaytrade,
      });
    }
  }, [
    wallet.balance,
    wallet.balanceDaytrade,
    month,
    wallet.walletMonth,
    wallet.id,
  ]);

  useEffect(() => {
    if (monthWallet.id !== -1 && monthWallet.walletMonth === month + "-01") {
      setWalletBalances({
        balance: monthWallet.balance,
        balanceDaytrade: monthWallet.balanceDaytrade,
      });
    }
  }, [
    monthWallet.id,
    monthWallet.walletMonth,
    month,
    monthWallet.balance,
    monthWallet.balanceDaytrade,
  ]);

  let normalTax = 0;
  let daytradeTax = 0;
  let deductable = 0;
  let daytradeDeductable = 0;
  let deducted = 0;
  let daytradeDeducted = 0;
  let withdrawn = 0;
  let daytradeWithdrawn = 0;
  let base = 0;
  let daytradeBase = 0;
  let irrf = 0;
  let daytradeIrrf = 0;
  if (tax) {
    normalTax = tax.normalTax;
    daytradeTax = tax.daytradeTax;
    deductable = tax.availableToDeduct;
    daytradeDeductable = tax.daytradeAvailableToDeduct;
    deducted = tax.deducted;
    daytradeDeducted = tax.daytradeDeducted;
    base = tax.baseForCalculation - deducted;
    daytradeBase = tax.daytradeBaseForCalculation - daytradeDeducted;
    withdrawn = tax.withdrawn;
    daytradeWithdrawn = tax.daytradeWithdrawn;
    irrf = withdrawn * 0.00005 > 1 && balance > 0 ? withdrawn * 0.00005 : 0;
    daytradeIrrf = balanceDaytrade > 0 ? balanceDaytrade * 0.01 : 0;
  } else {
    normalTax = 0;
    daytradeTax = 0;
  }

  const totalTax = normalTax + daytradeTax;
  const totalBalance = balance + balanceDaytrade;
  const totalIrrf = irrf + daytradeIrrf;
  const totalDeductable = deductable + daytradeDeductable;
  const totalDeducted = deducted + daytradeDeducted;
  const totalWithdrawn = withdrawn + daytradeWithdrawn;

  function setWalletBalances(balances = { balance: 0, balanceDaytrade: 0 }) {
    setBalance(balances.balance);
    setBalanceDaytrade(balances.balanceDaytrade);
  }

  function handleMonthSelect(event) {
    event.preventDefault();
    const selectedMonth = event.target.value;
    setMonth(selectedMonth);
    queryTax(selectedMonth);
    if (walletMonths.length > 0) {
      const monthWalletId = walletMonths.find(
        (entry) => entry.month === selectedMonth + "-01"
      )?.id;
      if (monthWalletId) {
        queryMonthWallet(monthWalletId);
      } else if (selectedMonth + "-01" === wallet.walletMonth) {
        setWalletBalances(wallet);
      } else {
        setWalletBalances();
      }
    }
  }

  const moneyClass = getMoneyClass(totalTax);
  return (
    <div className={baseStyles.container}>
      <Header backButton>
        <h2>Imposto</h2>
      </Header>
      <main>
        <section className={styles.glance}>
          <h3>
            Imposto{" "}
            {new Date(month + "-02").toLocaleDateString("pt-BR", {
              month: "long",
              year: "numeric",
            })}
            <input
              type="month"
              className={styles["month-input"]}
              max={today}
              defaultValue={month}
              onChange={handleMonthSelect}
              lang="pt-BR"
            />
            <span className={styles["calendar-icon"]}>
              <BiCalendarCheck />
            </span>
          </h3>
          <Money className={baseStyles[moneyClass]} value={totalTax} />
        </section>
        <section className={styles.overview} ref={printRef}>
          <ul>
            <li>
              <span>Balanço:</span>
              <span
                className={
                  totalBalance > 0
                    ? styles.green
                    : totalBalance < 0
                    ? styles.red
                    : ""
                }
              >
                <Money
                  className={styles["inline-money"]}
                  value={totalBalance}
                />
              </span>
            </li>
            <li className={styles["sub-list"]}>
              <span>- Normal:</span>
              <span
                className={
                  balance > 0 ? styles.green : balance < 0 ? styles.red : ""
                }
              >
                <Money className={styles["inline-money"]} value={balance} />
              </span>
            </li>
            <li className={styles["sub-list"]}>
              <span>- Daytrade:</span>
              <span
                className={
                  balanceDaytrade > 0
                    ? styles.green
                    : balanceDaytrade < 0
                    ? styles.red
                    : ""
                }
              >
                <Money
                  className={styles["inline-money"]}
                  value={balanceDaytrade}
                />
              </span>
            </li>
            <li>
              <span>Vendas:</span>
              <span>
                <Money
                  className={styles["inline-money"]}
                  value={totalWithdrawn}
                />
              </span>
            </li>
            <li className={styles["sub-list"]}>
              <span>- Normal:</span>
              <span>
                <Money className={styles["inline-money"]} value={withdrawn} />
              </span>
            </li>
            <li className={styles["sub-list"]}>
              <span>- Daytrade:</span>
              <span>
                <Money
                  className={styles["inline-money"]}
                  value={daytradeWithdrawn}
                />
              </span>
            </li>
            <li>
              <span>
                Prejuízo
                <br />
                acumulado:
              </span>
              <span>
                <Money
                  className={styles["inline-money"]}
                  value={totalDeductable}
                />
              </span>
            </li>
            <li className={styles["sub-list"]}>
              <span>- Normal:</span>
              <span>
                <Money className={styles["inline-money"]} value={deductable} />
              </span>
            </li>
            <li className={styles["sub-list"]}>
              <span>- Daytrade:</span>
              <span>
                <Money
                  className={styles["inline-money"]}
                  value={daytradeDeductable}
                />
              </span>
            </li>
            <li>
              <span>
                Prejuízo
                <br />
                descontado:
              </span>
              <span>
                <Money
                  className={styles["inline-money"]}
                  value={totalDeducted}
                />
              </span>
            </li>
            <li className={styles["sub-list"]}>
              <span>- Normal:</span>
              <span>
                <Money className={styles["inline-money"]} value={deducted} />
              </span>
            </li>
            <li className={styles["sub-list"]}>
              <span>- Daytrade:</span>
              <span>
                <Money
                  className={styles["inline-money"]}
                  value={daytradeDeducted}
                />
              </span>
            </li>
            <li>
              <span>IRRF:</span>
              <span>
                <Money className={styles["inline-money"]} value={totalIrrf} />
              </span>
            </li>
            <li className={styles["sub-list"]}>
              <span>- Normal:</span>
              <span>
                <Money className={styles["inline-money"]} value={irrf} />
              </span>
            </li>
            <li className={styles["sub-list"]}>
              <span>- Daytrade:</span>
              <span>
                <Money
                  className={styles["inline-money"]}
                  value={daytradeIrrf}
                />
              </span>
            </li>
            <li>
              <span>Imposto:</span>
              <span>
                <Money className={styles["inline-money"]} value={totalTax} />
              </span>
            </li>
            <li className={styles["sub-list"]}>
              <span>- Normal:</span>
              <span>
                <Money className={styles["inline-money"]} value={normalTax} />
              </span>
            </li>
            <li className={styles["sub-list"]}>
              <span>- Daytrade:</span>
              <span>
                <Money className={styles["inline-money"]} value={daytradeTax} />
              </span>
            </li>
          </ul>
          <p>Valor base cálculo</p>
          <Money
            value={base}
            className={base > 0 ? styles.green : base < 0 ? styles.red : ""}
          />
          <p>Valor base cálculo Daytrade</p>
          <Money
            value={daytradeBase}
            className={
              daytradeBase > 0
                ? styles.green
                : daytradeBase < 0
                ? styles.red
                : ""
            }
          />
        </section>
        <section className={styles.action}>
          <ReactToPrint
            trigger={() => {
              return (
                <button type="button" className={baseStyles.btn}>
                  Imprimir
                </button>
              );
            }}
            pageStyle={() => {
              return "width: 60%";
            }}
            content={() => printRef.current}
          />
        </section>
      </main>
    </div>
  );
}

export default TaxReport;
