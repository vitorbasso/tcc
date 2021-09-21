import SignUp from "./pages/auth/SignUp";
import "./App.css";
import { Route, Switch } from "react-router-dom";
import SignIn from "./pages/auth/SignIn";
import Home from "./pages/home/Home";
import AuthContext from "./context/auth-context";
import { useContext } from "react";
import Overview from "./pages/overview/Overview";
import NotFound from "./pages/not-found/NotFound";
import PerformanceReport from "./pages/reports/PerformanceReport";
import RegisterOperation from "./pages/operation/RegisterOperation";
import TaxReport from "./pages/tax/TaxReport";
import TicketReport from "./pages/reports/TicketReport";
import MonthWallet from "./context/month-wallet-context";
import WalletContext from "./context/wallet-context";
import useLogout from "./hooks/useLogout";
import StocksContext from "./context/stock-context";
import WalletMonths from "./context/month-wallets-context";
import TaxContext from "./context/tax-context";

function App() {
  const logout = useLogout();
  const { isLoggedIn } = useContext(AuthContext);
  const { error: walletError } = useContext(WalletContext);
  const { error: stocksError } = useContext(StocksContext);
  const { error: walletMonthsError } = useContext(WalletMonths);
  const { error: taxError } = useContext(TaxContext);
  const { error: monthWalletError } = useContext(MonthWallet);

  if (
    walletError?.status === 403 ||
    walletMonthsError?.status === 403 ||
    stocksError?.status === 403 ||
    taxError?.status === 403 ||
    monthWalletError?.status === 403
  ) {
    logout();
  }

  return (
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
      {isLoggedIn && (
        <Route path="/overview" exact>
          <Overview />
        </Route>
      )}
      {isLoggedIn && (
        <Route path="/performance" exact>
          <PerformanceReport />
        </Route>
      )}
      {isLoggedIn && (
        <Route path="/performance/:id" exact>
          <TicketReport />
        </Route>
      )}
      {isLoggedIn && (
        <Route path="/register-operation" exact>
          <RegisterOperation />
        </Route>
      )}
      {isLoggedIn && (
        <Route path="/tax" exact>
          <TaxReport />
        </Route>
      )}
      <Route path="*">
        <NotFound />
      </Route>
    </Switch>
  );
}

export default App;
