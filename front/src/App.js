import SignUp from "./pages/auth/SignUp";
import "./App.css";
import { Route, Switch } from "react-router-dom";
import SignIn from "./pages/auth/SignIn";
import Home from "./pages/home/Home";
import AuthContext from "./context/auth-context";
import { useContext } from "react";
import Overview from "./pages/overview/Overview";
import NotFound from "./pages/not-found/NotFound";

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
      <Route path="*">
        <NotFound />
      </Route>
    </Switch>
  );
}

export default App;
