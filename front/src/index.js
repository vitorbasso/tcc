import React from "react";
import ReactDOM from "react-dom";
import App from "./App";
import { BrowserRouter as Router } from "react-router-dom";
import { AuthContextProvider } from "./context/auth-context";
import { WalletContextProvider } from "./context/wallet-context";
import { TaxContextProvider } from "./context/tax-context";

ReactDOM.render(
  <AuthContextProvider>
    <WalletContextProvider>
      <TaxContextProvider>
        <Router>
          <React.StrictMode>
            <App />
          </React.StrictMode>
        </Router>
      </TaxContextProvider>
    </WalletContextProvider>
  </AuthContextProvider>,
  document.getElementById("root")
);
