import { useEffect } from "react";
import { CLIENTS_URL } from "../../constants/paths";
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

function Home() {
  const { result, isLoading, sendRequest } = useHttp();

  useEffect(() => {
    sendRequest({
      url: CLIENTS_URL,
    });
  }, [sendRequest]);

  const firstName = getFirstName(result);
  const money = 888_888.88;
  const moneyClass = getMoneyClass(money);
  return (
    <>
      {!isLoading && result && (
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
      )}
    </>
  );
}

export default Home;
