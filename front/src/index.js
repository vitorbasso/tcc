import React from "react";
import ReactDOM from "react-dom";
import App from "./App";
import { BrowserRouter as Router } from "react-router-dom";
import { AuthContextProvider } from "./context/auth-context";
import { WalletContextProvider } from "./context/wallet-context";
import { TaxContextProvider } from "./context/tax-context";
import { StocksContextProvider } from "./context/stock-context";
import { MonthWalletsContextProvider } from "./context/month-wallets-context";
import { MonthWalletContextProvider } from "./context/month-wallet-context";

ReactDOM.render(
  <AuthContextProvider>
    <WalletContextProvider>
      <StocksContextProvider>
        <TaxContextProvider>
          <MonthWalletsContextProvider>
            <MonthWalletContextProvider>
              <Router>
                <React.StrictMode>
                  <App />
                </React.StrictMode>
              </Router>
            </MonthWalletContextProvider>
          </MonthWalletsContextProvider>
        </TaxContextProvider>
      </StocksContextProvider>
    </WalletContextProvider>
  </AuthContextProvider>,
  document.getElementById("root")
);
