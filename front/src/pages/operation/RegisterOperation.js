import { useState } from "react";
import Header from "../../components/header/Header";
import baseStyles from "../../css/base.module.css";
import styles from "./RegisterOperation.module.css";
import { CgSoftwareUpload } from "react-icons/cg";

const BUY = "buy";
const SELL = "sell";

function RegisterOperation(props) {
  const [type, setType] = useState(BUY);
  function onSubmitHandler(event) {
    event.preventDefault();
  }

  function onTypeClick(event) {
    event.preventDefault();
    const btn = event.target.closest("button");
    if (!btn) return;
    document
      .querySelector(`.${styles.selected}`)
      .classList.remove(styles.selected);
    btn.classList.add(styles.selected);
    if (btn.dataset.type !== type) setType(btn.dataset.type);
  }
  return (
    <div className={baseStyles.container}>
      <Header backButton>
        <h2>Operação</h2>
      </Header>
      <main>
        <form onSubmit={onSubmitHandler} className={styles.form}>
          <div className={baseStyles["form-control"]}>
            <button
              className={`${styles["flex-end"]} ${styles.btn}`}
              type="button"
            >
              {" "}
              <CgSoftwareUpload /> Importar
            </button>
          </div>
          <div
            className={`${styles["form-type"]} ${baseStyles["form-control"]}`}
          >
            <span>Tipo</span>
            <div onClick={onTypeClick}>
              <button
                type="button"
                data-type={BUY}
                className={`${styles.btn} ${styles.selected}`}
              >
                COMPRA
              </button>
              <button type="button" data-type={SELL} className={styles.btn}>
                VENDA
              </button>
            </div>
          </div>
          <div className={baseStyles["form-control"]}>
            <input
              type="text"
              id="ticker"
              name="ticker"
              placeholder="Ação"
              required
            />
          </div>
          <div className={baseStyles["form-control"]}>
            <input
              type="number"
              name="quantity"
              min="0"
              placeholder="Quantidade"
              required
            />
          </div>
          <div className={baseStyles["form-control"]}>
            <input
              type="text"
              name="value"
              placeholder="Valor Total"
              required
            />
          </div>
          <div className={baseStyles["form-control"]}>
            <input type="datetime-local" name="date" />
          </div>
          <div className={baseStyles["form-control"]}>
            <button type="submit" className={baseStyles.btn}>
              Cadastrar
            </button>
          </div>
        </form>
      </main>
    </div>
  );
}

export default RegisterOperation;
