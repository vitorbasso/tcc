import { Link, useHistory } from "react-router-dom";
import { useRef, useState } from "react";
import styles from "./SignUp.module.css";
import LoadingOverlay from "../../components/loading-overlay/LoadingOverlay";

function isNameValid(name) {
  return name.trim() !== "";
}

function isEmailValid(email) {
  return /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email);
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
  const [nameError, setNameError] = useState(false);
  const emailRef = useRef();
  const [emailError, setEmailError] = useState(false);
  const passwordRef = useRef();
  const [passwordError, setPasswordError] = useState(false);
  const confirmPasswordRef = useRef();
  const [confirmPasswordError, setConfirmPasswordError] = useState(false);
  const [isLoading, setIsLoading] = useState(false);

  function checkFields() {
    const nameIsValid = isNameValid(nameRef.current.value);
    const emailIsValid = isEmailValid(emailRef.current.value);
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

  async function submitHandler(event) {
    event.preventDefault();
    setIsLoading(true);
    const formIsValid = checkFields();
    if (!formIsValid) {
      setIsLoading(false);
      return;
    }
    try {
      const response = await fetch("http://localhost:8080/v1/clients", {
        method: "POST",
        body: JSON.stringify({
          name: nameRef.current.value,
          email: emailRef.current.value,
          password: passwordRef.current.value,
        }),
        headers: {
          "Content-Type": "application/json",
        },
      });
      if (!response.ok) throw new Error("Couldn't register");
      history.replace("/login");
    } catch (err) {
      console.log(err);
    } finally {
      setIsLoading(false);
    }
  }

  return (
    <main className={styles.main}>
      {isLoading && <LoadingOverlay />}
      <h2>Faça seu cadastro</h2>
      <form onSubmit={submitHandler}>
        <div className={styles["form-control"]}>
          <p className={nameError ? "" : styles.hidden}>Digite um nome</p>
          <input
            ref={nameRef}
            type="text"
            name="name"
            id="name"
            placeholder="Seu nome aqui"
            required
          />
        </div>
        <div className={styles["form-control"]}>
          <p className={emailError ? "" : styles.hidden}>
            Digite um email válido
          </p>
          <input
            ref={emailRef}
            type="email"
            name="email"
            id="email"
            placeholder="Digite seu email"
            required
          />
        </div>
        <div className={styles["form-control"]}>
          <p className={passwordError ? "" : styles.hidden}>
            No mínimo 8 caracteres
          </p>
          <input
            ref={passwordRef}
            type="password"
            name="password"
            id="password"
            placeholder="Digite sua senha"
            required
          />
        </div>
        <div className={styles["form-control"]}>
          <p className={confirmPasswordError ? "" : styles.hidden}>
            Suas senhas não batem
          </p>
          <input
            ref={confirmPasswordRef}
            type="password"
            name="confirm-password"
            id="confirm-password"
            placeholder="Confirme sua senha"
            required
          />
        </div>
        <button type="submit">Cadastrar</button>
      </form>
      <Link to="/login">Já possuo conta</Link>
    </main>
  );
}
export default SignUp;
