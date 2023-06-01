package br.com.eterniaserver.xadrez.domain.ia.impl;

import br.com.eterniaserver.xadrez.Constants;
import br.com.eterniaserver.xadrez.domain.entities.Game;
import br.com.eterniaserver.xadrez.domain.entities.Piece;
import br.com.eterniaserver.xadrez.domain.enums.GameDifficulty;
import br.com.eterniaserver.xadrez.domain.enums.GameStatus;
import br.com.eterniaserver.xadrez.domain.enums.PieceType;
import br.com.eterniaserver.xadrez.domain.factory.PieceEvaluationFactory;
import br.com.eterniaserver.xadrez.domain.ia.GameIa;
import br.com.eterniaserver.xadrez.domain.ia.PieceEvaluation;
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
                gameDto, Constants.BLACK_COLOR, Constants.WHITE_COLOR, depth
        );

        if (move == null) {
            game.setWhiteTurn(true);
            game.setStatusCached(true);
            game.setGameStatus(GameStatus.WHITE_WINS);
            gameRepository.save(game);
            return;
        }

        PieceDto pieceDto = move.getFirst();
        MoveDto moveDto = move.getSecond();

        Piece piece = pieceRepository.findById(pieceDto.getId()).orElseThrow();

        classicPIAGameService.movePiece(game, null, piece, moveDto);
    }

    public Pair<PieceDto, MoveDto> miniMax(GameDto gameDto,
                                           int currentColor,
                                           int opponentColor,
                                           int depth) {
        Map<PieceDto, List<MoveDto>> legalMovesMap = classicPIAGameService.getPlayerLegalMoves(gameDto, currentColor);

        List<GameStatus> validStatus = List.of(GameStatus.NORMAL, GameStatus.BLACK_CHECK, GameStatus.BLACK_WINS);
        int highestValue = Integer.MIN_VALUE;
        Pair<PieceDto, MoveDto> bestMove = null;

        for (Map.Entry<PieceDto, List<MoveDto>> pieceDtoListEntry : legalMovesMap.entrySet()) {
            PieceDto pieceDto = pieceDtoListEntry.getKey();
            List<MoveDto> legalMoves = pieceDtoListEntry.getValue();
            legalMoves = classicPIAGameService.getValidMoves(
                    legalMoves,
                    validStatus,
                    gameDto,
                    pieceDto,
                    false
            );
            for (MoveDto legalMove : legalMoves) {
                GameDto tempGame = gameDto.copy();

                classicPIAGameService.movePieceOnBoardDto(tempGame, pieceDtoListEntry.getKey(), legalMove);
                tempGame.setStatusCached(false);

                GameStatus gameStatus = classicPIAGameService.getGameStatus(tempGame);

                if (validStatus.contains(gameStatus)) {
                    int actualBoardValue = evaluationFunction(tempGame, currentColor);

                    actualBoardValue = minimaxValue(
                            tempGame,
                            opponentColor,
                            currentColor,
                            depth - 1,
                            actualBoardValue
                    );

                    if (actualBoardValue > highestValue) {
                        highestValue = actualBoardValue;
                        bestMove = Pair.of(pieceDtoListEntry.getKey(), legalMove);
                    }
                }
            }
        }
        return bestMove;
    }

    private int minimaxValue(GameDto gameDto,
                             int currentColor,
                             int opponentColor,
                             int depth,
                             int bestBoardValue) {

        if (depth == 0) {
            return bestBoardValue;
        }

        Map<PieceDto, List<MoveDto>> legalMovesMap = classicPIAGameService.getPlayerLegalMoves(gameDto, currentColor);

        int boardValue = bestBoardValue;
        int maxValue = Integer.MIN_VALUE;
        int minValue = Integer.MAX_VALUE;

        for (Map.Entry<PieceDto, List<MoveDto>> pieceDtoListEntry : legalMovesMap.entrySet()) {
            List<MoveDto> legalMoves = pieceDtoListEntry.getValue();
            for (MoveDto legalMove : legalMoves) {
                GameDto tempGame = gameDto.copy();

                classicPIAGameService.movePieceOnBoardDto(tempGame, pieceDtoListEntry.getKey(), legalMove);

                int value = bestBoardValue;
                if (currentColor == Constants.BLACK_COLOR) {
                    value += evaluationFunction(tempGame, currentColor);
                }
                else {
                    value -= evaluationFunction(tempGame, currentColor);
                }

                if (depth >= 1) {
                    value = minimaxValue(
                            tempGame,
                            opponentColor,
                            currentColor,
                            depth - 1,
                            value
                    );
                }


                if (currentColor == Constants.BLACK_COLOR) {
                    if (value > maxValue) {
                        maxValue = value;
                        boardValue = value;
                    }
                }
                else {
                    if (value < minValue) {
                        minValue = value;
                        boardValue = value;
                    }
                }
            }
        }

        return boardValue;
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

    private int getPieceValue(PieceType pieceType) {
        return PieceEvaluationFactory.getPieceEvaluation(pieceType).getPieceValue();
    }

    public int evaluationFunction(GameDto gameDto, int color) {
        BoardDto boardDto = gameDto.getBoard();

        int boardValue = 0;

        int oponentColor = color == Constants.BLACK_COLOR ? Constants.WHITE_COLOR : Constants.BLACK_COLOR;
        int numpawn = countPiecesByType(boardDto, oponentColor, PieceType.PAWN);
        boardValue = boardValue + getPieceValue(PieceType.PAWN) * (8 - numpawn);
        int numhorse = countPiecesByType(boardDto, oponentColor, PieceType.HORSE);
        boardValue = boardValue + getPieceValue(PieceType.HORSE) * (2 - numhorse);
        int numbishop = countPiecesByType(boardDto, oponentColor, PieceType.BISHOP);
        boardValue = boardValue + getPieceValue(PieceType.BISHOP) * (2 - numbishop);
        int numtower = countPiecesByType(boardDto, oponentColor, PieceType.TOWER);
        boardValue = boardValue + getPieceValue(PieceType.TOWER) * (2 - numtower);
        int numqueen = countPiecesByType(boardDto, oponentColor, PieceType.QUEEN);
        boardValue = boardValue + getPieceValue(PieceType.QUEEN) * (1 - numqueen);
        int numking = countPiecesByType(boardDto, oponentColor, PieceType.KING);
        boardValue = boardValue + getPieceValue(PieceType.KING) * (1 - numking);

        boardValue = boardValue + evaluationPerPositioning(boardDto, color);
        boardValue = boardValue + extraPointsForCheck(gameDto, color);

        return boardValue;
    }

    public int extraPointsForCheck(GameDto gameDto, int color) {
        int score = 0;

        GameStatus gameStatus = classicPIAGameService.getGameStatus(gameDto);
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
                    PieceType pieceType = PieceType.getFromOrdinal(piece[2]);
                    boolean isWhite = color == Constants.WHITE_COLOR;

                    PieceEvaluation pieceEvaluation = PieceEvaluationFactory.getPieceEvaluation(pieceType);

                    value += pieceEvaluation.getEvaluation(boardDto, i, j, isWhite);
                }
            }
        }

        return value;
    }

}
