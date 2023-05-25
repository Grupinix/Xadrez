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
import org.springframework.data.util.Pair;
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

    default Optional<PieceDto> getPieceOptional(List<PieceDto> pieceDtoList, PieceType pieceType) {
        return pieceDtoList.stream()
                           .filter(pieceDto -> pieceDto.getPieceType() == pieceType)
                           .findFirst();
    }

    default GameStatus getGameStatus(GameDto gameDto, boolean isWhiteTurn) {
        gameDto = gameDto.copy();
        BoardDto boardDto = gameDto.getBoard();

        Optional<PieceDto> whiteKingOptional = getPieceOptional(boardDto.getWhitePieces(), PieceType.KING);
        Optional<PieceDto> blackKingOptional = getPieceOptional(boardDto.getBlackPieces(), PieceType.KING);

        if (whiteKingOptional.isEmpty()) {
            return GameStatus.BLACK_WINS;
        }
        else if (blackKingOptional.isEmpty()) {
            return GameStatus.WHITE_WINS;
        }

        GameStatus isInCheck = isCheck(gameDto, whiteKingOptional.get(), blackKingOptional.get());
        if (isInCheck != GameStatus.NORMAL) {
            if (isMatte(gameDto, whiteKingOptional.get(), blackKingOptional.get(), isWhiteTurn)) {
                return isInCheck == GameStatus.WHITE_CHECK ? GameStatus.WHITE_WINS : GameStatus.BLACK_WINS;
            }
            return isInCheck;
        }

        return GameStatus.NORMAL;
    }

    default boolean isMatte(GameDto gameDto,
                            PieceDto whiteKing,
                            PieceDto blackKing,
                            boolean isWhiteTurn) {
        GameDto tempGame = gameDto.copy();

        UUID playerUUID = isWhiteTurn ? gameDto.getWhitePlayerUUID() : gameDto.getBlackPlayerUUID();
        PieceDto king = isWhiteTurn ? whiteKing : blackKing;

        for (MoveDto possibleMove : getPossibleMoves(gameDto, king, playerUUID)) {
            movePieceOnBoardDto(tempGame.getBoard(), king, possibleMove);

            GameStatus isCheckValue = isCheck(tempGame, whiteKing, blackKing);
            if (isCheckValue == GameStatus.NORMAL) {
                return false;
            }
        }

        return true;
    }

    default GameStatus isCheck(GameDto gameDto, PieceDto whiteKing, PieceDto blackKing) {
        BoardDto boardDto = gameDto.getBoard();

        for (boolean isWhite : new boolean[] {false, true}) {
            List<PieceDto> pieceDtos = isWhite ? boardDto.getWhitePieces() : boardDto.getBlackPieces();
            PieceDto king = isWhite ? blackKing : whiteKing;
            for (PieceDto pieceDto : pieceDtos) {
                UUID playerUUID = isWhite ? gameDto.getWhitePlayerUUID() : gameDto.getBlackPlayerUUID();
                List<MoveDto> possibleMoves = getPossibleMoves(gameDto, pieceDto, playerUUID);
                GameStatus actualMovesStatus = verifyKingInCheck(possibleMoves, king, isWhite);
                if (actualMovesStatus != GameStatus.NORMAL) {
                    return actualMovesStatus;
                }
            }
        }

        return GameStatus.NORMAL;
    }

    private GameStatus verifyKingInCheck(List<MoveDto> possibleMoves, PieceDto king, boolean isWhite) {
        for (MoveDto possibleMove : possibleMoves) {
            if (possibleMove.getFirst() == MoveType.CAPTURE) {
                int x = possibleMove.getSecond().getFirst();
                int y = possibleMove.getSecond().getSecond();
                if (x == king.getPositionX() && y == king.getPositionY()) {
                    return isWhite ? GameStatus.WHITE_CHECK : GameStatus.BLACK_CHECK;
                }
            }
        }

        return GameStatus.NORMAL;
    }

    default void movePieceOnBoardDto(BoardDto boardDto, PieceDto pieceDto, MoveDto moveDto) {
        int x = moveDto.getSecond().getFirst();
        int y = moveDto.getSecond().getSecond();

        Integer[][][] pieceMatrix = boardDto.getPieceMatrix();
        Integer[] possibleKilled = pieceMatrix[x][y];

        List<PieceDto> whiteList = new ArrayList<>(boardDto.getWhitePieces());
        List<PieceDto> blackList = new ArrayList<>(boardDto.getBlackPieces());
        List<PieceDto> enemyList = pieceDto.getWhitePiece() ? blackList : whiteList;

        if (!pieceDto.getWhitePiece() && possibleKilled != null && possibleKilled[0] != null) {
            Pair<Integer, PieceDto> found = null;
            for (int i = 0; i < enemyList.size(); i++) {
                if (enemyList.get(i).getId().equals(possibleKilled[0])) {
                    found = Pair.of(i, enemyList.get(i));
                    break;
                }
            }
            if (found != null) {
                int index = found.getFirst();
                enemyList.remove(index);
                if (pieceDto.getWhitePiece()) {
                    boardDto.setBlackPieces(enemyList);
                }
                else {
                    boardDto.setWhitePieces(enemyList);
                }
            }
        }

        pieceDto.setPositionX(x);
        pieceDto.setPositionY(y);
    }

    Game movePiece(Game game, UUID playerUUID, Piece piece, MoveDto moveTypePairPair) throws ResponseStatusException;

    default Map<PieceDto, List<MoveDto>> getPlayerLegalMoves(GameDto gameDto, int playerColor) {
        BoardDto boardDto = gameDto.getBoard();
        Map<PieceDto, List<MoveDto>> moveDtoMap = new HashMap<>();

        Optional<PieceDto> whiteKingOptional = getPieceOptional(boardDto.getWhitePieces(), PieceType.KING);
        Optional<PieceDto> blackKingOptional = getPieceOptional(boardDto.getBlackPieces(), PieceType.KING);

        if (whiteKingOptional.isEmpty() || blackKingOptional.isEmpty()) {
            return moveDtoMap;
        }

        boolean isWhiteTurn = playerColor == Constants.WHITE_COLOR;
        GameStatus gameStatus = getGameStatus(gameDto, isWhiteTurn);
        List<PieceDto> pieceDtoList = playerColor == 0 ? boardDto.getWhitePieces() : boardDto.getBlackPieces();
        UUID playerUUID = playerColor == 0 ? gameDto.getWhitePlayerUUID() : null;

        for (PieceDto piece : pieceDtoList) {
            moveDtoMap.put(piece, getPossibleMoves(gameDto, piece, playerUUID));
        }

        if (gameStatus == GameStatus.NORMAL) {
            return moveDtoMap;
        }

        PieceDto whiteKing = whiteKingOptional.get();
        PieceDto blackKing = blackKingOptional.get();

        List<MoveDto> validMoves = new ArrayList<>();
        PieceDto pieceDto = isWhiteTurn ? whiteKing : blackKing;
        List<MoveDto> moves = moveDtoMap.get(pieceDto);
        for (MoveDto move : moves) {
            GameDto tempGame = gameDto.copy();
            movePieceOnBoardDto(tempGame.getBoard(), pieceDto, move);
            if (isCheck(tempGame, pieceDto, blackKing) == GameStatus.NORMAL) {
                validMoves.add(move);
            }
        }

        return Map.of(pieceDto, validMoves);
    }

}
