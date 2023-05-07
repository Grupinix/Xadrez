package br.com.eterniaserver.xadrez.domain.service.impl;

import br.com.eterniaserver.xadrez.domain.entities.Board;
import br.com.eterniaserver.xadrez.domain.entities.Game;
import br.com.eterniaserver.xadrez.domain.entities.History;
import br.com.eterniaserver.xadrez.domain.entities.Piece;
import br.com.eterniaserver.xadrez.domain.enums.GameDifficulty;
import br.com.eterniaserver.xadrez.domain.enums.GameStatus;
import br.com.eterniaserver.xadrez.domain.enums.GameType;
import br.com.eterniaserver.xadrez.domain.enums.MoveType;
import br.com.eterniaserver.xadrez.domain.repositories.BoardRepository;
import br.com.eterniaserver.xadrez.domain.repositories.GameRepository;
import br.com.eterniaserver.xadrez.domain.repositories.HistoryRepository;
import br.com.eterniaserver.xadrez.domain.repositories.PieceRepository;
import br.com.eterniaserver.xadrez.domain.service.BoardService;
import br.com.eterniaserver.xadrez.domain.service.GameService;
import br.com.eterniaserver.xadrez.rest.dtos.MoveDto;
import br.com.eterniaserver.xadrez.rest.dtos.PositionDto;
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

    @Override
    public List<Game> getAllGames() {
        return gameRepository.findAllByBlackPlayerUUIDIsNull();
    }

    @Override
    public Game getGame(Integer gameId) {
        return gameRepository.findById(gameId).orElse(null);
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

    @Transactional
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
    public List<MoveDto> getPossibleMoves(Game game, Piece piece, UUID uuid) {
        boolean isWhite = game.getWhitePlayerUUID().equals(uuid);
        return getPiecePossibleMoves(game, piece, isWhite);
    }

    @Override
    public GameStatus getGameStatus(Integer gameId) {
        return null;
    }

    @Override
    @Transactional
    public Game movePiece(Game game,
                          UUID playerUUID,
                          Piece piece,
                          MoveDto moveTypePairPair) throws ResponseStatusException {

        boolean isWhite = game.getWhitePlayerUUID().equals(playerUUID);
        if (isWhite != game.getWhiteTurn()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Não é turno do jogador");
        }

        Board board = game.getBoard();
        MoveType moveType = moveTypePairPair.getFirst();
        PositionDto position = moveTypePairPair.getSecond();

        History history = new History();
        history.setOldPositionX(piece.getPositionX());
        history.setOldPositionY(piece.getPositionY());
        history.setNewPositionX(position.getFirst());
        history.setNewPositionY(position.getSecond());
        history.setBoard(board);
        history.setIsWhite(isWhite);
        history.setPieceType(piece.getPieceType());

        if (moveType == MoveType.CAPTURE) {
            Integer[][][] pieceMatrix = board.getPieceMatrix();
            Integer[] pieceInPos = pieceMatrix[position.getFirst()][position.getSecond()];

            if (pieceInPos == null || pieceInPos[0] == null || pieceInPos[0] == 0) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Não há peça na posição");
            }

            Integer capturedPieceId = pieceInPos[0];
            Integer capturedPieceIsWhite = pieceInPos[1];

            if (capturedPieceIsWhite == 1 && isWhite || capturedPieceIsWhite == 0 && !isWhite) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Não pode capturar sua própria peça");
            }

            List<Piece> pieceList = isWhite ? board.getBlackPieces() : board.getWhitePieces();
            Piece capturedPiece = null;
            int removeIndex = -1;
            for (int i = 0; i < pieceList.size(); i++) {
                Piece p = pieceList.get(i);
                if (p != null && p.getId().equals(capturedPieceId)) {
                    capturedPiece = p;
                    removeIndex = i;
                }
            }

            if (removeIndex == -1) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Erro ao encontrar a peça capturada");
            }

            history.setKilledPiece(capturedPiece.getPieceType());
            pieceList.remove(removeIndex);
            pieceRepository.delete(capturedPiece);
        }

        piece.setPositionX(position.getFirst());
        piece.setPositionY(position.getSecond());

        game.setWhiteTurn(!game.getWhiteTurn());
        game.setTimer(System.currentTimeMillis());

        board.getHistories().add(history);

        gameRepository.save(game);
        historyRepository.save(history);
        pieceRepository.save(piece);
        boardRepository.save(board);

        return game;

    }

}
