import { useEffect } from "react";
import { CLIENTS_URL, WALLETS_URL } from "../../constants/paths";
import useHttp from "../../hooks/useHttp";
import Header from "../../components/header/Header";
import baseStyles from "../../css/base.module.css";
import Money from "../../components/money/Money";
import styles from "./Home.module.css";

function getMoneyClass(money) {
  const moneyLength = ("" + money).split(".")[0].length;
  return moneyLength < 7 ? "money" : moneyLength < 10 ? "money-7" : "money-10";
}

function getFirstName(result) {
  return result ? result.name?.split(" ")?.[0] : "-";
}

function getBalance(result) {
  return result ? result.balance + result.balanceDaytrade : 0;
}

function Home() {
  const { result: resultName, sendRequest: sendRequestName } = useHttp();
  const { result: resultWallet, sendRequest: sendRequestWallet } = useHttp();

  useEffect(() => {
    sendRequestName({
      url: CLIENTS_URL,
    });
  }, [sendRequestName]);

  useEffect(() => {
    sendRequestWallet({
      url: `${WALLETS_URL}/1`,
    });
  }, [sendRequestWallet]);

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
        <section>
          <Money className={styles[moneyClass]} value={money} />
        </section>
      </main>
    </div>
  );
}

export default Home;
