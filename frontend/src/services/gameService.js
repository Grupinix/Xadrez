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

  get(type, gameId) {
    return fetch(`${url}/get/${type}/${gameId}/`, {
      method: "GET",
      headers: headers,
    });
  },

  movePiece(type, gameId, uuid, pieceId, moveDto) {
    return fetch(`${url}/move/${type}/${gameId}/${uuid}/${pieceId}/`, {
      method: "POST",
      headers: headers,
      body: JSON.stringify(moveDto),
    });
  },

  getPiecePossibleMoves(type, gameId, uuid, pieceDto) {
    return fetch(`${url}/getPiecePossibleMoves/${type}/${gameId}/${uuid}/`, {
      method: "PUT",
      headers: headers,
      body: JSON.stringify(pieceDto),
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
