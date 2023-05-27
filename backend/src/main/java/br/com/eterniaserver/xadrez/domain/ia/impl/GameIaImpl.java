package br.com.eterniaserver.xadrez.domain.ia.impl;

import br.com.eterniaserver.xadrez.Constants;
import br.com.eterniaserver.xadrez.domain.entities.Game;
import br.com.eterniaserver.xadrez.domain.entities.Piece;
import br.com.eterniaserver.xadrez.domain.enums.GameDifficulty;
import br.com.eterniaserver.xadrez.domain.enums.GameStatus;
import br.com.eterniaserver.xadrez.domain.enums.PieceType;
import br.com.eterniaserver.xadrez.domain.ia.GameIa;
import br.com.eterniaserver.xadrez.domain.repositories.GameRepository;
import br.com.eterniaserver.xadrez.domain.repositories.PieceRepository;
import br.com.eterniaserver.xadrez.domain.service.impl.ClassicPIAGameServiceImpl;
import br.com.eterniaserver.xadrez.rest.dtos.BoardDto;
import br.com.eterniaserver.xadrez.rest.dtos.GameDto;
import br.com.eterniaserver.xadrez.rest.dtos.MoveDto;
import br.com.eterniaserver.xadrez.rest.dtos.PieceDto;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;

import java.util.*;

@Service("gameIa")
@AllArgsConstructor
public class GameIaImpl implements GameIa {

    private final GameRepository gameRepository;
    private final PieceRepository pieceRepository;
    private final ClassicPIAGameServiceImpl classicPIAGameService;

    @Override
    @Transactional
    public void movePiece(Integer gameId) {
        Optional<Game> optionalGame = gameRepository.findById(gameId);
        if (optionalGame.isEmpty()) {
            return;
        }

        GameDifficulty gameDifficulty = optionalGame.get().getGameDto().getGameDifficulty();
        Game game = optionalGame.get();
        GameDto gameDto = game.getGameDto();
        int depth = gameDifficulty.ordinal() + 1;

        Pair<PieceDto, MoveDto> move = miniMax(
                gameDto, Constants.WHITE_COLOR, Constants.BLACK_COLOR, depth
        );
        PieceDto pieceDto = move.getFirst();
        MoveDto moveDto = move.getSecond();

        Piece piece = pieceRepository.findById(pieceDto.getId()).orElseThrow();

        classicPIAGameService.movePiece(game, null, piece, moveDto);
    }

    public Pair<PieceDto, MoveDto> miniMax(GameDto gameDto,
                                           int currentColor,
                                           int opponentColor,
                                           int depth) {
        Map<PieceDto, List<MoveDto>> legalMovesMap = classicPIAGameService.getPlayerLegalMoves(gameDto, opponentColor);
        int highestValue = Integer.MIN_VALUE;
        Pair<PieceDto, MoveDto> bestMove = null;
        Pair<PieceDto, MoveDto> lastMove = null;
        for (Map.Entry<PieceDto, List<MoveDto>> pieceDtoListEntry : legalMovesMap.entrySet()) {
            List<MoveDto> legalMoves = pieceDtoListEntry.getValue();
            for (MoveDto legalMove : legalMoves) {
                BoardDto b = gameDto.getBoard().copy();
                classicPIAGameService.movePieceOnBoardDto(b, pieceDtoListEntry.getKey(), legalMove);
                GameDto tempGame = gameDto.copy();
                tempGame.setBoard(b);
                int bestBoardValue = evaluationFunction(tempGame, opponentColor);
                int value = minimaxValue(tempGame, opponentColor, currentColor, depth - 1, bestBoardValue);

                lastMove = Pair.of(pieceDtoListEntry.getKey(), legalMove);
                if (value > highestValue) {
                    highestValue = value;
                    bestMove = Pair.of(pieceDtoListEntry.getKey(), legalMove);
                }
            }
        }
        return bestMove != null ? bestMove : lastMove;
    }

