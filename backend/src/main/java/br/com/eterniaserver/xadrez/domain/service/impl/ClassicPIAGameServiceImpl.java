package br.com.eterniaserver.xadrez.domain.service.impl;

import br.com.eterniaserver.xadrez.domain.entities.Board;
import br.com.eterniaserver.xadrez.domain.entities.Game;
import br.com.eterniaserver.xadrez.domain.entities.History;
import br.com.eterniaserver.xadrez.domain.entities.Piece;
import br.com.eterniaserver.xadrez.domain.enums.GameDifficulty;
import br.com.eterniaserver.xadrez.domain.enums.GameType;
import br.com.eterniaserver.xadrez.domain.repositories.BoardRepository;
import br.com.eterniaserver.xadrez.domain.repositories.GameRepository;
import br.com.eterniaserver.xadrez.domain.repositories.HistoryRepository;
import br.com.eterniaserver.xadrez.domain.repositories.PieceRepository;
import br.com.eterniaserver.xadrez.domain.service.BoardService;
import br.com.eterniaserver.xadrez.domain.service.GameService;
import br.com.eterniaserver.xadrez.domain.service.PlayerService;
import br.com.eterniaserver.xadrez.rest.dtos.GameDto;
import br.com.eterniaserver.xadrez.rest.dtos.MoveDto;
import br.com.eterniaserver.xadrez.rest.dtos.PieceDto;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service("classicPIAGameService")
@AllArgsConstructor
public class ClassicPIAGameServiceImpl implements GameService {

    private final BoardService boardService;
    private final GameRepository gameRepository;
    private final HistoryRepository historyRepository;
    private final PieceRepository pieceRepository;
    private final BoardRepository boardRepository;
    private final PlayerService playerService;

    @Override
    public PlayerService getPlayerService() {
        return playerService;
    }

    @Override
    public List<Game> getAllGames() {
        return null;
    }

    @Override
    public Game getGame(Integer gameId) throws ResponseStatusException {
        return gameRepository.findById(gameId).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Partida não encontrada")
        );
    }

    @Override
    public List<Game> getGames() {
        return null;
    }

    @Override
    public Game enterGame(UUID blackUUID, Integer gameId) {
        return null;
    }

    @Transactional
    public Game createGame(UUID whiteUUID) {
        Board board = boardService.createBoard();
        Game game = new Game();

        GameDifficulty gameDifficulty = playerService.getGameDifficulty(whiteUUID);

        game.setGameType(GameType.PLAYER_IA_CLASSIC);
        game.setGameDifficulty(gameDifficulty);
        game.setBoard(board);
        game.setWhiteTurn(true);
        game.setWhitePlayerUUID(whiteUUID);
        game.setWhiteMoves(0);
        game.setBlackMoves(0);
        game.setTimer(System.currentTimeMillis());

        gameRepository.save(game);

        return game;
    }

    @Override
    public boolean checkGame(Integer gameId) {
        return gameRepository.existsById(gameId);
    }

    @Override
    @Transactional
    public void refreshGameTimer(Integer gameId) {
        Optional<Game> optionalGame = gameRepository.findById(gameId);

        if (optionalGame.isPresent()) {
            Game game = optionalGame.get();
            game.setTimer(System.currentTimeMillis());
            gameRepository.save(game);
        }
    }

    @Override
    public List<MoveDto> getPossibleMoves(GameDto game, PieceDto piece, UUID uuid) {
        boolean isWhite = game.getWhitePlayerUUID().equals(uuid);
        return getPiecePossibleMoves(game, piece, isWhite);
    }

    @Override
    @Transactional
    public Game movePiece(Game game,
                          UUID playerUUID,
                          Piece piece,
                          MoveDto moveTypePairPair) throws ResponseStatusException {
        boolean isWhite = game.getWhitePlayerUUID().equals(playerUUID);
        movePieceOnGame(game, piece, moveTypePairPair, isWhite);
        return game;
    }

    @Override
    public void deleteEntity(Piece piece) {
        pieceRepository.delete(piece);
    }

    @Override
    public void saveEntities(Game game, History history, Piece piece, Board board) {
        gameRepository.save(game);
        historyRepository.save(history);
        pieceRepository.save(piece);
        boardRepository.save(board);
    }

}
