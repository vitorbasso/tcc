import { useRef } from "react";

function nameIsValid(name) {
  return name.trim() !== "";
}

function emailIsValid(email) {
  return /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email);
}

function passwordIsValid(password) {
  return password.length >= 8 && password.length <= 30;
}

function confirmPasswordIsValid(confirmPassword, password) {
  return confirmPassword === password;
}

function SignUp() {
  const nameRef = useRef();
  const emailRef = useRef();
  const passwordRef = useRef();
  const confirmPasswordRef = useRef();

  async function submitHandler(event) {
    event.preventDefault();
    const formIsValid =
      nameIsValid(nameRef.current.value) &&
      emailIsValid(emailRef.current.value) &&
      passwordIsValid(passwordRef.current.value) &&
      confirmPasswordIsValid(
        confirmPasswordRef.current.value,
        passwordRef.current.value
      );
    if (!formIsValid) {
      console.log("form is invalid");
      return;
    }

    try {
      const response = await fetch("http://localhost:8080/v1/clients", {
        method: "POST",
        body: JSON.stringify({
          email: emailRef.current.value,
          password: passwordRef.current.value,
          name: nameRef.current.value,
        }),
        headers: {
          "Content-Type": "application/json",
        },
      });
      if (!response.ok) {
        console.log("problem sending request");
        return;
      }
      console.log("Signup successfull");
    } catch (err) {
      console.log(err.message);
    }
  }

  return (
    <main>
      <h2>Faça seu cadastro</h2>
      <form onSubmit={submitHandler}>
        <div className="form-control">
          <label htmlFor="name">Nome:</label>
          <input
            ref={nameRef}
            type="text"
            name="name"
            id="name"
            placeholder="Seu nome aqui"
            required
          />
        </div>
        <div className="form-control">
          <label htmlFor="email">Email:</label>
          <input
            ref={emailRef}
            type="email"
            name="email"
            id="email"
            placeholder="Digite seu email"
            required
          />
        </div>
        <div className="form-control">
          <label htmlFor="password">Senha:</label>
          <input
            ref={passwordRef}
            type="password"
            name="password"
            id="password"
            placeholder="Digite sua senha"
            required
          />
        </div>
        <div className="form-control">
          <label htmlFor="confirm-password">Repita sua senha:</label>
          <input
            ref={confirmPasswordRef}
            type="password"
            name="confirm-password"
            id="confirm-password"
            placeholder="Digite sua senha novamente"
            required
          />
        </div>
        <button type="submit">Cadastrar</button>
      </form>
    </main>
  );
}
export default SignUp;
