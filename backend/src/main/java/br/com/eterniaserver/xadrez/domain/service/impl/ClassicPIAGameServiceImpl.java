package br.com.eterniaserver.xadrez.domain.service.impl;

import br.com.eterniaserver.xadrez.domain.entities.Board;
import br.com.eterniaserver.xadrez.domain.entities.Game;
import br.com.eterniaserver.xadrez.domain.entities.Piece;
import br.com.eterniaserver.xadrez.domain.enums.GameDifficulty;
import br.com.eterniaserver.xadrez.domain.enums.GameStatus;
import br.com.eterniaserver.xadrez.domain.enums.GameType;
import br.com.eterniaserver.xadrez.domain.enums.MoveType;
import br.com.eterniaserver.xadrez.domain.repositories.GameRepository;
import br.com.eterniaserver.xadrez.domain.service.BoardService;
import br.com.eterniaserver.xadrez.domain.service.GameService;
import lombok.AllArgsConstructor;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service("classicPIAGameService")
@AllArgsConstructor
public class ClassicPIAGameServiceImpl implements GameService {

    private final GameRepository gameRepository;
    private final BoardService boardService;

    @Override
    public List<Game> getAllGames() {
        return gameRepository.findAllByBlackPlayerUUIDIsNull();
    }

    @Override
    public List<Game> getGames() {
        return gameRepository.findAllByBlackPlayerUUIDIsNullAndGameTypeEquals(
                GameType.PLAYER_IA_CLASSIC
        );
    }

    @Override
    public Game enterGame(UUID blackUUID, Integer gameId) {
        return null;
    }

    public Game createGame(UUID whiteUUID) {
        Board board = boardService.createBoard();
        Game game = new Game();

        game.setGameType(GameType.PLAYER_IA_CLASSIC);
        game.setGameDifficulty(GameDifficulty.NORMAL);
        game.setBoard(board);
        game.setWhiteTurn(true);
        game.setWhitePlayerUUID(whiteUUID);
        game.setWhiteMoves(0);
        game.setBlackMoves(0);

        gameRepository.save(game);

        return game;
    }

    @Override
    public Map<Pair<Integer, Integer>, List<Pair<MoveType, Pair<Integer, Integer>>>> getPossibleMoves(
            Game game,
            UUID playerUUID
    ) {
        return null;
    }

    @Override
    public GameStatus getGameStatus(Game game) {
        return null;
    }

    @Override
    public Game movePiece(
            Game game,
            UUID playerUUID,
            Piece piece,
            Pair<MoveType, Pair<Integer, Integer>> moveTypePairPair
    ) {
        return null;
    }

}
