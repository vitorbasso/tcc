import SignUp from "./pages/signup/SignUp";
import "./App.css";
import { Route, Switch } from "react-router-dom";
import SignIn from "./pages/signIn/SignIn";

function App() {
  return (
    <Switch>
      <Route path="/" exact>
        <SignIn />
      </Route>
      <Route path="/register">
        <SignUp />
      </Route>
    </Switch>
  );
}

export default App;
