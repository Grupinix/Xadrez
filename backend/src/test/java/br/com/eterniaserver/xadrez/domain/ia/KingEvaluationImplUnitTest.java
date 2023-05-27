package br.com.eterniaserver.xadrez.domain.ia;

import br.com.eterniaserver.xadrez.domain.enums.PieceType;
import br.com.eterniaserver.xadrez.domain.factory.PieceEvaluationFactory;
import br.com.eterniaserver.xadrez.rest.dtos.BoardDto;
import br.com.eterniaserver.xadrez.rest.dtos.PieceDto;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

class KingEvaluationImplUnitTest {

    private static final int[][] KING_WHITE_NORMAL_GAME_TABLE = {
            {-30, -40, -40, -50, -50, -40, -40, -30},
            {-30, -40, -40, -50, -50, -40, -40, -30},
            {-30, -40, -40, -50, -50, -40, -40, -30},
            {-30, -40, -40, -50, -50, -40, -40, -30},
            {-20, -30, -30, -40, -40, -30, -30, -20},
            {-10, -20, -20, -20, -20, -20, -20, -10},
            {20, 20, 0, 0, 0, 0, 20, 20},
            {20, 30, 10, 0, 0, 10, 30, 20}
    };

    private static final int[][] KING_WHITE_END_GAME_TABLE = {
            {-50, -40, -30, -20, -20, -30, -40, -50},
            {-30, -20, -10, 0, 0, -10, -20, -30},
            {-30, -10, 20, 30, 30, 20, -10, -30},
            {-30, -10, 30, 40, 40, 30, -10, -30},
            {-30, -10, 30, 40, 40, 30, -10, -30},
            {-30, -10, 20, 30, 30, 20, -10, -30},
            {-30, -30, 0, 0, 0, 0, -30, -30},
            {-50, -30, -30, -30, -30, -30, -30, -50}
    };

    private static final int[][] KING_BLACK_NORMAL_GAME_TABLE = {
            {20, 30, 10, 0, 0, 10, 30, 20},
            {20, 20, 0, 0, 0, 0, 20, 20},
            {-10, -20, -20, -20, -20, -20, -20, -10},
            {-20, -30, -30, -40, -40, -30, -30, -20},
            {-30, -40, -40, -50, -50, -40, -40, -30},
            {-30, -40, -40, -50, -50, -40, -40, -30},
            {-30, -40, -40, -50, -50, -40, -40, -30},
            {-30, -40, -40, -50, -50, -40, -40, -30}
    };

    private static final int[][] KING_BLACK_END_GAME_TABLE = {
            {-50, -30, -30, -30, -30, -30, -30, -50},
            {-30, -30, 0, 0, 0, 0, -30, -30},
            {-30, -10, 20, 30, 30, 20, -10, -30},
            {-30, -10, 30, 40, 40, 30, -10, -30},
            {-30, -10, 30, 40, 40, 30, -10, -30},
            {-30, -10, 20, 30, 30, 20, -10, -30},
            {-30, -20, -10, 0, 0, -10, -20, -30},
            {-50, -40, -30, -20, -20, -30, -40, -50}
    };

    private PieceEvaluation pieceEvaluation;
    private BoardDto normalGameBoard;
    private BoardDto endGameBoard;

    @BeforeEach
    void init() {
        pieceEvaluation = PieceEvaluationFactory.getPieceEvaluation(PieceType.KING);
        normalGameBoard = new BoardDto();
        endGameBoard = new BoardDto();

        normalGameBoard.setWhitePieces(List.of(
                PieceDto.builder().pieceType(PieceType.QUEEN).build(),
                PieceDto.builder().pieceType(PieceType.PAWN).build(),
                PieceDto.builder().pieceType(PieceType.PAWN).build(),
                PieceDto.builder().pieceType(PieceType.PAWN).build(),
                PieceDto.builder().pieceType(PieceType.KING).build()
        ));
        normalGameBoard.setBlackPieces(List.of(
                PieceDto.builder().pieceType(PieceType.QUEEN).build(),
                PieceDto.builder().pieceType(PieceType.PAWN).build(),
                PieceDto.builder().pieceType(PieceType.PAWN).build(),
                PieceDto.builder().pieceType(PieceType.PAWN).build(),
                PieceDto.builder().pieceType(PieceType.KING).build()
        ));

        endGameBoard.setWhitePieces(List.of(
                PieceDto.builder().pieceType(PieceType.KING).build()
        ));
        endGameBoard.setBlackPieces(List.of(
                PieceDto.builder().pieceType(PieceType.KING).build()
        ));
    }

    @Test
    void testPositionEvaluationNormalGameWhenWhite() {
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                Assertions.assertEquals(
                        KING_WHITE_NORMAL_GAME_TABLE[i][j],
                        pieceEvaluation.getEvaluation(normalGameBoard, i, j, true)
                );
            }
        }
    }

    @Test
    void testPositionEvaluationNormalGameWhenBlack() {
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                Assertions.assertEquals(
                        KING_BLACK_NORMAL_GAME_TABLE[i][j],
                        pieceEvaluation.getEvaluation(normalGameBoard, i, j, false)
                );
            }
        }
    }

    @Test
    void testPositionEvaluationEndGameWhenWhite() {
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                Assertions.assertEquals(
                        KING_WHITE_END_GAME_TABLE[i][j],
                        pieceEvaluation.getEvaluation(endGameBoard, i, j, true)
                );
            }
        }
    }

    @Test
    void testPositionEvaluationEndGameWhenBlack() {
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                Assertions.assertEquals(
                        KING_BLACK_END_GAME_TABLE[i][j],
                        pieceEvaluation.getEvaluation(endGameBoard, i, j, false)
                );
            }
        }
    }
}
