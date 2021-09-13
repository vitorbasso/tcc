import styles from "./Header.module.css";
import { CgArrowLeft } from "react-icons/cg";
import { useHistory } from "react-router-dom";
import DropdownMenu from "../menu/DropdownMenu";

function Header(props) {
  const history = useHistory();
  function handleGoBackClick() {
    if (props.caller) history.push(props.caller);
    else history.push("/");
  }
  return (
    <header id="header" className={`${styles.header} ${props.className}`}>
      {props.backButton && (
        <div>
          <i onClick={handleGoBackClick}>
            <CgArrowLeft />
          </i>
        </div>
      )}
      <div>{props.children}</div>
      <div>{props.logout && <DropdownMenu />}</div>
    </header>
  );
}

export default Header;
