import { useCallback, useContext, useState } from "react";
import { BASE_URL } from "../constants/paths";
import AuthContext from "../context/auth-context";

function HttpException(response = { status: 500, body: {} }) {
  this.body = response.body;
  this.status = response.status;
}

function useHttp() {
  const [error, setError] = useState(null);
  const [result, setResult] = useState(null);
  const [isLoading, setIsLoading] = useState(false);
  const authCtx = useContext(AuthContext);

  const sendRequest = useCallback(
    async (config = {}) => {
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
        const data = await response.json();
        if (!response.ok) {
          throw new HttpException({ status: response.status, body: data });
        }
        setResult(data);
      } catch (err) {
        setError(err);
      } finally {
        setIsLoading(false);
      }
    },
    [authCtx.isLoggedIn, authCtx.token]
  );

  return {
    result,
    error,
    isLoading,
    sendRequest,
  };
}

export default useHttp;
