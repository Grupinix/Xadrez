const headers = { "Content-Type": "application/json" };
const url = import.meta.env.PROD
  ? "https://xadrez.eterniaserver.com.br/api/game"
  : "http://localhost:8000/api/game";

export default {
  check(type, gameId) {
    return fetch(`${url}/check/${type}/${gameId}/`, {
      method: "GET",
      headers: headers,
    });
  },

  refreshTimer(type, gameId) {
    return fetch(`${url}/refresh/${type}/${gameId}/`, {
      method: "GET",
      headers: headers,
    });
  },

  list() {
    return fetch(`${url}/list/`, {
      method: "GET",
      headers: headers,
    });
  },

  listByType(type) {
    return fetch(`${url}/list/${type}/`, {
      method: "GET",
      headers: headers,
    });
  },

  create(type, playerDto) {
    return fetch(`${url}/create/${type}/`, {
      method: "POST",
      headers: headers,
      body: JSON.stringify(playerDto),
    });
  },
};