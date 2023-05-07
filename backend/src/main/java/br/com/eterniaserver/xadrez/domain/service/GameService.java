package br.com.eterniaserver.xadrez.domain.service;

import br.com.eterniaserver.xadrez.domain.entities.Game;
import br.com.eterniaserver.xadrez.domain.entities.Piece;
import br.com.eterniaserver.xadrez.domain.enums.GameStatus;
import br.com.eterniaserver.xadrez.domain.enums.MoveType;

import org.springframework.data.util.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public interface GameService {

    default List<Pair<MoveType, Pair<Integer, Integer>>> getPiecePossibleMoves(Game game,
                                                                               Piece piece,
                                                                               boolean isWhite) {

        List<Pair<Integer, Integer>> moves = new ArrayList<>();
        List<Pair<MoveType, Pair<Integer, Integer>>> possibleMoves = new ArrayList<>();
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

        for (Pair<Integer, Integer> move : moves) {
            Integer[] pieceInMatrix = pieceMatrix[move.getFirst()][move.getSecond()];
            if (pieceInMatrix == null || pieceInMatrix[0] == null || pieceInMatrix[0] == 0) {
                possibleMoves.add(Pair.of(MoveType.NORMAL, move));
            }
            else if (pieceInMatrix[1] == 1 && !isWhite || pieceInMatrix[1] == 0 && isWhite) {
                possibleMoves.add(Pair.of(MoveType.CAPTURE, move));
            }
        }

        return possibleMoves;

    }

    private void addKingMoves(Integer[][][] pieceMatrix,
                               int[] rowCol,
                               List<Pair<Integer, Integer>> moves,
                               boolean isWhite) {

        addMovesInAllDirections(pieceMatrix, rowCol, moves, 1, isWhite);
        addDiagonalMoves(pieceMatrix, rowCol, moves, 1, isWhite);

    }

    private void addQueenMoves(Integer[][][] pieceMatrix,
                               int[] rowCol,
                               List<Pair<Integer, Integer>> moves,
                               boolean isWhite) {

        addMovesInAllDirections(pieceMatrix, rowCol, moves, 7, isWhite);
        addDiagonalMoves(pieceMatrix, rowCol, moves, 7, isWhite);

    }

    private void addMovesInAllDirections(Integer[][][] pieceMatrix,
                                         int[] rowCol,
                                         List<Pair<Integer, Integer>> moves,
                                         int maxDistance,
                                         boolean isWhite) {

        addMovesInDirection(pieceMatrix, rowCol, moves, maxDistance, new int[] {1, 0}, isWhite);
        addMovesInDirection(pieceMatrix, rowCol, moves, maxDistance, new int[] {-1, 0}, isWhite);
        addMovesInDirection(pieceMatrix, rowCol, moves, maxDistance, new int[] {0, 1}, isWhite);
        addMovesInDirection(pieceMatrix, rowCol, moves, maxDistance, new int[] {0, -1}, isWhite);

    }

    private void addDiagonalMoves(Integer[][][] pieceMatrix,
                                  int[] rowCol,
                                  List<Pair<Integer, Integer>> moves,
                                  int maxDistance,
                                  boolean isWhite) {

        addMovesInDirection(pieceMatrix, rowCol, moves, maxDistance, new int[] {1, 1}, isWhite);
        addMovesInDirection(pieceMatrix, rowCol, moves, maxDistance, new int[] {1, -1}, isWhite);
        addMovesInDirection(pieceMatrix, rowCol, moves, maxDistance, new int[] {-1, 1}, isWhite);
        addMovesInDirection(pieceMatrix, rowCol, moves, maxDistance, new int[] {-1, -1}, isWhite);

    }

    private void addPawnMoves(Integer[][][] pieceMatrix,
                              int[] rowCol,
                              List<Pair<Integer, Integer>> moves,
                              boolean isWhite) {

        int multiplier = isWhite ? -1 : 1;

        if ((rowCol[0] == 1 && !isWhite) || (rowCol[0] == 6 && isWhite)) {
            addMovesInDirection(pieceMatrix, rowCol, moves, 1, new int[] {multiplier, 0}, isWhite);
            addMovesInDirection(pieceMatrix, rowCol, moves, 1, new int[] {2 * multiplier, 0}, isWhite);
        }
        else {
            addMovesInDirection(pieceMatrix, rowCol, moves, 1, new int[] {multiplier, 0}, isWhite);
        }

        addPawnCapture(pieceMatrix, rowCol, moves, true, isWhite);
        addPawnCapture(pieceMatrix, rowCol, moves, false, isWhite);

    }

    private void addHorseMoves(Integer[][][] pieceMatrix,
                               int[] rowCol,
                               List<Pair<Integer, Integer>> moves,
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
                                     List<Pair<Integer, Integer>> moves,
                                     int length,
                                     int[] riCi,
                                     boolean isWhite) {

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
                if (pieceInMatrix[1] == 1 && !isWhite || pieceInMatrix[1] == 0 && isWhite) {
                    moves.add(Pair.of(newRow, newCol));
                }
                break;
            }
            moves.add(Pair.of(newRow, newCol));
        }

    }

    private void addPawnCapture(Integer[][][] pieceMatrix,
                                int[] rowCol,
                                List<Pair<Integer, Integer>> moves,
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
            if (hasPiece && (leftPiece[1] == 1 && !isWhite || leftPiece[1] == 0 && isWhite)) {
                moves.add(Pair.of(x, y));
            }
        }
    }

    List<Game> getAllGames();

    Game getGame(Integer gameId);

    List<Game> getGames();

    Game createGame(UUID whiteUUID);

    boolean checkGame(Integer gameId);

    void refreshGameTimer(Integer gameId);

    Game enterGame(UUID blackUUID, Integer gameId);

    List<Pair<MoveType, Pair<Integer, Integer>>> getPossibleMoves(Game game, Piece piece, UUID playerUUID);

    GameStatus getGameStatus(Integer gameId);

    Game movePiece(Game game, UUID playerUUID, Piece piece, Pair<MoveType, Pair<Integer, Integer>> moveTypePairPair);

}
