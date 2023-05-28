package br.com.eterniaserver.xadrez.domain.ia.impl;

import br.com.eterniaserver.xadrez.domain.ia.PieceEvaluation;
import br.com.eterniaserver.xadrez.rest.dtos.BoardDto;

public class PawnEvaluationImpl implements PieceEvaluation {

    private static final int[][] PAWN_TABLE = {
            {0, 0, 0, 0, 0, 0, 0, 0},
            {50, 50, 50, 50, 50, 50, 50, 50},
            {10, 10, 20, 30, 30, 20, 10, 10},
            {5, 5, 10, 25, 25, 10, 5, 5},
            {0, 0, 0, 20, 20, 0, 0, 0},
            {5, -5, -10, 0, 0, -10, -5, 5},
            {5, 10, 10, -10, -10, 10, 10, 5},
            {0, 0, 0, 0, 0, 0, 0, 0}
    };

    @Override
    public int getPieceValue() {
        return 100;
    }

    @Override
    public int getEvaluation(BoardDto boardDto, int positionX, int positionY, boolean isWhite) {
        positionX = getXPositionValueByColor(positionX, isWhite);

        return PAWN_TABLE[positionX][positionY];
    }

}
