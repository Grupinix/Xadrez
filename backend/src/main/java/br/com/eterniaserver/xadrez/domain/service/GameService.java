package br.com.eterniaserver.xadrez.domain.service;

import br.com.eterniaserver.xadrez.Constants;
import br.com.eterniaserver.xadrez.domain.entities.Board;
import br.com.eterniaserver.xadrez.domain.entities.Game;
import br.com.eterniaserver.xadrez.domain.entities.History;
import br.com.eterniaserver.xadrez.domain.entities.Piece;
import br.com.eterniaserver.xadrez.domain.enums.GameStatus;
import br.com.eterniaserver.xadrez.domain.enums.MoveType;

import br.com.eterniaserver.xadrez.domain.enums.PieceType;
import br.com.eterniaserver.xadrez.domain.service.impl.PlayerServiceImpl;
import br.com.eterniaserver.xadrez.rest.dtos.BoardDto;
import br.com.eterniaserver.xadrez.rest.dtos.GameDto;
import br.com.eterniaserver.xadrez.rest.dtos.HistoryDto;
import br.com.eterniaserver.xadrez.rest.dtos.MoveDto;
import br.com.eterniaserver.xadrez.rest.dtos.PieceDto;
import br.com.eterniaserver.xadrez.rest.dtos.PositionDto;
import org.springframework.data.util.Pair;
import org.springframework.http.HttpStatus;
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

        int[] rowCol = new int[]{piece.getPositionX(), piece.getPositionY()};

        switch (piece.getPieceType()) {
            case KING -> addKingMoves(pieceMatrix, rowCol, moves, isWhite);
            case QUEEN -> addQueenMoves(pieceMatrix, rowCol, moves, isWhite);
            case TOWER -> addMovesInAllDirections(pieceMatrix, rowCol, moves, 7, isWhite);
            case BISHOP -> addDiagonalMoves(pieceMatrix, rowCol, moves, 7, isWhite);
            case HORSE -> addHorseMoves(pieceMatrix, rowCol, moves, isWhite);
            case PAWN -> addPawnMoves(pieceMatrix, rowCol, moves, isWhite);
        }

        int enemyColor = isWhite ? Constants.BLACK_COLOR : Constants.WHITE_COLOR;

        if (piece.getPieceType() == PieceType.PAWN && isWhite == game.getWhiteTurn()) {
            int pieceX = piece.getPositionX();
            int pieceY = piece.getPositionY();

            if (isWhite && pieceX == 3 || !isWhite && pieceY == 4) {
                addEnPassant(pieceMatrix, game, possibleMoves, -1, pieceX, pieceY, enemyColor);
                addEnPassant(pieceMatrix, game, possibleMoves, 1, pieceX, pieceY, enemyColor);
            }
        }

        for (PositionDto move : moves) {
            Integer[] pieceInMatrix = pieceMatrix[move.getFirst()][move.getSecond()];
            if (!hasPieceInteger(pieceInMatrix)) {
                boolean canRoq = canRoque(game.getBoard(), piece);
                int moveY = move.getSecond();

                if ((moveY == 6 || moveY == 2) && piece.getPieceType() == PieceType.KING && canRoq) {
                    possibleMoves.add(new MoveDto(MoveType.ROQUE, move));
                } else {
                    possibleMoves.add(new MoveDto(MoveType.NORMAL, move));
                }
            } else if (isEnemy(pieceInMatrix, enemyColor)) {
                possibleMoves.add(new MoveDto(MoveType.CAPTURE, move));
            }
        }

        return possibleMoves;

    }

    private void addEnPassant(Integer[][][] pieceMatrix,
                              GameDto gameDto,
                              List<MoveDto> possibleMoves,
                              int side,
                              int pieceX,
                              int pieceY,
                              int enemyColor) {
        List<HistoryDto> histories = gameDto.getBoard().getHistories();

        if (pieceY + side > 0 && pieceY + side < 8) {
            final Integer[] pawn = pieceMatrix[pieceX][pieceY + side];
            if (hasPieceInteger(pawn) && isEnemy(pawn, enemyColor) && isPieceType(pawn, PieceType.PAWN)) {
                long enemyMoves = histories.stream().filter(h -> h.getPieceId().equals(pawn[0])).count();
                if (enemyMoves == 1) {
                    int multiplier = enemyColor == Constants.WHITE_COLOR ? 1 : -1;
                    possibleMoves.add(
                            new MoveDto(MoveType.EN_PASSANT, new PositionDto(pieceX + multiplier, pieceY + side))
                    );
                }
            }
        }
    }

    private boolean isPieceType(Integer[] pieceMatrix, PieceType pieceType) {
        return pieceMatrix[2] == pieceType.ordinal();
    }

    private boolean isEnemy(Integer[] pieceMatrix, int enemyColor) {
        return pieceMatrix[1] == enemyColor;
    }

    private void addKingMoves(Integer[][][] pieceMatrix,
                              int[] rowCol,
                              List<PositionDto> moves,
                              boolean isWhite) {

        addMovesInAllDirections(pieceMatrix, rowCol, moves, 1, isWhite);
        addDiagonalMoves(pieceMatrix, rowCol, moves, 1, isWhite);
        addRoqueMove(pieceMatrix, moves, isWhite);
    }

    private void addRoqueMove(Integer[][][] pieceMatrix, List<PositionDto> moves, boolean isWhite) {
        int x = isWhite ? 7 : 0;
        int color = isWhite ? Constants.WHITE_COLOR : Constants.BLACK_COLOR;

        Integer[] kingPiece = pieceMatrix[x][4];
        if (!hasPieceInteger(kingPiece) || kingPiece[1] != color || kingPiece[2] != PieceType.KING.ordinal()) {
            return;
        }

        addRoqueMove(pieceMatrix, moves, isWhite, false);
        addRoqueMove(pieceMatrix, moves, isWhite, true);
    }

    private void addRoqueMove(Integer[][][] pieceMatrix, List<PositionDto> moves, boolean isWhite, boolean right) {
        int i = right ? 5 : 1;
        int n = right ? 7 : 4;
        int x = isWhite ? 7 : 0;
        int towerY = right ? 7 : 0;
        int kingNewY = right ? 6 : 2;
        int color = isWhite ? Constants.WHITE_COLOR : Constants.BLACK_COLOR;

        boolean invalidRoque = true;
        for (; i < n; i++) {
            if (hasPieceInteger(pieceMatrix[x][i])) {
                invalidRoque = false;
                break;
            }
        }

        Integer[] tower = pieceMatrix[x][towerY];
        if (invalidRoque && hasPieceInteger(tower) && tower[1] == color && isPieceType(tower, PieceType.TOWER)) {
            moves.add(new PositionDto(x, kingNewY));
        }
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

        addMovesInDirection(pieceMatrix, rowCol, moves, maxDistance, new int[]{1, 0}, isWhite);
        addMovesInDirection(pieceMatrix, rowCol, moves, maxDistance, new int[]{-1, 0}, isWhite);
        addMovesInDirection(pieceMatrix, rowCol, moves, maxDistance, new int[]{0, 1}, isWhite);
        addMovesInDirection(pieceMatrix, rowCol, moves, maxDistance, new int[]{0, -1}, isWhite);

    }

    private void addDiagonalMoves(Integer[][][] pieceMatrix,
                                  int[] rowCol,
                                  List<PositionDto> moves,
                                  int maxDistance,
                                  boolean isWhite) {

        addMovesInDirection(pieceMatrix, rowCol, moves, maxDistance, new int[]{1, 1}, isWhite);
        addMovesInDirection(pieceMatrix, rowCol, moves, maxDistance, new int[]{1, -1}, isWhite);
        addMovesInDirection(pieceMatrix, rowCol, moves, maxDistance, new int[]{-1, 1}, isWhite);
        addMovesInDirection(pieceMatrix, rowCol, moves, maxDistance, new int[]{-1, -1}, isWhite);

    }

    private void addPawnMoves(Integer[][][] pieceMatrix,
                              int[] rowCol,
                              List<PositionDto> moves,
                              boolean isWhite) {

        int multiplier = isWhite ? -1 : 1;

        if ((rowCol[0] == 1 && !isWhite) || (rowCol[0] == 6 && isWhite)) {
            addMovesInDirection(pieceMatrix, rowCol, moves, 1, new int[]{multiplier, 0}, isWhite, true);
            if (!hasPieceInteger(pieceMatrix[rowCol[0] + multiplier][rowCol[1]])) {
                addMovesInDirection(pieceMatrix, rowCol, moves, 1, new int[]{2 * multiplier, 0}, isWhite, true);
            }
        } else {
            addMovesInDirection(pieceMatrix, rowCol, moves, 1, new int[]{multiplier, 0}, isWhite, true);
        }

        addPawnCapture(pieceMatrix, rowCol, moves, true, isWhite);
        addPawnCapture(pieceMatrix, rowCol, moves, false, isWhite);

    }

    private void addHorseMoves(Integer[][][] pieceMatrix,
                               int[] rowCol,
                               List<PositionDto> moves,
                               boolean isWhite) {

        addMovesInDirection(pieceMatrix, rowCol, moves, 1, new int[]{1, 2}, isWhite);
        addMovesInDirection(pieceMatrix, rowCol, moves, 1, new int[]{2, 1}, isWhite);
        addMovesInDirection(pieceMatrix, rowCol, moves, 1, new int[]{-1, 2}, isWhite);
        addMovesInDirection(pieceMatrix, rowCol, moves, 1, new int[]{-2, 1}, isWhite);
        addMovesInDirection(pieceMatrix, rowCol, moves, 1, new int[]{1, -2}, isWhite);
        addMovesInDirection(pieceMatrix, rowCol, moves, 1, new int[]{2, -1}, isWhite);
        addMovesInDirection(pieceMatrix, rowCol, moves, 1, new int[]{-1, -2}, isWhite);
        addMovesInDirection(pieceMatrix, rowCol, moves, 1, new int[]{-2, -1}, isWhite);

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
        int enemyColor = isWhite ? Constants.BLACK_COLOR : Constants.WHITE_COLOR;

        for (int i = 1; i <= length; i++) {
            int newRow = row + ri * i;
            int newCol = col + ci * i;
            if (newRow < 0 || newRow > 7 || newCol < 0 || newCol > 7) {
                break;
            }

            Integer[] piece = pieceMatrix[newRow][newCol];
            if (hasPieceInteger(piece)) {
                if (piece[1] == enemyColor && !isPawn) {
                    moves.add(new PositionDto(newRow, newCol));
                }
                break;
            }
            moves.add(new PositionDto(newRow, newCol));
        }
    }

    default boolean hasPieceInteger(Integer[] pieceInt) {
        return pieceInt != null && pieceInt[0] != null && pieceInt[0] != 0;
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
            boolean hasPiece = hasPieceInteger(leftPiece);
            if (hasPiece && (leftPiece[1] == Constants.WHITE_COLOR && !isWhite || leftPiece[1] == Constants.BLACK_COLOR && isWhite)) {
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

    Game movePiece(Game game, UUID playerUUID, Piece piece, MoveDto moveTypePairPair) throws ResponseStatusException;

    void deleteEntity(Piece piece);

    void saveEntities(Game game, History history, Piece piece, Board board);

    default Optional<PieceDto> getPieceOptional(List<PieceDto> pieceDtoList, PieceType pieceType) {
        return pieceDtoList.stream()
                .filter(pieceDto -> pieceDto.getPieceType() == pieceType)
                .findFirst();
    }

    default GameStatus getGameStatus(GameDto gameDto) {
        if (gameDto.getStatusCached()) {
            return gameDto.getGameStatus();
        }

        GameDto tempGameDto = gameDto.copy();
        BoardDto tempBoardDto = tempGameDto.getBoard();

        Optional<PieceDto> whiteKingOptional = getPieceOptional(tempBoardDto.getWhitePieces(), PieceType.KING);
        Optional<PieceDto> blackKingOptional = getPieceOptional(tempBoardDto.getBlackPieces(), PieceType.KING);

        if (whiteKingOptional.isEmpty()) {
            return GameStatus.BLACK_WINS;
        } else if (blackKingOptional.isEmpty()) {
            return GameStatus.WHITE_WINS;
        }

        GameStatus isInCheck = isCheck(tempGameDto, whiteKingOptional.get(), blackKingOptional.get());
        if (isInCheck != GameStatus.NORMAL) {
            if (isMatte(tempGameDto, whiteKingOptional.get(), blackKingOptional.get(), isInCheck)) {
                return isInCheck == GameStatus.WHITE_CHECK ? GameStatus.WHITE_WINS : GameStatus.BLACK_WINS;
            }
            return isInCheck;
        }

        return GameStatus.NORMAL;
    }

    default boolean isMatte(GameDto gameDto,
                            PieceDto whiteKing,
                            PieceDto blackKing,
                            GameStatus isInCheck) {
        boolean isWhiteObviousMatte = isInCheck == GameStatus.WHITE_CHECK && gameDto.getWhiteTurn();
        boolean isBlackObviousMatte = isInCheck == GameStatus.BLACK_CHECK && !gameDto.getWhiteTurn();
        if (isWhiteObviousMatte || isBlackObviousMatte) {
            return true;
        }

        List<PieceDto> whitePieces = gameDto.getBoard().getWhitePieces();
        List<PieceDto> blackPieces = gameDto.getBoard().getBlackPieces();
        List<PieceDto> playerPieces = gameDto.getWhiteTurn() ? whitePieces : blackPieces;

        for (PieceDto pieceDto : playerPieces) {
            List<MoveDto> possibleMoves = getPiecePossibleMoves(gameDto, pieceDto, gameDto.getWhiteTurn());
            for (MoveDto possibleMove : possibleMoves) {
                GameDto tempGame = gameDto.copy();
                BoardDto tempBoard = tempGame.getBoard();

                List<PieceDto> tempPlayerPieces = tempGame.getWhiteTurn()
                        ? tempBoard.getWhitePieces()
                        : tempBoard.getBlackPieces();
                PieceDto tempPiece = tempPlayerPieces.stream()
                        .filter(p -> p.getId().equals(pieceDto.getId()))
                        .findFirst()
                        .orElse(null);
                PieceDto tempWhiteKing = tempBoard.getWhitePieces()
                        .stream()
                        .filter(p -> p.getId().equals(whiteKing.getId()))
                        .findFirst()
                        .orElse(null);
                PieceDto tempBlackKing = tempBoard.getBlackPieces()
                        .stream()
                        .filter(p -> p.getId().equals(blackKing.getId()))
                        .findFirst()
                        .orElse(null);
                if (tempPiece != null) {
                    movePieceOnBoardDto(tempGame, tempPiece, possibleMove);

                    GameStatus isCheckValue = isCheck(tempGame, tempWhiteKing, tempBlackKing);
                    if (isCheckValue == GameStatus.NORMAL) {
                        return false;
                    }
                }
            }
        }


        return true;
    }

    default GameStatus isCheck(GameDto gameDto, PieceDto whiteKing, PieceDto blackKing) {
        BoardDto boardDto = gameDto.getBoard();

        boolean[] turnPriority = gameDto.getWhiteTurn() ? new boolean[]{true, false} : new boolean[]{false, true};

        for (boolean isWhite : turnPriority) {
            List<PieceDto> pieceDtos = isWhite ? boardDto.getWhitePieces() : boardDto.getBlackPieces();
            PieceDto king = isWhite ? blackKing : whiteKing;
            for (PieceDto pieceDto : pieceDtos) {
                List<MoveDto> possibleMoves = getPiecePossibleMoves(gameDto, pieceDto, isWhite);
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

    default void movePieceOnBoardDto(GameDto gameDto, PieceDto pieceDto, MoveDto moveDto) {
        BoardDto boardDto = gameDto.getBoard();
        int x = moveDto.getSecond().getFirst();
        int y = moveDto.getSecond().getSecond();

        Integer[][][] pieceMatrix = boardDto.getPieceMatrix();
        Integer[] possibleKilled = pieceMatrix[x][y];

        List<PieceDto> whiteList = new ArrayList<>(boardDto.getWhitePieces());
        List<PieceDto> blackList = new ArrayList<>(boardDto.getBlackPieces());
        List<PieceDto> enemyList = pieceDto.getWhitePiece() ? blackList : whiteList;

        if (moveDto.getFirst() == MoveType.EN_PASSANT) {
            int multiplier = pieceDto.getWhitePiece() ? 1 : -1;
            int pawnX = x + multiplier;
            Pair<Integer, PieceDto> found = null;
            for (int i = 0; i < enemyList.size(); i++) {
                if (enemyList.get(i).getPositionX() == pawnX && enemyList.get(i).getPositionY() == y) {
                    found = Pair.of(i, enemyList.get(i));
                    break;
                }
            }
            if (found != null) {
                int index = found.getFirst();
                enemyList.remove(index);
            }
        }
        else if (hasPieceInteger(possibleKilled)) {
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
            }
        }

        boardDto.setWhitePieces(whiteList);
        boardDto.setBlackPieces(blackList);

        pieceDto.setPositionX(x);
        pieceDto.setPositionY(y);

        if (moveDto.getFirst() == MoveType.ROQUE) {
            int towerY = y == 2 ? 0 : 7;
            List<PieceDto> towerList = pieceDto.getWhitePiece() ? boardDto.getWhitePieces() : boardDto.getBlackPieces();
            for (PieceDto tower : towerList) {
                if (tower.getPositionX() == x && tower.getPositionY() == towerY) {
                    tower.setPositionY(y == 2 ? 3 : 5);
                    break;
                }
            }
        }

        gameDto.setWhiteTurn(!gameDto.getWhiteTurn());
        Integer[][][] newPieceMatrix = new Integer[8][8][1];
        for (PieceDto piece : gameDto.getBoard().getWhitePieces()) {
            newPieceMatrix[piece.getPositionX()][piece.getPositionY()] = new Integer[]{
                    piece.getId(), Constants.WHITE_COLOR, piece.getPieceType().ordinal()
            };
        }
        for (PieceDto piece : gameDto.getBoard().getBlackPieces()) {
            newPieceMatrix[piece.getPositionX()][piece.getPositionY()] = new Integer[]{
                    piece.getId(), Constants.BLACK_COLOR, piece.getPieceType().ordinal()
            };
        }
        gameDto.getBoard().setPieceMatrix(newPieceMatrix);
    }

    default Map<PieceDto, List<MoveDto>> getPlayerLegalMoves(GameDto gameDto, int playerColor) {
        BoardDto boardDto = gameDto.getBoard();
        Map<PieceDto, List<MoveDto>> moveDtoMap = new HashMap<>();

        Optional<PieceDto> whiteKingOptional = getPieceOptional(boardDto.getWhitePieces(), PieceType.KING);
        Optional<PieceDto> blackKingOptional = getPieceOptional(boardDto.getBlackPieces(), PieceType.KING);

        boolean isWhiteTurn = gameDto.getWhiteTurn();
        boolean playerTurn = playerColor == Constants.WHITE_COLOR && isWhiteTurn
                || playerColor == Constants.BLACK_COLOR && !isWhiteTurn;

        if (whiteKingOptional.isEmpty() || blackKingOptional.isEmpty() || !playerTurn) {
            return moveDtoMap;
        }

        gameDto.setStatusCached(false);
        GameStatus gameStatus = getGameStatus(gameDto);
        List<PieceDto> pieceDtoList = playerColor == Constants.WHITE_COLOR
                ? boardDto.getWhitePieces()
                : boardDto.getBlackPieces();
        List<GameStatus> validStatus = playerColor == Constants.WHITE_COLOR
                ? List.of(GameStatus.NORMAL, GameStatus.WHITE_CHECK, GameStatus.WHITE_WINS)
                : List.of(GameStatus.NORMAL, GameStatus.BLACK_CHECK, GameStatus.BLACK_WINS);

        for (PieceDto piece : pieceDtoList) {
            moveDtoMap.put(piece, getPiecePossibleMoves(gameDto, piece, isWhiteTurn));
        }

        if (validStatus.contains(gameStatus)) {
            return moveDtoMap;
        }

        Map<PieceDto, List<MoveDto>> validMovesMap = new HashMap<>();

        for (Map.Entry<PieceDto, List<MoveDto>> entry : moveDtoMap.entrySet()) {
            PieceDto pieceDto = entry.getKey();
            List<MoveDto> moves = entry.getValue();
            List<MoveDto> validMoves = getValidMoves(moves, validStatus, gameDto, pieceDto, isWhiteTurn);
            if (!validMoves.isEmpty()) {
                validMovesMap.put(pieceDto, validMoves);
            }
        }

        return validMovesMap;
    }

    default List<MoveDto> getValidMoves(List<MoveDto> moves,
                                        List<GameStatus> validStatus,
                                        GameDto gameDto,
                                        PieceDto pieceDto,
                                        boolean isWhiteTurn) {
        List<MoveDto> validMoves = new ArrayList<>();

        for (MoveDto move : moves) {
            GameDto tempGame = gameDto.copy();
            BoardDto tempBoard = tempGame.getBoard();
            List<PieceDto> pieces = isWhiteTurn ? tempBoard.getWhitePieces() : tempBoard.getBlackPieces();

            PieceDto tempPieceDto = pieces.stream()
                    .filter(p -> p.getId().equals(pieceDto.getId()))
                    .findFirst()
                    .orElse(null);
            PieceDto whiteKing = tempBoard.getWhitePieces()
                    .stream()
                    .filter(p -> p.getPieceType() == PieceType.KING)
                    .findFirst()
                    .orElse(null);
            PieceDto blackKing = tempBoard.getBlackPieces()
                    .stream()
                    .filter(p -> p.getPieceType() == PieceType.KING)
                    .findFirst()
                    .orElse(null);

            if (tempPieceDto != null && whiteKing != null && blackKing != null) {
                movePieceOnBoardDto(tempGame, tempPieceDto, move);

                tempGame.setStatusCached(false);
                GameStatus checkStatus = getGameStatus(tempGame);
                if (validStatus.contains(checkStatus)) {
                    validMoves.add(move);
                }
            }
        }

        return validMoves;
    }

    default boolean canRoque(BoardDto boardDto, PieceDto pieceDto) {
        if (pieceDto.getPieceType() != PieceType.KING) {
            return false;
        }

        if (!pieceDto.getWhitePiece() && pieceDto.getPositionX() != 0) {
            return false;
        } else if (pieceDto.getWhitePiece() && pieceDto.getPositionX() != 7) {
            return false;
        }

        for (HistoryDto history : boardDto.getHistories()) {
            if (history.getPieceId().equals(pieceDto.getId())) {
                return false;
            }
        }

        return true;
    }

    default void movePieceOnGame(Game game,
                                 Piece piece,
                                 MoveDto moveTypePairPair,
                                 boolean isWhite) throws ResponseStatusException {
        Board board = game.getBoard();
        MoveType moveType = moveTypePairPair.getFirst();
        PositionDto position = moveTypePairPair.getSecond();
        History history = createHistory(board, piece, position, isWhite);
        History towerHistory = null;

        Piece tower = null;
        List<Piece> pieces = isWhite ? board.getWhitePieces() : board.getBlackPieces();
        List<Piece> enemyPieces = isWhite ? board.getBlackPieces() : board.getWhitePieces();

        if (moveType == MoveType.CAPTURE) {
            handleCapture(board, position, history, isWhite);
        }
        else if (moveType == MoveType.EN_PASSANT) {
            int multiplier = isWhite ? 1 : -1;
            int pawnX = position.getFirst() + multiplier;
            int pawnY = position.getSecond();

            for (Piece maybePawn : enemyPieces) {
                if (maybePawn.getPieceType() == PieceType.PAWN
                        && maybePawn.getPositionX() == pawnX
                        && maybePawn.getPositionY() == pawnY) {
                    removeCapturedPiece(board, maybePawn.getId(), history, isWhite);
                    break;
                }
            }
        }
        else if (moveType == MoveType.ROQUE) {
            if (position.getSecond() == 2) {
                for (Piece maybeTower : pieces) {
                    if (maybeTower.getPieceType() == PieceType.TOWER && maybeTower.getPositionY() == 0) {
                        towerHistory = createHistory(board, maybeTower, new PositionDto(maybeTower.getPositionX(), 3), isWhite);
                        maybeTower.setPositionY(3);
                        tower = maybeTower;
                        break;
                    }
                }
            }
            else {
                for (Piece maybeTower : pieces) {
                    if (maybeTower.getPieceType() == PieceType.TOWER && maybeTower.getPositionY() == 7) {
                        towerHistory = createHistory(board, maybeTower, new PositionDto(maybeTower.getPositionX(), 5), isWhite);
                        maybeTower.setPositionY(5);
                        tower = maybeTower;
                        break;
                    }
                }
            }
        }

        if (piece.getPieceType() == PieceType.PAWN) {
            UUID uuid = isWhite ? game.getWhitePlayerUUID() : game.getBlackPlayerUUID();
            PieceType pieceType = PlayerServiceImpl.getPawnToPiece(uuid);
            if (isWhite && position.getFirst() == 0) {
                piece.setPieceType(pieceType);
            }
            else if (!isWhite && position.getFirst() == 7) {
                piece.setPieceType(pieceType);
            }
        }

        piece.setPositionX(position.getFirst());
        piece.setPositionY(position.getSecond());

        game.setWhiteTurn(!game.getWhiteTurn());
        game.setTimer(System.currentTimeMillis());
        game.setGameStatus(getGameStatus(game.getGameDto()));
        game.setStatusCached(true);

        board.getHistories().add(history);

        if (isWhite) {
            game.setWhiteMoves(game.getWhiteMoves() + 1);
        } else {
            game.setBlackMoves(game.getBlackMoves() + 1);
        }

        saveEntities(game, history, piece, board);
        if (tower != null) {
            saveEntities(game, towerHistory, tower, board);
        }
    }


    private History createHistory(Board board, Piece piece, PositionDto position, boolean isWhite) {
        History history = new History();
        history.setPieceId(piece.getId());
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

        deleteEntity(capturedPiece);
    }
}
