import { useEffect } from "react";
import Header from "../../components/header/Header";
import Money from "../../components/money/Money";
import { TAX_URL, WALLETS_URL } from "../../constants/paths";
import baseStyles from "../../css/base.module.css";
import useHttp from "../../hooks/useHttp";
import { getMoneyClass } from "../../utils/cssUtils";
import styles from "./TaxReport.module.css";

function TaxReport() {
  const { result: resultTax, sendRequest: sendRequestTax } = useHttp();
  const { result: resultWallet, sendRequest: sendRequestWallet } = useHttp();

  useEffect(() => {
    sendRequestTax({
      url: TAX_URL,
    });
    sendRequestWallet({
      url: WALLETS_URL,
    });
  }, [sendRequestTax, sendRequestWallet]);

  let money = 0;
  let balance = 0;
  let deductable = 0;
  let withdrawn = 0;
  let base = 0;
  if (resultWallet) {
    balance = resultWallet.balance + resultWallet.balanceDaytrade;
    withdrawn = resultWallet.withdrawn + resultWallet.withdrawnDaytrade;
  }
  if (resultTax) {
    money = resultTax.normalTax + resultTax.daytradeTax;
    deductable =
      resultTax.availableToDeduct + resultTax.daytradeAvailableToDeduct;
    base = resultTax.baseForCalculation + resultTax.daytradeBaseForCalculation;
  } else {
    money = 0;
  }

  const moneyClass = getMoneyClass(money);
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
            value={money}
          />
        </section>
        <section className={styles.overview}>
          <ul>
            <li>
              <span>Lucro:</span>
              <span>
                <Money className={styles["inline-money"]} value={balance} />
              </span>
            </li>
            <li>
              <span>Vendas:</span>
              <span>
                <Money className={styles["inline-money"]} value={withdrawn} />
              </span>
            </li>
            <li>
              <span>
                Prejuízo
                <br />
                acumulado:
              </span>
              <span>
                <Money className={styles["inline-money"]} value={deductable} />
              </span>
            </li>
          </ul>
          <p>Valor base cálculo</p>
          <Money value={base} />
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
