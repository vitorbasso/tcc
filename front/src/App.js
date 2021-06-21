import SignUp from "./pages/auth/SignUp";
import "./App.css";
import { Route, Switch } from "react-router-dom";
import SignIn from "./pages/auth/SignIn";
import Home from "./pages/home/Home";
import AuthContext from "./context/auth-context";
import { useContext } from "react";

function App() {
  const authCtx = useContext(AuthContext);
  return (
    <Switch>
      {!authCtx.isLoggedIn && (
        <Route path="/" exact>
          <SignIn />
        </Route>
      )}
      {!authCtx.isLoggedIn && (
        <Route path="/register">
          <SignUp />
        </Route>
      )}
      {authCtx.isLoggedIn && (
        <Route path="/" exact>
          <Home />
        </Route>
      )}
      <Route path="*">
        <h2>Not Found</h2>
      </Route>
    </Switch>
  );
}

export default App;