    private int minimaxValue(GameDto gameDto,
                             int currentColor,
                             int opponentColor,
                             int depth,
                             int bestBoardValue) {

        if (depth == 0) {
            return bestBoardValue;
        }

        int actualMove = bestBoardValue;
        int lastValue;
        GameDto tempGame = gameDto.copy();
        Map<PieceDto, List<MoveDto>> legalMovesMap = classicPIAGameService.getPlayerLegalMoves(tempGame, currentColor);
        int highestValue = Integer.MIN_VALUE;
        int oldValue = Integer.MAX_VALUE;
        for (Map.Entry<PieceDto, List<MoveDto>> pieceDtoListEntry : legalMovesMap.entrySet()) {
            List<MoveDto> legalMoves = pieceDtoListEntry.getValue();
            for (MoveDto legalMove : legalMoves) {
                BoardDto b = gameDto.getBoard().copy();
                classicPIAGameService.movePieceOnBoardDto(b, pieceDtoListEntry.getKey(), legalMove);
                tempGame.setBoard(b);
                int value = evaluationFunction(tempGame, opponentColor);
                if (depth >= 2) {
                    int moveValue;
                    if (currentColor == Constants.WHITE_COLOR) {
                        moveValue = actualMove + value;
                    }
                    else {
                        moveValue = actualMove - value;
                    }

                    value = minimaxValue(
                            tempGame,
                            opponentColor,
                            currentColor,
                            depth - 1,
                            moveValue
                    );
                    if ((currentColor == Constants.WHITE_COLOR && value > oldValue) || value < oldValue) {
                        oldValue = value;
                        bestBoardValue = value;
                    }
                }
                else if (currentColor == Constants.WHITE_COLOR && value > highestValue) {
                    if (highestValue != Integer.MIN_VALUE) {
                        bestBoardValue = bestBoardValue - oldValue;
                    }
                    oldValue = value;
                    highestValue = value;
                    bestBoardValue = bestBoardValue + value;
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

        return lastValue;
    }

    public int countPiecesByType(BoardDto boardDto, int color, PieceType pieceType) {
        List<PieceDto> pieces = color == Constants.WHITE_COLOR ? boardDto.getWhitePieces() : boardDto.getBlackPieces();
        int count = 0;
        for (PieceDto piece : pieces) {
            if (piece.getPieceType() == pieceType) {
                count++;
            }
        }
        return count;
    }

    public int evaluationFunction(GameDto gameDto, int color) {
        BoardDto boardDto = gameDto.getBoard();

        int boardValue = 0;
        int numhorse;
        int numtower;
        int numqueen;
        int numking;
        int numpawn;
        int numbishop;

        int tempColor = color;
        color = color == Constants.BLACK_COLOR ? Constants.WHITE_COLOR : Constants.BLACK_COLOR;

        numpawn = countPiecesByType(boardDto, color, PieceType.PAWN);
        boardValue = boardValue + 100 * (8 - numpawn);
        numhorse = countPiecesByType(boardDto, color, PieceType.HORSE);
        boardValue = boardValue + 300 * (2 - numhorse);
        numbishop = countPiecesByType(boardDto, color, PieceType.BISHOP);
        boardValue = boardValue + 325 * (2 - numbishop);
        numtower = countPiecesByType(boardDto, color, PieceType.TOWER);
        boardValue = boardValue + 500 * (2 - numtower);
        numqueen = countPiecesByType(boardDto, color, PieceType.QUEEN);
        boardValue = boardValue + 900 * (1 - numqueen);
        numking = countPiecesByType(boardDto, color, PieceType.KING);
        boardValue = boardValue + 3000 * (1 - numking);
        color = tempColor;

        boardValue = boardValue + evaluationPerPositioning(boardDto, color);
        boardValue = boardValue + extraPointsForCheck(gameDto, color);

        return boardValue;
    }

    public int extraPointsForCheck(GameDto gameDto, int color) {
        int score = 0;
        boolean whiteTurn = color == Constants.WHITE_COLOR;

        GameStatus gameStatus = classicPIAGameService.getGameStatus(gameDto, !whiteTurn);
        if (gameStatus == GameStatus.NORMAL) {
            return score;
        }

        if (color == Constants.WHITE_COLOR) {
            if (gameStatus == GameStatus.WHITE_CHECK) {
                score = 200;
            }
            else if (gameStatus == GameStatus.WHITE_WINS) {
                score = 5000;
            }
        }
        else {
            if (gameStatus == GameStatus.BLACK_CHECK) {
                score = 200;
            }
            else if (gameStatus == GameStatus.BLACK_WINS) {
                score = 5000;
            }
        }

        return score;
    }

    public int evaluationPerPositioning(BoardDto boardDto, int color) {
        Integer[][][] boardMatrix = boardDto.getPieceMatrix();

        int value = 0;
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                Integer[] piece = boardMatrix[i][j];
                if (piece == null || piece[0] == null) {
                    continue;
                }

                if (piece[1] == color) {
                    if ((i >= 3 && i <= 4) && (j >= 3 && j <= 4)) {
                        value = switch (PieceType.getFromOrdinal(piece[2])) {
                            case BISHOP, HORSE -> value + 40;
                            case KING -> value - 30;
                            case PAWN -> value + 10;
                            case QUEEN -> value + 60;
                            case TOWER -> value + 50;
                        };
                    }
                    else if ((i >= 2 && i <= 5) && (j >= 2 && j <= 5)) {
                        value = switch (PieceType.getFromOrdinal(piece[2])) {
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
        return value;
    }

}
