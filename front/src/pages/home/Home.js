import { useEffect } from "react";
import { CLIENTS_URL, TAX_URL, WALLETS_URL } from "../../constants/paths";
import useHttp from "../../hooks/useHttp";
import Header from "../../components/header/Header";
import baseStyles from "../../css/base.module.css";
import Money from "../../components/money/Money";
import styles from "./Home.module.css";
import AtAGlance from "../../components/atAGlance/AtAGlance";
import Navigation from "../../components/navigation/Navigation";
import { getMoneyClass } from "../../utils/cssUtils";

function getFirstName(result) {
  return result ? result.name?.split(" ")?.[0] : "-";
}

function getBalance(result) {
  return result ? result.balance + result.balanceDaytrade : 0;
}

function Home() {
  const { result: resultName, sendRequest: sendRequestName } = useHttp();
  const { result: resultWallet, sendRequest: sendRequestWallet } = useHttp();
  const { result: resultTax, sendRequest: sendRequestTax } = useHttp();

  useEffect(() => {
    sendRequestName({
      url: CLIENTS_URL,
    });
  }, [sendRequestName]);

  useEffect(() => {
    sendRequestWallet({
      url: WALLETS_URL,
    });
  }, [sendRequestWallet]);

  useEffect(() => {
    sendRequestTax({
      url: TAX_URL,
    });
  }, [sendRequestTax]);

  const firstName = getFirstName(resultName);
  const money = getBalance(resultWallet);
  const moneyClass = getMoneyClass(money);
  return (
    <div className={`${baseStyles.container} ${styles.container}`}>
      <Header logout>
        <h3>Bem Vindo,</h3>
        <h2>{firstName}</h2>
      </Header>
      <main>
        <section className={styles.section}>
          <Money className={baseStyles[moneyClass]} value={money} />
        </section>
        <section>
          <AtAGlance wallet={resultWallet} tax={resultTax} />
        </section>
        <section>
          <Navigation />
        </section>
      </main>
    </div>
  );
}

export default Home;
