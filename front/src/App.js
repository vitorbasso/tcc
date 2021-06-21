import SignUp from "./pages/signup/SignUp";
import "./App.css";
import { Route, Switch } from "react-router-dom";
import SignIn from "./pages/signIn/SignIn";
import Home from "./pages/home/Home";
import AuthContext from "./context/auth-context";
import { useEffect, useState } from "react";

function App() {
  const [isLoggedIn, setIsLoggedIn] = useState(false);
  const [token, setToken] = useState("");
  useEffect(() => {
    const localToken = localStorage.getItem("token");
    if (!localToken) return;
    setToken(localToken);
    setIsLoggedIn(true);
  }, []);
  function logoutHandler() {
    if (!isLoggedIn) return;
    localStorage.removeItem("token");
    setToken("");
    setIsLoggedIn(false);
  }
  function loginHandler(token) {
    if (isLoggedIn) return;
    localStorage.setItem("token", token);
    setToken(token);
    setIsLoggedIn(true);
  }
  return (
    <AuthContext.Provider
      value={{
        isLoggedIn,
        token,
        onLogin: loginHandler,
        onLogout: logoutHandler,
      }}
    >
      <Switch>
        {!isLoggedIn && (
          <Route path="/" exact>
            <SignIn />
          </Route>
        )}
        {!isLoggedIn && (
          <Route path="/register">
            <SignUp />
          </Route>
        )}
        {isLoggedIn && (
          <Route path="/" exact>
            <Home />
          </Route>
        )}
      </Switch>
    </AuthContext.Provider>
  );
}

export default App;
