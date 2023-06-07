package br.com.eterniaserver.xadrez.domain.service.impl;

import br.com.eterniaserver.xadrez.domain.entities.Board;
import br.com.eterniaserver.xadrez.domain.entities.Game;
import br.com.eterniaserver.xadrez.domain.entities.History;
import br.com.eterniaserver.xadrez.domain.entities.Piece;
import br.com.eterniaserver.xadrez.domain.enums.GameType;
import br.com.eterniaserver.xadrez.domain.repositories.BoardRepository;
import br.com.eterniaserver.xadrez.domain.repositories.GameRepository;
import br.com.eterniaserver.xadrez.domain.repositories.HistoryRepository;
import br.com.eterniaserver.xadrez.domain.repositories.PieceRepository;
import br.com.eterniaserver.xadrez.domain.service.BoardService;
import br.com.eterniaserver.xadrez.domain.service.GameService;
import br.com.eterniaserver.xadrez.rest.dtos.GameDto;
import br.com.eterniaserver.xadrez.rest.dtos.MoveDto;
import br.com.eterniaserver.xadrez.rest.dtos.PieceDto;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;


@Service("classicPPGameService")
@AllArgsConstructor
public class ClassicPPGameServiceImpl implements GameService {

    private final BoardService boardService;
    private final GameRepository gameRepository;
    private final HistoryRepository historyRepository;
    private final PieceRepository pieceRepository;
    private final BoardRepository boardRepository;

    @Override
    public List<Game> getAllGames() {
        return gameRepository.findAllByBlackPlayerUUIDIsNull();
    }

    @Override
    public Game getGame(Integer gameId) throws ResponseStatusException {
        return gameRepository.findById(gameId).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Partida online não encontrada")
        );
    }

    @Override
    public List<Game> getGames() {
        return gameRepository.findAllByBlackPlayerUUIDIsNullAndGameTypeEquals(
                GameType.PLAYER_PLAYER_CLASSIC
        );
    }

    @Override
    public Game createGame(UUID whiteUUID) {
        Board board = boardService.createBoard();
        Game game = new Game();

        game.setGameType(GameType.PLAYER_PLAYER_CLASSIC);
        game.setBoard(board);
        game.setWhiteMoves(0);
        game.setBlackMoves(0);
        game.setWhiteTurn(true);
        game.setWhitePlayerUUID(whiteUUID);
        game.setTimer(System.currentTimeMillis());

        gameRepository.save(game);

        return game;
    }

    @Override
    public boolean checkGame(Integer gameId) {
        return gameRepository.existsById(gameId);
    }

    @Override
    public void refreshGameTimer(Integer gameId) {
        throw new ResponseStatusException(HttpStatus.NOT_IMPLEMENTED, "Não é possível pedir mais tempo");
    }

    @Override
    @Transactional
    public Game enterGame(UUID playerUUID, Integer gameId) {
        Game game = gameRepository.findById(gameId).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Partida não encontrada")
        );

        if (game.getBlackPlayerUUID() != null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Partida já possui dois jogadores");
        }

        game.setBlackPlayerUUID(playerUUID);
        game.setTimer(System.currentTimeMillis());

        gameRepository.save(game);

        return game;
    }

    private void verifyAction(GameDto gameDto, UUID playerUUID) throws ResponseStatusException {
        if (gameDto.getBlackPlayerUUID() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Partida não possui dois jogadores");
        }

        boolean isWhiteTurn = gameDto.getWhiteTurn();
        boolean isWhitePlayer = gameDto.getWhitePlayerUUID().equals(playerUUID);
        if (isWhitePlayer != isWhiteTurn) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Não é turno do jogador");
        }
    }

    @Override
    public List<MoveDto> getPossibleMoves(GameDto gameDto,
                                          PieceDto piece,
                                          UUID playerUUID) throws ResponseStatusException {
        verifyAction(gameDto, playerUUID);

        return getPiecePossibleMoves(gameDto, piece, gameDto.getWhitePlayerUUID().equals(playerUUID));
    }

    @Override
    public Game movePiece(Game game,
                          UUID playerUUID,
                          Piece piece,
                          MoveDto moveTypePairPair) throws ResponseStatusException {
        verifyAction(game.getGameDto(), playerUUID);

        movePieceOnGame(game, piece, moveTypePairPair, game.getWhitePlayerUUID().equals(playerUUID));

        return game;

    }

    @Override
    public void deleteEntity(Piece piece) {
        pieceRepository.delete(piece);
    }

    @Override
    public void saveEntities(Game game, History history, Piece piece, Board board) {
        pieceRepository.save(piece);
        boardRepository.save(board);
        gameRepository.save(game);
        historyRepository.save(history);
    }

}
