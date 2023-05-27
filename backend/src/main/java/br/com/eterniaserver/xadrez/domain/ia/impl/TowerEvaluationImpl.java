package br.com.eterniaserver.xadrez.domain.ia.impl;

import br.com.eterniaserver.xadrez.domain.ia.PieceEvaluation;
import br.com.eterniaserver.xadrez.rest.dtos.BoardDto;

public class TowerEvaluationImpl implements PieceEvaluation {

    private static final int[][] TOWER_TABLE = {
            {0, 0, 0, 0, 0, 0, 0, 0},
            {5, 10, 10, 10, 10, 10, 10, 5},
            {-5, 0, 0, 0, 0, 0, 0, -5},
            {-5, 0, 0, 0, 0, 0, 0, -5},
            {-5, 0, 0, 0, 0, 0, 0, -5},
            {-5, 0, 0, 0, 0, 0, 0, -5},
            {-5, 0, 0, 0, 0, 0, 0, -5},
            {0, 0, 0, 5, 5, 0, 0, 0}
    };

    @Override
    public int getPieceValue() {
        return 500;
    }

    @Override
    public int getEvaluation(BoardDto boardDto, int positionX, int positionY, boolean isWhite) {
        positionX = getXPositionValueByColor(positionX, isWhite);

        return TOWER_TABLE[positionX][positionY];
    }

}
