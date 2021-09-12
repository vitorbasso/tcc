import { useContext, useEffect } from "react";
import Header from "../../components/header/Header";
import Money from "../../components/money/Money";
import TaxContext from "../../context/tax-context";
import WalletContext from "../../context/wallet-context";
import baseStyles from "../../css/base.module.css";
import { getMoneyClass } from "../../utils/cssUtils";
import styles from "./TaxReport.module.css";

function TaxReport() {
  const { tax, fetchTax } = useContext(TaxContext);
  const { wallet, fetchWallet } = useContext(WalletContext);

  useEffect(() => {
    window.scrollTo(0, 0);
  }, []);

  useEffect(() => {
    fetchTax();
    fetchWallet();
  }, [fetchTax, fetchWallet]);

  let normalTax = 0;
  let daytradeTax = 0;
  let balance = 0;
  let daytradeBalance = 0;
  let deductable = 0;
  let daytradeDeductable = 0;
  let withdrawn = 0;
  let daytradeWithdrawn = 0;
  let base = 0;
  let daytradeBase = 0;
  let irrf = 0;
  let daytradeIrrf = 0;
  if (wallet) {
    balance = wallet.balance > 0 ? wallet.balance : 0;
    daytradeBalance = wallet.balanceDaytrade > 0 ? wallet.balanceDaytrade : 0;
    withdrawn = wallet.withdrawn;
    daytradeWithdrawn = wallet.withdrawnDaytrade;
    irrf = withdrawn * 0.00005 > 1 && balance > 0 ? withdrawn * 0.00005 : 0;
    daytradeIrrf = daytradeBalance * 0.01;
  }
  if (tax) {
    normalTax = tax.normalTax;
    daytradeTax = tax.daytradeTax;
    deductable = tax.availableToDeduct;
    daytradeDeductable = tax.daytradeAvailableToDeduct;
    base = tax.baseForCalculation;
    daytradeBase = tax.daytradeBaseForCalculation;
  } else {
    normalTax = 0;
    daytradeTax = 0;
  }

  const totalTax = normalTax + daytradeTax;
  const totalBalance = balance + daytradeBalance;
  const totalIrrf = irrf + daytradeIrrf;
  const totalDeductable = deductable + daytradeDeductable;
  const totalWithdrawn = withdrawn + daytradeWithdrawn;

  const moneyClass = getMoneyClass(totalTax);
  return (
    <div className={baseStyles.container}>
      <Header backButton>
        <h2>Imposto</h2>
      </Header>
      <main>
        <section className={styles.glance}>
          <h3>
            Imposto {new Date().toLocaleString("pt-BR", { month: "long" })}
          </h3>
          <Money
            className={`${styles.money} ${baseStyles[moneyClass]}`}
            value={totalTax}
          />
        </section>
        <section className={styles.overview}>
          <ul>
            <li>
              <span>Lucro:</span>
              <span>
                <Money
                  className={styles["inline-money"]}
                  value={totalBalance}
                />
              </span>
            </li>
            <li className={styles["sub-list"]}>
              <span>- Normal:</span>
              <span>
                <Money className={styles["inline-money"]} value={balance} />
              </span>
            </li>
            <li className={styles["sub-list"]}>
              <span>- Daytrade:</span>
              <span>
                <Money
                  className={styles["inline-money"]}
                  value={daytradeBalance}
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
          <Money value={base} className={base < 0 && styles.loss} />
          <p>Valor base cálculo Daytrade</p>
          <Money
            value={daytradeBase}
            className={daytradeBase < 0 && styles.loss}
          />
        </section>
        <section className={styles.action}>
          <button type="button" className={baseStyles.btn}>
            Descontar Prejuizo
          </button>
          <button type="button" className={baseStyles.btn}>
            Imprimir
          </button>
        </section>
      </main>
    </div>
  );
}

export default TaxReport;
