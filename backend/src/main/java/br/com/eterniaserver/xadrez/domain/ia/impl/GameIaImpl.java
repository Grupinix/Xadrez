package br.com.eterniaserver.xadrez.domain.ia.impl;

import br.com.eterniaserver.xadrez.domain.entities.Game;
import br.com.eterniaserver.xadrez.domain.entities.Piece;
import br.com.eterniaserver.xadrez.domain.enums.PieceType;
import br.com.eterniaserver.xadrez.domain.ia.GameIa;
import br.com.eterniaserver.xadrez.domain.repositories.GameRepository;
import br.com.eterniaserver.xadrez.domain.repositories.PieceRepository;
import br.com.eterniaserver.xadrez.domain.service.GameService;
import br.com.eterniaserver.xadrez.rest.dtos.BoardDto;
import br.com.eterniaserver.xadrez.rest.dtos.GameDto;
import br.com.eterniaserver.xadrez.rest.dtos.MoveDto;
import br.com.eterniaserver.xadrez.rest.dtos.PieceDto;
import lombok.AllArgsConstructor;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;

import java.util.*;

@Service("gameIa")
@AllArgsConstructor
public class GameIaImpl implements GameIa {

    private final GameRepository gameRepository;
    private final PieceRepository pieceRepository;

    @Override
    public void movePiece(Integer gameId, UUID uuid, GameService gameService) {
        Optional<Game> optionalGame = gameRepository.findById(gameId);
        if (optionalGame.isEmpty()) {
            return;
        }

        Game game = optionalGame.get();
        GameDto gameDto = game.getGameDto();

        Pair<PieceDto, MoveDto> move = miniMax(
                gameService, gameDto.getBoard(), gameDto, 0, 1, 2
        );
        PieceDto pieceDto = move.getFirst();
        MoveDto moveDto = move.getSecond();

        Piece piece = pieceRepository.findById(pieceDto.getId()).orElseThrow();

        gameService.movePiece(game, null, piece, moveDto);
    }

    public Map<PieceDto, List<MoveDto>> getPlayerLegalMoves(GameService gameService, GameDto game, int playerColor) {
        Map<PieceDto, List<MoveDto>> moveDtoMap = new HashMap<>();
        List<PieceDto> pieceDtoList = playerColor == 0 ? game.getBoard().getWhitePieces() : game.getBoard().getBlackPieces();
        UUID playerUUID = playerColor == 0 ? game.getWhitePlayerUUID() : null;

        for (PieceDto piece : pieceDtoList) {
            moveDtoMap.put(piece, gameService.getPossibleMoves(game, piece, playerUUID));
        }

        return moveDtoMap;
    }

    public Pair<PieceDto, MoveDto> miniMax(GameService gameService,
                                           BoardDto boardDto,
                                           GameDto gameDto,
                                           int currentColor,
                                           int opponentColor,
                                           int depth) {
        Map<PieceDto, List<MoveDto>> legalMovesMap = getPlayerLegalMoves(gameService, gameDto, opponentColor);
        int highestValue = Integer.MIN_VALUE;
        Pair<PieceDto, MoveDto> bestMove = null;
        Pair<PieceDto, MoveDto> lastMove = null;
        for (Map.Entry<PieceDto, List<MoveDto>> pieceDtoListEntry : legalMovesMap.entrySet()) {
            List<MoveDto> legalMoves = pieceDtoListEntry.getValue();
            for (MoveDto legalMove : legalMoves) {
                BoardDto b = boardDto.copy();
                movePieceOnBoard(b, pieceDtoListEntry.getKey(), legalMove);

                GameDto tempGame = gameDto.copy();
                tempGame.setBoard(b);
                int bestBoardValue = evalutionFunction(b, opponentColor != 1);
                int value = minimaxValue(gameService, b, gameDto, opponentColor, currentColor, depth - 1, bestBoardValue);

                if (currentColor == 0) {
                    lastMove = Pair.of(pieceDtoListEntry.getKey(), legalMove);
                    if (value > highestValue) {
                        highestValue = value;
                        bestMove = Pair.of(pieceDtoListEntry.getKey(), legalMove);
                    }
                }
            }
        }
        return bestMove != null ? bestMove : lastMove;
    }

