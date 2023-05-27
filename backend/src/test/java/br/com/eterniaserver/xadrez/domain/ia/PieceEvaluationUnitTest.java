package br.com.eterniaserver.xadrez.domain.ia;

import br.com.eterniaserver.xadrez.rest.dtos.BoardDto;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class PieceEvaluationUnitTest {

    private PieceEvaluation pieceEvaluation;

    @BeforeEach
    void init() {
        pieceEvaluation = new PieceEvaluation() {
            @Override
            public int getPieceValue() {
                return 0;
            }
            @Override
            public int getEvaluation(BoardDto boardDto, int positionX, int positionY, boolean isWhite) {
                return 0;
            }
        };
    }

    @Test
    void validPositionXWhenWhite() {
        Assertions.assertEquals(7, pieceEvaluation.getXPositionValueByColor(7, true));
        Assertions.assertEquals(6, pieceEvaluation.getXPositionValueByColor(6, true));
        Assertions.assertEquals(5, pieceEvaluation.getXPositionValueByColor(5, true));
        Assertions.assertEquals(4, pieceEvaluation.getXPositionValueByColor(4, true));
        Assertions.assertEquals(3, pieceEvaluation.getXPositionValueByColor(3, true));
        Assertions.assertEquals(2, pieceEvaluation.getXPositionValueByColor(2, true));
        Assertions.assertEquals(1, pieceEvaluation.getXPositionValueByColor(1, true));
        Assertions.assertEquals(0, pieceEvaluation.getXPositionValueByColor(0, true));
    }

    @Test
    void validPositionXWhenBlack() {
        Assertions.assertEquals(7, pieceEvaluation.getXPositionValueByColor(0, false));
        Assertions.assertEquals(6, pieceEvaluation.getXPositionValueByColor(1, false));
        Assertions.assertEquals(5, pieceEvaluation.getXPositionValueByColor(2, false));
        Assertions.assertEquals(4, pieceEvaluation.getXPositionValueByColor(3, false));
        Assertions.assertEquals(3, pieceEvaluation.getXPositionValueByColor(4, false));
        Assertions.assertEquals(2, pieceEvaluation.getXPositionValueByColor(5, false));
        Assertions.assertEquals(1, pieceEvaluation.getXPositionValueByColor(6, false));
        Assertions.assertEquals(0, pieceEvaluation.getXPositionValueByColor(7, false));
    }
}
