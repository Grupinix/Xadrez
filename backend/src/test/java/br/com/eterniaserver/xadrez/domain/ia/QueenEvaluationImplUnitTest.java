package br.com.eterniaserver.xadrez.domain.ia;

import br.com.eterniaserver.xadrez.domain.enums.PieceType;
import br.com.eterniaserver.xadrez.domain.factory.PieceEvaluationFactory;
import br.com.eterniaserver.xadrez.rest.dtos.BoardDto;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class QueenEvaluationImplUnitTest {

    private static final int[][] QUEEN_WHITE_TABLE = {
            {-20, -10, -10, 0, 0, -10, -10, -20},
            {-10, 0, 5, 0, 0, 0, 0, -10},
            {-10, 5, 5, 5, 5, 5, 0, -10},
            {0, 0, 5, 5, 5, 5, 0, -5},
            {-5, 0, 5, 5, 5, 5, 0, -5},
            {-10, 0, 5, 5, 5, 5, 0, -10},
            {-10, 0, 0, 0, 0, 0, 0, -10},
            {-20, -10, -10, 0, 0, -10, -10, -20}
    };

    private static final int[][] QUEEN_BLACK_TABLE = {
            {-20, -10, -10, 0, 0, -10, -10, -20},
            {-10, 0, 0, 0, 0, 0, 0, -10},
            {-10, 0, 5, 5, 5, 5, 0, -10},
            {-5, 0, 5, 5, 5, 5, 0, -5},
            {0, 0, 5, 5, 5, 5, 0, -5},
            {-10, 5, 5, 5, 5, 5, 0, -10},
            {-10, 0, 5, 0, 0, 0, 0, -10},
            {-20, -10, -10, 0, 0, -10, -10, -20}
    };

    private PieceEvaluation pieceEvaluation;

    @BeforeEach
    void init() {
        pieceEvaluation = PieceEvaluationFactory.getPieceEvaluation(PieceType.QUEEN);
    }

    @Test
    void testPositionEvaluationWhenWhite() {
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                Assertions.assertEquals(
                        QUEEN_WHITE_TABLE[i][j],
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
                        QUEEN_BLACK_TABLE[i][j],
                        pieceEvaluation.getEvaluation(new BoardDto(), i, j, false)
                );
            }
        }
    }
}
