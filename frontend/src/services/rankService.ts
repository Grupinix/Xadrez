const headers = { "Content-Type": "application/json" };
const url = import.meta.env.PROD
  ? "https://xadrez.eterniaserver.com.br/api/game"
  : "http://localhost:8000/api/rank";

export default {
  list(): Promise<Response> {
    return fetch(`${url}/`, {
      method: "GET",
      headers: headers,
    });
  },
};