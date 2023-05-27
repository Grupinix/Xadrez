package br.com.eterniaserver.xadrez.domain.ia;

import br.com.eterniaserver.xadrez.domain.enums.PieceType;
import br.com.eterniaserver.xadrez.domain.factory.PieceEvaluationFactory;
import br.com.eterniaserver.xadrez.rest.dtos.BoardDto;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class PawnEvaluationImplUnitTest {

    private static final int[][] PAWN_WHITE_TABLE = {
            {0, 0, 0, 0, 0, 0, 0, 0},
            {50, 50, 50, 50, 50, 50, 50, 50},
            {10, 10, 20, 30, 30, 20, 10, 10},
            {5, 5, 10, 25, 25, 10, 5, 5},
            {0, 0, 0, 20, 20, 0, 0, 0},
            {5, -5, -10, 0, 0, -10, -5, 5},
            {5, 10, 10, -10, -10, 10, 10, 5},
            {0, 0, 0, 0, 0, 0, 0, 0}
    };

    private static final int[][] PAWN_BLACK_TABLE = {
            {0, 0, 0, 0, 0, 0, 0, 0},
            {5, 10, 10, -10, -10, 10, 10, 5},
            {5, -5, -10, 0, 0, -10, -5, 5},
            {0, 0, 0, 20, 20, 0, 0, 0},
            {5, 5, 10, 25, 25, 10, 5, 5},
            {10, 10, 20, 30, 30, 20, 10, 10},
            {50, 50, 50, 50, 50, 50, 50, 50},
            {0, 0, 0, 0, 0, 0, 0, 0}
    };

    private PieceEvaluation pieceEvaluation;

    @BeforeEach
    void init() {
        pieceEvaluation = PieceEvaluationFactory.getPieceEvaluation(PieceType.PAWN);
    }

    @Test
    void testPositionEvaluationWhenWhite() {
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                Assertions.assertEquals(
                        PAWN_WHITE_TABLE[i][j],
                        pieceEvaluation.getEvaluation(new BoardDto(), i, j, true)
                );
            }
        }
    }

    @Test
    void testPositionEvaluationWhenBlack() {
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                Assertions.assertEquals(
                        PAWN_BLACK_TABLE[i][j],
                        pieceEvaluation.getEvaluation(new BoardDto(), i, j, false)
                );
            }
        }
    }
}
