package br.com.eterniaserver.xadrez.domain.ia;

import br.com.eterniaserver.xadrez.domain.service.GameService;

import java.util.UUID;

public interface GameIa {

    void movePiece(Integer gameId, UUID uuid, GameService gameService);

}
