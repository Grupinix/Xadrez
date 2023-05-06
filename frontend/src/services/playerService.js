const headers = { "Content-Type": "application/json" };
const url = import.meta.env.PROD
  ? "https://xadrez.eterniaserver.com.br/api/player"
  : "http://localhost:8000/api/player";

export default {
  verify(playerDto) {
    return fetch(`${url}/verify/`, {
      method: "PUT",
      headers: headers,
      body: JSON.stringify(playerDto),
    });
  },

  register(identifier) {
    return fetch(`${url}/${identifier}/`, {
      method: "PUT",
      headers: headers,
    });
  },

  get(uuid) {
    return fetch(`${url}/${uuid}/`, {
      method: "GET",
      headers: headers,
    });
  },
};
