package br.com.eterniaserver.xadrez.domain.ia.impl;

import br.com.eterniaserver.xadrez.domain.ia.PieceEvaluation;
import br.com.eterniaserver.xadrez.rest.dtos.BoardDto;

public class BishopEvaluationImpl implements PieceEvaluation {

    private static final int[][] BISHOP_TABLE = {
            {-20, -10, -10, -10, -10, -10, -10, -20},
            {-10, 5, 0, 0, 0, 0, 5, -10},
            {-10, 10, 10, 10, 10, 10, 10, -10},
            {-10, 0, 10, 10, 10, 10, 0, -10},
            {-10, 5, 5, 10, 10, 5, 5, -10},
            {-10, 0, 5, 10, 10, 5, 0, -10},
            {-10, 0, 0, 0, 0, 0, 0, -10},
            {-20, -10, -10, -10, -10, -10, -10, -20}
    };

    @Override
    public int getPieceValue() {
        return 325;
    }

    @Override
    public int getEvaluation(BoardDto boardDto, int positionX, int positionY, boolean isWhite) {
        positionX = getXPositionValueByColor(positionX, isWhite);

        return BISHOP_TABLE[positionX][positionY];
    }

}
