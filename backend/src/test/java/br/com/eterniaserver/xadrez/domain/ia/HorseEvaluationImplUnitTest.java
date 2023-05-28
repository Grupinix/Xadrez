package br.com.eterniaserver.xadrez.domain.ia;

import br.com.eterniaserver.xadrez.domain.enums.PieceType;
import br.com.eterniaserver.xadrez.domain.factory.PieceEvaluationFactory;
import br.com.eterniaserver.xadrez.rest.dtos.BoardDto;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class HorseEvaluationImplUnitTest {

    private static final int[][] HORSE_WHITE_TABLE = {
            {-50, -5, -30, -30, -30, -30, -5, -50},
            {-40, -20, 0, 0, 0, 0, -20, -40},
            {-30, 0, 10, 15, 15, 10, 0, -30},
            {-30, 5, 15, 20, 20, 15, 5, -30},
            {-30, 0, 15, 20, 20, 15, 0, -30},
            {-30, 5, 10, 15, 15, 10, 5, -30},
            {-40, -20, 0, 5, 5, 0, -20, -40},
            {-50, -5, -30, -30, -30, -30, -5, -50}
    };

    private static final int[][] HORSE_BLACK_TABLE = {
            {-50, -5, -30, -30, -30, -30, -5, -50},
            {-40, -20, 0, 5, 5, 0, -20, -40},
            {-30, 5, 10, 15, 15, 10, 5, -30},
            {-30, 0, 15, 20, 20, 15, 0, -30},
            {-30, 5, 15, 20, 20, 15, 5, -30},
            {-30, 0, 10, 15, 15, 10, 0, -30},
            {-40, -20, 0, 0, 0, 0, -20, -40},
            {-50, -5, -30, -30, -30, -30, -5, -50}
    };

    private PieceEvaluation pieceEvaluation;

    @BeforeEach
    void init() {
        pieceEvaluation = PieceEvaluationFactory.getPieceEvaluation(PieceType.HORSE);
    }

    @Test
    void testPositionEvaluationWhenWhite() {
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                Assertions.assertEquals(
                        HORSE_WHITE_TABLE[i][j],
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
                        HORSE_BLACK_TABLE[i][j],
                        pieceEvaluation.getEvaluation(new BoardDto(), i, j, false)
                );
            }
        }
    }
}
