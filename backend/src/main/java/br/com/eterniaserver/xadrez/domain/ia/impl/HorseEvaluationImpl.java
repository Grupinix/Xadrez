package br.com.eterniaserver.xadrez.domain.ia.impl;

import br.com.eterniaserver.xadrez.domain.ia.PieceEvaluation;
import br.com.eterniaserver.xadrez.rest.dtos.BoardDto;

public class HorseEvaluationImpl implements PieceEvaluation {

    private static final int[][] HORSE_TABLE = {
            {-50, -5, -30, -30, -30, -30, -5, -50},
            {-40, -20,   0,   0,   0,   0, -20, -40},
            {-30,   0,  10,  15,  15,  10,   0, -30},
            {-30,   5,  15,  20,  20,  15,   5, -30},
            {-30,   0,  15,  20,  20,  15,   0, -30},
            {-30,   5,  10,  15,  15,  10,   5, -30},
            {-40, -20,   0,   5,   5,   0, -20, -40},
            {-50, -5, -30, -30, -30, -30, -5, -50}
    };

    @Override
    public int getPieceValue() {
        return 300;
    }

    @Override
    public int getEvaluation(BoardDto boardDto, int positionX, int positionY, boolean isWhite) {
        positionX = getXPositionValueByColor(positionX, isWhite);

        return HORSE_TABLE[positionX][positionY];
    }

}
