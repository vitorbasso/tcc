import { useHistory } from "react-router";
import Header from "../../components/header/Header";
import baseStyles from "../../css/base.module.css";
import styles from "./NotFound.module.css";

function NotFound() {
  const history = useHistory();

  function onClickHandler() {
    history.push("/");
  }
  return (
    <div className={baseStyles.container}>
      <Header backButton>
        <h2>Not Found</h2>
      </Header>
      <main className={styles.main}>
        <p className={baseStyles["error-text"]}>
          Desculpe, não encontramos a página que estava procurando.
        </p>
        <button onClick={onClickHandler} className={baseStyles.btn}>
          Voltar para Home
        </button>
      </main>
    </div>
  );
}

export default NotFound;
