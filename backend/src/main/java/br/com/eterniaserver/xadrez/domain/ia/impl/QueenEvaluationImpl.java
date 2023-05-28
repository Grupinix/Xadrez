package br.com.eterniaserver.xadrez.domain.ia.impl;

import br.com.eterniaserver.xadrez.domain.ia.PieceEvaluation;
import br.com.eterniaserver.xadrez.rest.dtos.BoardDto;

public class QueenEvaluationImpl implements PieceEvaluation {

    private static final int[][] QUEEN_TABLE = {
            {-20, -10, -10, 0, 0, -10, -10, -20},
            {-10, 0, 5, 0, 0, 0, 0, -10},
            {-10, 5, 5, 5, 5, 5, 0, -10},
            {0, 0, 5, 5, 5, 5, 0, -5},
            {-5, 0, 5, 5, 5, 5, 0, -5},
            {-10, 0, 5, 5, 5, 5, 0, -10},
            {-10, 0, 0, 0, 0, 0, 0, -10},
            {-20, -10, -10, 0, 0, -10, -10, -20}
    };

    @Override
    public int getPieceValue() {
        return 900;
    }

    @Override
    public int getEvaluation(BoardDto boardDto, int positionX, int positionY, boolean isWhite) {
        positionX = getXPositionValueByColor(positionX, isWhite);

        return QUEEN_TABLE[positionX][positionY];
    }

}
