import { Link, useHistory } from "react-router-dom";
import { useEffect, useRef, useState } from "react";
import { CLIENTS_URL } from "../../constants/paths";
import { validateEmailInput } from "../../utils/validationUtils";
import styles from "./forms.module.css";
import baseStyles from "../../css/base.module.css";
import LoadingOverlay from "../../components/loading-overlay/LoadingOverlay";
import useHttp from "../../hooks/useHttp";

function isNameValid(name) {
  return name.trim() !== "";
}

function isPasswordValid(password) {
  return password.length >= 8 && password.length <= 30;
}

function isConfirmPasswordValid(confirmPassword, password) {
  return confirmPassword === password;
}

function SignUp() {
  const history = useHistory();
  const nameRef = useRef();
  const {
    result: registerSuccess,
    error: registerError,
    isLoading: registerIsLoading,
    sendRequest: sendRegister,
  } = useHttp();
  const [nameError, setNameError] = useState(false);
  const emailRef = useRef();
  const [emailError, setEmailError] = useState(false);
  const passwordRef = useRef();
  const [passwordError, setPasswordError] = useState(false);
  const confirmPasswordRef = useRef();
  const [confirmPasswordError, setConfirmPasswordError] = useState(false);

  useEffect(() => {
    nameRef.current.focus();
    window.scrollTo(0, 0);
  }, []);

  function areFieldsValid() {
    const nameIsValid = isNameValid(nameRef.current.value);
    const emailIsValid = validateEmailInput(emailRef.current.value);
    const passwordIsValid = isPasswordValid(passwordRef.current.value);
    const confirmPasswordIsValid = isConfirmPasswordValid(
      confirmPasswordRef.current.value,
      passwordRef.current.value
    );
    setNameError(!nameIsValid);
    setEmailError(!emailIsValid);
    setPasswordError(!passwordIsValid);
    setConfirmPasswordError(!confirmPasswordIsValid);
    return (
      nameIsValid && emailIsValid && passwordIsValid && confirmPasswordIsValid
    );
  }

  function submitHandler(event) {
    event.preventDefault();
    if (!areFieldsValid()) return;

    sendRegister({
      url: CLIENTS_URL,
      method: "POST",
      body: {
        name: nameRef.current.value,
        email: emailRef.current.value,
        password: passwordRef.current.value,
      },
    });
  }

  if (!registerIsLoading && registerSuccess) {
    setTimeout(() => {
      history.replace("/");
    }, 10);
    return [];
  }

  return (
    <main className={`${styles.main} ${baseStyles.container}`}>
      {registerIsLoading && <LoadingOverlay />}
      <h2>Cadastro</h2>
      <form onSubmit={submitHandler} className={styles["sign-up"]}>
        <p
          className={`${baseStyles["error-text"]} ${
            registerError ? "" : baseStyles.hidden
          }`}
        >
          Não foi possível completar seu cadastro.
        </p>

        <div className={baseStyles["form-control"]}>
          <p
            className={`${baseStyles["error-text"]} ${
              nameError ? "" : baseStyles.hidden
            }`}
          >
            Digite um nome
          </p>
          <input
            ref={nameRef}
            type="text"
            name="name"
            placeholder="Seu nome aqui"
            required
          />
        </div>
        <div className={baseStyles["form-control"]}>
          <p
            className={`${baseStyles["error-text"]} ${
              emailError ? "" : baseStyles.hidden
            }`}
          >
            Digite um email válido
          </p>
          <input
            ref={emailRef}
            type="email"
            name="email"
            placeholder="Digite seu email"
            required
          />
        </div>
        <div className={baseStyles["form-control"]}>
          <p
            className={`${baseStyles["error-text"]} ${
              passwordError ? "" : baseStyles.hidden
            }`}
          >
            No mínimo 8 caracteres
          </p>
          <input
            ref={passwordRef}
            type="password"
            name="password"
            placeholder="Digite sua senha"
            required
          />
        </div>
        <div className={baseStyles["form-control"]}>
          <p
            className={`${baseStyles["error-text"]} ${
              confirmPasswordError ? "" : baseStyles.hidden
            }`}
          >
            Suas senhas não batem
          </p>
          <input
            ref={confirmPasswordRef}
            type="password"
            name="confirm-password"
            placeholder="Confirme sua senha"
            required
          />
        </div>
        <button className={baseStyles.btn} type="submit">
          Cadastrar
        </button>
      </form>
      <Link to="/">Já possuo uma conta</Link>
    </main>
  );
}
export default SignUp;
