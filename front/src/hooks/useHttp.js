import { useState } from "react";

function useHttp() {
  const [isLoading, setIsLoading] = useState(false);

  async function sendRequest() {
    try {
      const response = await fetch("http://localhost:8080/v1/clients", {
        method: "POST",
        body: JSON.stringify({
          name: "Vitor Martins",
          email: "vitor@email.com",
          password: "012345678",
        }),
        headers: {
          "Content-Type": "application/json",
        },
      });
      if (!response.ok) throw new Error("Couldn't complete fetch");
      const data = await response.json();
      return data;
    } catch (err) {
      throw err;
    }
  }
}

export default useHttp;
