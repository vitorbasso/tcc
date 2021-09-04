import { useContext, useRef, useState } from "react";
import { Link } from "react-router-dom";
import { validateEmailInput } from "../../utils/validationUtils";
import LoadingOverlay from "../../components/loading-overlay/LoadingOverlay";
import useHttp from "../../hooks/useHttp";
import { AUTH_URL } from "../../constants/paths";
import styles from "./forms.module.css";
import baseStyles from "../../css/base.module.css";
import AuthContext from "../../context/auth-context";

function SignIn() {
  const emailRef = useRef();
  const [emailError, setEmailError] = useState(false);
  const passwordRef = useRef();
  const authCtx = useContext(AuthContext);
  const { result, error, isLoading, sendRequest } = useHttp();

  function submitHandler(event) {
    event.preventDefault();
    const emailIsValid = validateEmailInput(emailRef.current.value);
    setEmailError(!emailIsValid);
    if (!emailIsValid) return;
    sendRequest({
      url: AUTH_URL,
      method: "POST",
      body: {
        username: emailRef.current.value,
        password: passwordRef.current.value,
      },
    });
  }

  if (!isLoading && result) {
    const jwt = result?.jwt;
    if (!jwt) {
      console.error("did not get token");
      return;
    }
    setTimeout(() => {
      authCtx.onLogin(jwt);
    }, 10);
    return [];
  }

  return (
    <main className={`${styles.main} ${baseStyles.container}`}>
      {isLoading && <LoadingOverlay />}
      <h2>Login</h2>
      <form onSubmit={submitHandler} className={styles["sign-in"]}>
        <p
          className={`${baseStyles["error-text"]} ${
            error ? "" : baseStyles.hidden
          }`}
        >
          Dados de Login inválidos.
        </p>
        <div className={baseStyles["form-control"]}>
          <p
            className={`${baseStyles["error-text"]} ${
              emailError ? "" : baseStyles.hidden
            }`}
          >
            Informe um email válido.
          </p>
          <input
            ref={emailRef}
            type="email"
            name="email"
            placeholder="Seu email"
            required
          />
        </div>
        <div className={baseStyles["form-control"]}>
          <p className={baseStyles.hidden}>No mínimo 8 caracteres</p>
          <input
            ref={passwordRef}
            type="password"
            name="password"
            placeholder="Sua senha"
            required
          />
        </div>
        <button className={baseStyles.btn} type="submit">
          Login
        </button>
      </form>
      <Link to="/register">Criar uma conta</Link>
    </main>
  );
}

export default SignIn;
