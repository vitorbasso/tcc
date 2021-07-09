import React, { useEffect, useState } from "react";

export const DEFAULT_CONTEXT = {
  isLoggedIn: false,
  token: "",
  onLogin: (token) => {},
  onLogout: () => {},
};

export function AuthContextProvider(props) {
  const [isLoggedIn, setIsLoggedIn] = useState(false);
  const [token, setToken] = useState("");
  useEffect(() => {
    const localToken = localStorage.getItem("token");
    if (!localToken) return;
    setToken(localToken);
    setIsLoggedIn(true);
  }, []);
  function loginHandler(token) {
    if (isLoggedIn) return;
    localStorage.setItem("token", token);
    setToken(token);
    setIsLoggedIn(true);
  }
  function logoutHandler() {
    if (!isLoggedIn) return;
    localStorage.removeItem("token");
    setToken("");
    setIsLoggedIn(false);
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
      {props.children}
    </AuthContext.Provider>
  );
}

const AuthContext = React.createContext(DEFAULT_CONTEXT);

export default AuthContext;
