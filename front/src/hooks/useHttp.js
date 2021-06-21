import { useContext, useState } from "react";
import { BASE_URL } from "../constants/paths";
import AuthContext from "../context/auth-context";

function useHttp() {
  const [error, setError] = useState(null);
  const [result, setResult] = useState(null);
  const [isLoading, setIsLoading] = useState(false);
  const authCtx = useContext(AuthContext);

  async function sendRequest(config = {}) {
    const bearer =
      authCtx.isLoggedIn && authCtx.token.trim().length > 0
        ? { Authorization: `Bearer ${authCtx.token}` }
        : {};
    const defaultConfig = Object.assign(
      {
        url: BASE_URL,
        method: "GET",
        headers: Object.assign(
          {
            "Content-Type": "application/json",
          },
          bearer,
          config?.headers
        ),
        errorMsg: "Couldn't complete fetch",
      },
      config
    );

    try {
      setError(null);
      setResult(null);
      setIsLoading(true);
      const response = await fetch(defaultConfig.url, {
        method: defaultConfig.method,
        body: JSON.stringify(defaultConfig.body),
        headers: defaultConfig.headers,
      });
      if (!response.ok) throw new Error(defaultConfig.errorMsg);
      const data = await response.json();
      setResult(data);
    } catch (err) {
      setError(err);
    } finally {
      setIsLoading(false);
    }
  }

  return {
    result,
    error,
    isLoading,
    sendRequest,
  };
}

export default useHttp;
