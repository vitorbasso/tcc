import { useEffect } from "react";
import LoadingOverlay from "../../components/loading-overlay/LoadingOverlay";
import { CLIENTS_URL } from "../../constants/paths";
import useHttp from "../../hooks/useHttp";
import Header from "../../components/header/Header";
import baseStyles from "../../css/base.module.css";

function Home() {
  const { result, error, isLoading, sendRequest } = useHttp();

  useEffect(() => {
    sendRequest({
      url: CLIENTS_URL,
    });
  }, []);

  return (
    <>
      {isLoading && <LoadingOverlay />}
      {!isLoading && result && (
        <div className={baseStyles.container}>
          <Header logout>
            <h3>Bem Vindo,</h3>
            <h2>{result.name?.split(" ")?.[0]}</h2>
          </Header>
          <main></main>
        </div>
      )}
    </>
  );
}

export default Home;
