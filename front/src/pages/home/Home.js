import { useContext, useEffect } from "react";
import { CLIENTS_URL } from "../../constants/paths";
import useHttp from "../../hooks/useHttp";
import Header from "../../components/header/Header";
import baseStyles from "../../css/base.module.css";
import Money from "../../components/money/Money";
import styles from "./Home.module.css";
import AtAGlance from "../../components/atAGlance/AtAGlance";
import Navigation from "../../components/navigation/Navigation";
import { getMoneyClass } from "../../utils/cssUtils";
import WalletContext from "../../context/wallet-context";

function getFirstName(result) {
  return result ? result.name?.split(" ")?.[0] : "-";
}

function getBalance(result) {
  return result
    ? result.stockAssets.reduce((total, asset) => {
        return (total +=
          asset.amount > 0 ? asset.averageCost * asset.amount : 0);
      }, 0)
    : 0;
}

function Home() {
  const { wallet, fetchWallet } = useContext(WalletContext);
  const { result: resultName, sendRequest: sendRequestName } = useHttp();
  useEffect(() => {
    sendRequestName({
      url: CLIENTS_URL,
    });
  }, [sendRequestName]);

  useEffect(() => {
    fetchWallet();
  }, [fetchWallet]);

  const firstName = getFirstName(resultName);
  const money = getBalance(wallet);
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
          <AtAGlance />
        </section>
        <section>
          <Navigation />
        </section>
      </main>
    </div>
  );
}

export default Home;
