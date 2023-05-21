const headers = { "Content-Type": "application/json" };
const url = import.meta.env.PROD
  ? "https://xadrez.eterniaserver.com.br/api/game"
  : "http://localhost:8000/api/game";

export default {
  check(type: string, gameId: number): Promise<Response> {
    return fetch(`${url}/check/${type}/${gameId}/`, {
      method: "GET",
      headers: headers,
    });
  },

  get(type: string, gameId: number): Promise<Response> {
    return fetch(`${url}/get/${type}/${gameId}/`, {
      method: "GET",
      headers: headers,
    });
  },

  movePiece(type: string, gameId: number, uuid: string, pieceId: number, moveDto: object): Promise<Response> {
    return fetch(`${url}/move/${type}/${gameId}/${uuid}/${pieceId}/`, {
      method: "POST",
      headers: headers,
      body: JSON.stringify(moveDto),
    });
  },

  getPiecePossibleMoves(type: string, gameId: number, uuid: string, pieceDto: PieceDto): Promise<Response> {
    return fetch(`${url}/getPiecePossibleMoves/${type}/${gameId}/${uuid}/`, {
      method: "PUT",
      headers: headers,
      body: JSON.stringify(pieceDto),
    });
  },

  getGameStatus(type: string, gameId: number): Promise<Response> {
    return fetch(`${url}/status/${type}/${gameId}/`, {
      method: "GET",
      headers: headers,
    });
  },

  refreshTimer(type: string, gameId: number): Promise<Response> {
    return fetch(`${url}/refresh/${type}/${gameId}/`, {
      method: "GET",
      headers: headers,
    });
  },

  list(): Promise<Response> {
    return fetch(`${url}/list/`, {
      method: "GET",
      headers: headers,
    });
  },

  listByType(type: string): Promise<Response> {
    return fetch(`${url}/list/${type}/`, {
      method: "GET",
      headers: headers,
    });
  },

  create(type: string, playerDto: PlayerDto): Promise<Response> {
    return fetch(`${url}/create/${type}/`, {
      method: "POST",
      headers: headers,
      body: JSON.stringify(playerDto),
    });
  },

  getIaGameDtoFromStorage(): GameDto {
    const iaGameDtoStringed = localStorage.getItem("iaGameDto") || "";
    return JSON.parse(iaGameDtoStringed);
  }
};
