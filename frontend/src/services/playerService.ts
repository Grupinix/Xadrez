const headers = { "Content-Type": "application/json" };
const url = import.meta.env.PROD
  ? "https://xadrez.eterniaserver.com.br/api/player"
  : "http://localhost:8000/api/player";

export default {
  verify(playerDto: PlayerDto): Promise<Response> {
    return fetch(`${url}/verify/`, {
      method: "PUT",
      headers: headers,
      body: JSON.stringify(playerDto),
    });
  },

  register(identifier: string): Promise<Response> {
    return fetch(`${url}/${identifier}/`, {
      method: "PUT",
      headers: headers,
    });
  },

  get(uuid: string): Promise<Response> {
    return fetch(`${url}/${uuid}/`, {
      method: "GET",
      headers: headers,
    });
  },

  setGameDifficulty(difficulty: string, playerDto: PlayerDto): Promise<Response> {
    return fetch(`${url}/setDifficulty/${difficulty}/`, {
      method: "PUT",
      headers: headers,
      body: JSON.stringify(playerDto),
    });
  },

  getPlayerDtoFromStorage(): PlayerDto {
    const playerDtoStringed = localStorage.getItem("playerDto") || "";
    return JSON.parse(playerDtoStringed);
  },
};