    private int minimaxValue(GameService gameService,
                             BoardDto boardDto,
                             GameDto gameDto,
                             int currentColor,
                             int opponentColor,
                             int depth,
                             int bestBoardValue) {

        if (depth == 0) {
            return bestBoardValue;
        }

        int actualMove = bestBoardValue;
        int lastValue;
        if (currentColor == 0) {
            GameDto tempGame = gameDto.copy();
            tempGame.setBoard(boardDto);
            Map<PieceDto, List<MoveDto>> legalMovesMap = getPlayerLegalMoves(gameService, tempGame, opponentColor);
            int highestValue = Integer.MIN_VALUE;
            int oldValue= Integer.MIN_VALUE;
            int value;
            for (Map.Entry<PieceDto, List<MoveDto>> pieceDtoListEntry : legalMovesMap.entrySet()) {
                List<MoveDto> legalMoves = pieceDtoListEntry.getValue();
                for (MoveDto legalMove : legalMoves) {
                    BoardDto b = boardDto.copy();
                    movePieceOnBoard(b, pieceDtoListEntry.getKey(), legalMove);
                    value = evalutionFunction(b, opponentColor != 1);
                    tempGame.setBoard(b);
                    if (depth >= 2) {
                        int moveValue = actualMove + evalutionFunction(b, opponentColor != 1);
                        value = minimaxValue(gameService, b, gameDto, opponentColor, currentColor, depth - 1, moveValue);
                        if (value > oldValue) {
                            oldValue = value;
                            bestBoardValue = value;
                        }
                    }
                    else if (value > highestValue) {
                        if (highestValue != Integer.MIN_VALUE) {
                            bestBoardValue = bestBoardValue - oldValue;
                        }
                        oldValue = value;
                        highestValue = value;
                        bestBoardValue = bestBoardValue + value;
                    }
                }
            }
            lastValue = bestBoardValue;
        }
        else {
            GameDto tempGame = gameDto.copy();
            tempGame.setBoard(boardDto);
            Map<PieceDto, List<MoveDto>> legalMovesMap = getPlayerLegalMoves(gameService, tempGame, opponentColor);
            int highestValue = Integer.MIN_VALUE;
            int oldValue = Integer.MAX_VALUE;
            for (Map.Entry<PieceDto, List<MoveDto>> pieceDtoListEntry : legalMovesMap.entrySet()) {
                List<MoveDto> legalMoves = pieceDtoListEntry.getValue();
                for (MoveDto legalMove : legalMoves) {
                    BoardDto b = boardDto.copy();
                    movePieceOnBoard(b, pieceDtoListEntry.getKey(), legalMove);
                    tempGame.setBoard(b);
                    int value = evalutionFunction(b, opponentColor != 1);
                    if (depth >= 2) {
                        int moveValue = actualMove - evalutionFunction(b, opponentColor != 1);
                        value = minimaxValue(gameService, b, gameDto, opponentColor, currentColor, depth - 1, moveValue);
                        if (value < oldValue) {
                            oldValue = value;
                            bestBoardValue = value;
                        }
                    }
                    else if (value > highestValue) {
                        if (highestValue != Integer.MIN_VALUE) {
                            bestBoardValue = bestBoardValue + oldValue;
                        }
                        oldValue = value;
                        highestValue = value;
                        bestBoardValue = bestBoardValue - value;
                    }
                }
            }
            lastValue = bestBoardValue;
        }

        return lastValue;
    }

