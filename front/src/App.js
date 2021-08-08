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
