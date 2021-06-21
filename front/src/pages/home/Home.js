import { useContext, useEffect } from "react";
import LoadingOverlay from "../../components/loading-overlay/LoadingOverlay";
import { CLIENTS_URL } from "../../constants/paths";
import AuthContext from "../../context/auth-context";
import useHttp from "../../hooks/useHttp";

function Home() {
  const authCtx = useContext(AuthContext);
  const { result, error, isLoading, sendRequest } = useHttp();

  useEffect(() => {
    sendRequest({
      url: CLIENTS_URL,
    });
  }, []);

  return (
    <div>
      {isLoading && <LoadingOverlay />}
      <p>Home Page</p>
      {!isLoading && result && (
        <p>
          id: {result.id} - Email: {result.email} - Nome: {result.name}
        </p>
      )}
      <button onClick={authCtx.onLogout}>logout</button>
    </div>
  );
}

export default Home;