    public void movePieceOnBoard(BoardDto boardDto, PieceDto pieceDto, MoveDto moveDto) {
        int x = moveDto.getSecond().getFirst();
        int y = moveDto.getSecond().getSecond();

        Integer[][][] pieceMatrix = boardDto.getPieceMatrix();
        Integer[] possibleKilled = pieceMatrix[x][y];

        if (!pieceDto.getWhitePiece() && possibleKilled != null && possibleKilled[0] != null) {
            Pair<Integer, PieceDto> found = null;
            for (int i = 0; i < boardDto.getBlackPieces().size(); i++) {
                if (boardDto.getBlackPieces().get(i).getId().equals(possibleKilled[0])) {
                    found = Pair.of(i, boardDto.getBlackPieces().get(i));
                    break;
                }
            }
            if (found != null) {
                boardDto.getBlackPieces().remove(found.getFirst().intValue());
            }
        }
        else if (possibleKilled != null && possibleKilled[0] != null) {
            Pair<Integer, PieceDto> found = null;
            for (int i = 0; i < boardDto.getWhitePieces().size(); i++) {
                if (boardDto.getWhitePieces().get(i).getId().equals(possibleKilled[0])) {
                    found = Pair.of(i, boardDto.getWhitePieces().get(i));
                    break;
                }
            }
            if (found != null) {
                boardDto.getWhitePieces().remove(found.getFirst().intValue());
            }
        }
        pieceDto.setPositionX(x);
        pieceDto.setPositionY(y);
    }

    public int countPiecesByType(BoardDto boardDto, boolean whiteTurn, PieceType pieceType) {
        List<PieceDto> pieces = whiteTurn ? boardDto.getWhitePieces() : boardDto.getBlackPieces();
        int count = 0;
        for (PieceDto piece : pieces) {
            if (piece.getPieceType() == pieceType) {
                count++;
            }
        }
        return count;
    }


    public int evalutionFunction(BoardDto boardDto, boolean whiteTurn) {

        int boardValue = 0;
        int numhorse, numrook, numqueen, numking, numpawn, numbishop;

        boolean tempWhiteTurn = whiteTurn;

        whiteTurn = !whiteTurn;
        numpawn = countPiecesByType(boardDto, whiteTurn, PieceType.PAWN);
        boardValue = boardValue + 100 * (8 - numpawn);
        numhorse = countPiecesByType(boardDto, whiteTurn, PieceType.HORSE);
        boardValue = boardValue + 300 * (2 - numhorse);
        numbishop = countPiecesByType(boardDto, whiteTurn, PieceType.BISHOP);
        boardValue = boardValue + 325 * (2 - numbishop);
        numrook = countPiecesByType(boardDto, whiteTurn, PieceType.TOWER);
        boardValue = boardValue + 500 * (2 - numrook);
        numqueen = countPiecesByType(boardDto, whiteTurn, PieceType.QUEEN);
        boardValue = boardValue + 900 * (1 - numqueen);
        numking = countPiecesByType(boardDto, whiteTurn, PieceType.KING);
        boardValue = boardValue + 3000 * (1 - numking);
        whiteTurn = tempWhiteTurn;

        boardValue = boardValue + evalutionPerPositioning(boardDto, whiteTurn);

        return boardValue;
    }

    public int evalutionPerPositioning(BoardDto boardDto, boolean whiteTurn) {
        Integer[][][] boardMatrix = boardDto.getPieceMatrix();
        int whiteTurnInt = whiteTurn ? 0 : 1;

        int value = 0;
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                Integer[] piece = boardMatrix[i][j];
                if (piece == null || piece[0] == null) {
                    continue;
                }
                if (piece[1] != whiteTurnInt) {
                    List<PieceDto> pieceDtoList = whiteTurn ? boardDto.getWhitePieces() : boardDto.getBlackPieces();
                    for (PieceDto pieceDto : pieceDtoList) {
                        if ((i >= 3 && i <= 4) && (j >= 3 && j <= 4)) {
                            value = switch (pieceDto.getPieceType()) {
                                case BISHOP, HORSE -> value + 40;
                                case KING -> value - 30;
                                case PAWN -> value + 10;
                                case QUEEN -> value + 60;
                                case TOWER -> value + 50;
                            };
                        }
                        else if ((i >= 2 && i <= 5) && (j >= 2 && j <= 5)) {
                            value = switch (pieceDto.getPieceType()) {
                                case BISHOP, HORSE -> value + 30;
                                case KING -> value - 20;
                                case PAWN -> value + 5;
                                case QUEEN -> value + 50;
                                case TOWER -> value + 40;
                            };
                        }
                    }
                }
            }
        }
        return value;
    }

}
