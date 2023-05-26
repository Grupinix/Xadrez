package br.com.eterniaserver.xadrez.domain.service.impl;

import br.com.eterniaserver.xadrez.Constants;
import br.com.eterniaserver.xadrez.domain.entities.Board;
import br.com.eterniaserver.xadrez.domain.entities.Game;
import br.com.eterniaserver.xadrez.domain.entities.History;
import br.com.eterniaserver.xadrez.domain.entities.Piece;
import br.com.eterniaserver.xadrez.domain.enums.GameDifficulty;
import br.com.eterniaserver.xadrez.domain.enums.GameType;
import br.com.eterniaserver.xadrez.domain.enums.MoveType;
import br.com.eterniaserver.xadrez.domain.ia.impl.GameIaImpl;
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
import br.com.eterniaserver.xadrez.rest.dtos.PositionDto;
import lombok.AllArgsConstructor;
import org.springframework.data.util.Pair;
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

    private final GameIaImpl gameIa;

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

        boolean isWhite = checkTurn(game, playerUUID);
        Board board = game.getBoard();
        MoveType moveType = moveTypePairPair.getFirst();
        PositionDto position = moveTypePairPair.getSecond();
        History history = createHistory(board, piece, position, isWhite);

        if (moveType == MoveType.CAPTURE) {
            handleCapture(board, position, history, isWhite);
        }

        piece.setPositionX(position.getFirst());
        piece.setPositionY(position.getSecond());
        game.setWhiteTurn(!game.getWhiteTurn());
        game.setTimer(System.currentTimeMillis());
        board.getHistories().add(history);

        saveEntities(game, history, piece, board);

        if (playerUUID != null) {
            callIaMove(playerUUID, game.getId());
        }

        return game;
    }

    private void callIaMove(final UUID uuid, final int gameId) {
        gameIa.movePiece(gameId, uuid, this);
    }

    private boolean checkTurn(Game game, UUID playerUUID) throws ResponseStatusException {
        boolean isWhite = game.getWhitePlayerUUID().equals(playerUUID);
        if (!game.getWhiteTurn().equals(isWhite)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Não é turno do jogador");
        }
        return isWhite;
    }

    private History createHistory(Board board, Piece piece, PositionDto position, boolean isWhite) {
        History history = new History();
        history.setOldPositionX(piece.getPositionX());
        history.setOldPositionY(piece.getPositionY());
        history.setNewPositionX(position.getFirst());
        history.setNewPositionY(position.getSecond());
        history.setBoard(board);
        history.setIsWhite(isWhite);
        history.setPieceType(piece.getPieceType());
        return history;
    }

    private void handleCapture(Board board,
                               PositionDto position,
                               History history,
                               boolean isWhite) throws ResponseStatusException {

        Integer[][][] pieceMatrix = board.getPieceMatrix();
        Integer[] pieceInPos = pieceMatrix[position.getFirst()][position.getSecond()];

        if (pieceInPos == null || pieceInPos[0] == null || pieceInPos[0] == 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Não há peça na posição");
        }

        int capturedPieceId = pieceInPos[0];
        boolean capturedPieceIsWhite = pieceInPos[1] == Constants.WHITE_COLOR;

        if (capturedPieceIsWhite && isWhite || !capturedPieceIsWhite && !isWhite) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Não pode capturar sua própria peça");
        }

        removeCapturedPiece(board, capturedPieceId, history, isWhite);

    }

    private void removeCapturedPiece(Board board,
                                     Integer capturedPieceId,
                                     History history,
                                     boolean isWhite) throws ResponseStatusException {

        List<Piece> pieceList = isWhite ? board.getBlackPieces() : board.getWhitePieces();
        Pair<Integer, Piece> piecePair = null;
        for (int i = 0; i < pieceList.size(); i++) {
            Piece p = pieceList.get(i);
            if (p != null && p.getId().equals(capturedPieceId)) {
                piecePair = Pair.of(i, p);
            }
        }

        if (piecePair == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Erro ao encontrar a peça capturada");
        }

        int removeIndex = piecePair.getFirst();
        Piece capturedPiece = piecePair.getSecond();
        history.setKilledPiece(capturedPiece.getPieceType());
        pieceList.remove(removeIndex);

        pieceRepository.delete(capturedPiece);

    }

    private void saveEntities(Game game, History history, Piece piece, Board board) {
        gameRepository.save(game);
        historyRepository.save(history);
        pieceRepository.save(piece);
        boardRepository.save(board);
    }

}
