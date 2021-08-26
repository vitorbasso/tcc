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
import { WalletContextProvider } from "./context/wallet-context";
import { TaxContextProvider } from "./context/tax-context";

function App() {
  const { isLoggedIn } = useContext(AuthContext);
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
          <WalletContextProvider>
            <TaxContextProvider>
              <Home />
            </TaxContextProvider>
          </WalletContextProvider>
        </Route>
      )}
      {isLoggedIn && (
        <Route path="/overview" exact>
          <WalletContextProvider>
            <Overview />
          </WalletContextProvider>
        </Route>
      )}
      {isLoggedIn && (
        <Route path="/performance" exact>
          <WalletContextProvider>
            <PerformanceReport />
          </WalletContextProvider>
        </Route>
      )}
      {isLoggedIn && (
        <Route path="/performance/:id" exact>
          <WalletContextProvider>
            <TicketReport />
          </WalletContextProvider>
        </Route>
      )}
      {isLoggedIn && (
        <Route path="/register-operation" exact>
          <WalletContextProvider>
            <RegisterOperation />
          </WalletContextProvider>
        </Route>
      )}
      {isLoggedIn && (
        <Route path="/tax" exact>
          <WalletContextProvider>
            <TaxContextProvider>
              <TaxReport />
            </TaxContextProvider>
          </WalletContextProvider>
        </Route>
      )}
      <Route path="*">
        <NotFound />
      </Route>
    </Switch>
  );
}

export default App;
