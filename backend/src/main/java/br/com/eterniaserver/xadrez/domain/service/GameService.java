package br.com.eterniaserver.xadrez.domain.service;

import br.com.eterniaserver.xadrez.Constants;
import br.com.eterniaserver.xadrez.domain.entities.Game;
import br.com.eterniaserver.xadrez.domain.entities.Piece;
import br.com.eterniaserver.xadrez.domain.enums.GameStatus;
import br.com.eterniaserver.xadrez.domain.enums.MoveType;

import br.com.eterniaserver.xadrez.domain.enums.PieceType;
import br.com.eterniaserver.xadrez.rest.dtos.BoardDto;
import br.com.eterniaserver.xadrez.rest.dtos.GameDto;
import br.com.eterniaserver.xadrez.rest.dtos.MoveDto;
import br.com.eterniaserver.xadrez.rest.dtos.PieceDto;
import br.com.eterniaserver.xadrez.rest.dtos.PositionDto;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public interface GameService {

    default List<MoveDto> getPiecePossibleMoves(GameDto game,
                                                PieceDto piece,
                                                boolean isWhite) {

        List<PositionDto> moves = new ArrayList<>();
        List<MoveDto> possibleMoves = new ArrayList<>();
        Integer[][][] pieceMatrix = game.getBoard().getPieceMatrix();

        int[] rowCol = new int[] { piece.getPositionX(), piece.getPositionY() };

        switch (piece.getPieceType()) {
            case KING -> addKingMoves(pieceMatrix, rowCol, moves, isWhite);
            case QUEEN -> addQueenMoves(pieceMatrix, rowCol, moves, isWhite);
            case TOWER -> addMovesInAllDirections(pieceMatrix, rowCol, moves, 7, isWhite);
            case BISHOP -> addDiagonalMoves(pieceMatrix, rowCol, moves, 7, isWhite);
            case HORSE -> addHorseMoves(pieceMatrix, rowCol, moves, isWhite);
            case PAWN -> addPawnMoves(pieceMatrix, rowCol, moves, isWhite);
        }

        for (PositionDto move : moves) {
            Integer[] pieceInMatrix = pieceMatrix[move.getFirst()][move.getSecond()];
            if (pieceInMatrix == null || pieceInMatrix[0] == null || pieceInMatrix[0] == 0) {
                possibleMoves.add(new MoveDto(MoveType.NORMAL, move));
            }
            else if (pieceInMatrix[1] == Constants.WHITE_COLOR && !isWhite
                    || pieceInMatrix[1] == Constants.BLACK_COLOR && isWhite) {
                possibleMoves.add(new MoveDto(MoveType.CAPTURE, move));
            }
        }

        return possibleMoves;

    }

    private void addKingMoves(Integer[][][] pieceMatrix,
                               int[] rowCol,
                               List<PositionDto> moves,
                               boolean isWhite) {

        addMovesInAllDirections(pieceMatrix, rowCol, moves, 1, isWhite);
        addDiagonalMoves(pieceMatrix, rowCol, moves, 1, isWhite);

    }

    private void addQueenMoves(Integer[][][] pieceMatrix,
                               int[] rowCol,
                               List<PositionDto> moves,
                               boolean isWhite) {

        addMovesInAllDirections(pieceMatrix, rowCol, moves, 7, isWhite);
        addDiagonalMoves(pieceMatrix, rowCol, moves, 7, isWhite);

    }

    private void addMovesInAllDirections(Integer[][][] pieceMatrix,
                                         int[] rowCol,
                                         List<PositionDto> moves,
                                         int maxDistance,
                                         boolean isWhite) {

        addMovesInDirection(pieceMatrix, rowCol, moves, maxDistance, new int[] {1, 0}, isWhite);
        addMovesInDirection(pieceMatrix, rowCol, moves, maxDistance, new int[] {-1, 0}, isWhite);
        addMovesInDirection(pieceMatrix, rowCol, moves, maxDistance, new int[] {0, 1}, isWhite);
        addMovesInDirection(pieceMatrix, rowCol, moves, maxDistance, new int[] {0, -1}, isWhite);

    }

    private void addDiagonalMoves(Integer[][][] pieceMatrix,
                                  int[] rowCol,
                                  List<PositionDto> moves,
                                  int maxDistance,
                                  boolean isWhite) {

        addMovesInDirection(pieceMatrix, rowCol, moves, maxDistance, new int[] {1, 1}, isWhite);
        addMovesInDirection(pieceMatrix, rowCol, moves, maxDistance, new int[] {1, -1}, isWhite);
        addMovesInDirection(pieceMatrix, rowCol, moves, maxDistance, new int[] {-1, 1}, isWhite);
        addMovesInDirection(pieceMatrix, rowCol, moves, maxDistance, new int[] {-1, -1}, isWhite);

    }

    private void addPawnMoves(Integer[][][] pieceMatrix,
                              int[] rowCol,
                              List<PositionDto> moves,
                              boolean isWhite) {

        int multiplier = isWhite ? -1 : 1;

        if ((rowCol[0] == 1 && !isWhite) || (rowCol[0] == 6 && isWhite)) {
            addMovesInDirection(pieceMatrix, rowCol, moves, 1, new int[] {multiplier, 0}, isWhite, true);
            addMovesInDirection(pieceMatrix, rowCol, moves, 1, new int[] {2 * multiplier, 0}, isWhite, true);
        }
        else {
            addMovesInDirection(pieceMatrix, rowCol, moves, 1, new int[] {multiplier, 0}, isWhite, true);
        }

        addPawnCapture(pieceMatrix, rowCol, moves, true, isWhite);
        addPawnCapture(pieceMatrix, rowCol, moves, false, isWhite);

    }

    private void addHorseMoves(Integer[][][] pieceMatrix,
                               int[] rowCol,
                               List<PositionDto> moves,
                               boolean isWhite) {

        addMovesInDirection(pieceMatrix, rowCol, moves, 1, new int[] {1, 2}, isWhite);
        addMovesInDirection(pieceMatrix, rowCol, moves, 1, new int[] {2, 1}, isWhite);
        addMovesInDirection(pieceMatrix, rowCol, moves, 1, new int[] {-1, 2}, isWhite);
        addMovesInDirection(pieceMatrix, rowCol, moves, 1, new int[] {-2, 1}, isWhite);
        addMovesInDirection(pieceMatrix, rowCol, moves, 1, new int[] {1, -2}, isWhite);
        addMovesInDirection(pieceMatrix, rowCol, moves, 1, new int[] {2, -1}, isWhite);
        addMovesInDirection(pieceMatrix, rowCol, moves, 1, new int[] {-1, -2}, isWhite);
        addMovesInDirection(pieceMatrix, rowCol, moves, 1, new int[] {-2, -1}, isWhite);

    }

    private void addMovesInDirection(Integer[][][] pieceMatrix,
                                     int[] rowCol,
                                     List<PositionDto> moves,
                                     int length,
                                     int[] riCi,
                                     boolean isWhite) {
        addMovesInDirection(pieceMatrix, rowCol, moves, length, riCi, isWhite, false);
    }

    private void addMovesInDirection(Integer[][][] pieceMatrix,
                                     int[] rowCol,
                                     List<PositionDto> moves,
                                     int length,
                                     int[] riCi,
                                     boolean isWhite,
                                     boolean isPawn) {

        int row = rowCol[0];
        int col = rowCol[1];
        int ri = riCi[0];
        int ci = riCi[1];

        for (int i = 1; i <= length; i++) {
            int newRow = row + ri * i;
            int newCol = col + ci * i;
            if (newRow < 0 || newRow > 7 || newCol < 0 || newCol > 7) {
                break;
            }

            Integer[] pieceInMatrix = pieceMatrix[newRow][newCol];
            if (pieceInMatrix != null && pieceInMatrix[0] != null && pieceInMatrix[0] != 0) {
                if ((pieceInMatrix[1] == Constants.WHITE_COLOR && !isWhite
                        || pieceInMatrix[1] == Constants.BLACK_COLOR && isWhite) && !isPawn) {
                    moves.add(new PositionDto(newRow, newCol));
                }
                break;
            }
            moves.add(new PositionDto(newRow, newCol));
        }

    }

    private void addPawnCapture(Integer[][][] pieceMatrix,
                                int[] rowCol,
                                List<PositionDto> moves,
                                boolean left,
                                boolean isWhite) {
        int row = rowCol[0];
        int col = rowCol[1];

        int multiplier = isWhite ? -1 : 1;
        boolean canY = left ? col - 1 >= 0 : col + 1 <= 7;

        if (canY && ((row + multiplier) >= 0 && (row + multiplier) <= 7)) {
            int x = row + multiplier;
            int y = col + (left ? -1 : 1);

            Integer[] leftPiece = pieceMatrix[x][y];
            boolean hasPiece = leftPiece != null && leftPiece[0] != null;
            if (hasPiece && (leftPiece[1] == Constants.WHITE_COLOR && !isWhite
                    || leftPiece[1] == Constants.BLACK_COLOR && isWhite)) {
                moves.add(new PositionDto(x, y));
            }
        }
    }

    List<Game> getAllGames();

    Game getGame(Integer gameId) throws ResponseStatusException;

    List<Game> getGames();

    Game createGame(UUID whiteUUID);

    boolean checkGame(Integer gameId);

    void refreshGameTimer(Integer gameId);

    Game enterGame(UUID blackUUID, Integer gameId);

    List<MoveDto> getPossibleMoves(GameDto game, PieceDto piece, UUID playerUUID);

    default GameStatus getGameStatus(BoardDto boardDto, boolean isWhiteTurn) {
        List<PieceDto> whitePieces = boardDto.getWhitePieces();
        List<PieceDto> blackPieces = boardDto.getBlackPieces();

        Optional<PieceDto> whiteKingOptional = whitePieces.stream()
                                                          .filter(piece -> piece.getPieceType() == PieceType.KING)
                                                          .findFirst();
        Optional<PieceDto> blackKingOptional = blackPieces.stream()
                                                          .filter(piece -> piece.getPieceType() == PieceType.KING)
                                                          .findFirst();

        if (whiteKingOptional.isEmpty()) {
            return GameStatus.BLACK_WINS;
        }
        else if (blackKingOptional.isEmpty()) {
            return GameStatus.WHITE_WINS;
        }

        return GameStatus.NORMAL;
    }

    Game movePiece(Game game, UUID playerUUID, Piece piece, MoveDto moveTypePairPair) throws ResponseStatusException;

    default Map<PieceDto, List<MoveDto>> getPlayerLegalMoves(GameDto gameDto, int playerColor) {
        BoardDto boardDto = gameDto.getBoard();

        Map<PieceDto, List<MoveDto>> moveDtoMap = new HashMap<>();
        List<PieceDto> pieceDtoList = playerColor == 0 ? boardDto.getWhitePieces() : boardDto.getBlackPieces();
        UUID playerUUID = playerColor == 0 ? gameDto.getWhitePlayerUUID() : null;

        for (PieceDto piece : pieceDtoList) {
            moveDtoMap.put(piece, getPossibleMoves(gameDto, piece, playerUUID));
        }

        return moveDtoMap;
    }

}
