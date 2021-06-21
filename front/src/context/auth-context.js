import React from "react";

export const DEFAULT_CONTEXT = {
  isLoggedIn: false,
  token: "",
  onLogin: (token) => {},
  onLogout: () => {},
};

const AuthContext = React.createContext(DEFAULT_CONTEXT);

export default AuthContext;
