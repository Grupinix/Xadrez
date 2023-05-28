package br.com.eterniaserver.xadrez.domain.ia;

import br.com.eterniaserver.xadrez.domain.enums.PieceType;
import br.com.eterniaserver.xadrez.domain.factory.PieceEvaluationFactory;
import br.com.eterniaserver.xadrez.rest.dtos.BoardDto;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class TowerEvaluationImplUnitTest {

    private static final int[][] TOWER_WHITE_TABLE = {
            {0, 0, 0, 0, 0, 0, 0, 0},
            {5, 10, 10, 10, 10, 10, 10, 5},
            {-5, 0, 0, 0, 0, 0, 0, -5},
            {-5, 0, 0, 0, 0, 0, 0, -5},
            {-5, 0, 0, 0, 0, 0, 0, -5},
            {-5, 0, 0, 0, 0, 0, 0, -5},
            {-5, 0, 0, 0, 0, 0, 0, -5},
            {0, 0, 0, 5, 5, 0, 0, 0}
    };

    private static final int[][] TOWER_BLACK_TABLE = {
            {0, 0, 0, 5, 5, 0, 0, 0},
            {-5, 0, 0, 0, 0, 0, 0, -5},
            {-5, 0, 0, 0, 0, 0, 0, -5},
            {-5, 0, 0, 0, 0, 0, 0, -5},
            {-5, 0, 0, 0, 0, 0, 0, -5},
            {-5, 0, 0, 0, 0, 0, 0, -5},
            {5, 10, 10, 10, 10, 10, 10, 5},
            {0, 0, 0, 0, 0, 0, 0, 0}
    };

    private PieceEvaluation pieceEvaluation;

    @BeforeEach
    void init() {
        pieceEvaluation = PieceEvaluationFactory.getPieceEvaluation(PieceType.TOWER);
    }

    @Test
    void testPositionEvaluationWhenWhite() {
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                Assertions.assertEquals(
                        TOWER_WHITE_TABLE[i][j],
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
                        TOWER_BLACK_TABLE[i][j],
                        pieceEvaluation.getEvaluation(new BoardDto(), i, j, false)
                );
            }
        }
    }
}
