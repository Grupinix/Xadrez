package br.com.eterniaserver.xadrez.domain.service;

import br.com.eterniaserver.xadrez.domain.entities.Game;
import br.com.eterniaserver.xadrez.domain.entities.Piece;
import br.com.eterniaserver.xadrez.domain.enums.GameStatus;
import br.com.eterniaserver.xadrez.domain.enums.MoveType;
import br.com.eterniaserver.xadrez.rest.dtos.GameDto;
import org.springframework.data.util.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface GameService {

    default List<GameDto> convertToDtoList(List<Game> gameList) {
        List<GameDto> gameDtoList = new ArrayList<>();

        for (Game game : gameList) {
            GameDto gameDto = GameDto.builder()
                    .id(game.getId())
                    .gameType(game.getGameType())
                    .gameDifficulty(game.getGameDifficulty())
                    .whiteTurn(game.getWhiteTurn())
                    .whitePlayerUUID(game.getWhitePlayerUUID())
                    .whiteMoves(game.getWhiteMoves())
                    .blackPlayerUUID(game.getBlackPlayerUUID())
                    .blackMoves(game.getBlackMoves())
                    .build();

            gameDtoList.add(gameDto);
        }

        return gameDtoList;
    }

    List<GameDto> getAllGames();

    List<GameDto> getGames();

    GameDto createGame(UUID whiteUUID);

    GameDto enterGame(UUID blackUUID, Integer gameId);

    Map<Pair<Integer, Integer>, List<Pair<MoveType, Pair<Integer, Integer>>>> getPossibleMoves(
            Game game,
            UUID playerUUID
    );

    GameStatus getGameStatus(GameDto game);

    GameDto movePiece(GameDto game, UUID playerUUID, Piece piece, Pair<MoveType, Pair<Integer, Integer>> moveTypePairPair);

}
