package br.com.eterniaserver.xadrez.domain.service;

import br.com.eterniaserver.xadrez.domain.entities.Game;
import br.com.eterniaserver.xadrez.domain.entities.Piece;
import br.com.eterniaserver.xadrez.domain.enums.GameStatus;
import br.com.eterniaserver.xadrez.domain.enums.MoveType;

import org.springframework.data.util.Pair;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface GameService {

    List<Game> getAllGames();

    List<Game> getGames();

    Game createGame(UUID whiteUUID);

    Game enterGame(UUID blackUUID, Integer gameId);

    List<Pair<MoveType, Pair<Integer, Integer>>> getPossibleMoves(Game game, Piece piece, UUID playerUUID);

    GameStatus getGameStatus(Game game);

    Game movePiece(Game game, UUID playerUUID, Piece piece, Pair<MoveType, Pair<Integer, Integer>> moveTypePairPair);

}
