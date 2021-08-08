import styles from "./Header.module.css";
import { useContext } from "react";
import AuthContext from "../../context/auth-context";
import { CgLogOut, CgArrowLeft } from "react-icons/cg";
import { useHistory } from "react-router-dom";

function Header(props) {
  const authCtx = useContext(AuthContext);
  const history = useHistory();
  function handleGoBackClick() {
    if (props.caller) history.push(props.caller);
    else history.push("/");
  }
  return (
    <header className={`${styles.header} ${props.className}`}>
      {props.backButton && (
        <div>
          <i onClick={handleGoBackClick}>
            <CgArrowLeft />
          </i>
        </div>
      )}
      <div>{props.children}</div>
      <div>
        {props.logout && (
          <i onClick={authCtx.onLogout} title="Logout">
            <CgLogOut />
          </i>
        )}
      </div>
    </header>
  );
}

export default Header;
