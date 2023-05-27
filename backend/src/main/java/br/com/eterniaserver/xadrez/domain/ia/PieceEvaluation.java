package br.com.eterniaserver.xadrez.domain.ia;

import br.com.eterniaserver.xadrez.rest.dtos.BoardDto;

public interface PieceEvaluation {

    int getPieceValue();

    int getEvaluation(BoardDto boardDto, int positionX, int positionY, boolean isWhite);

    default int getXPositionValueByColor(int positionX, boolean isWhite) {
        return isWhite ? positionX : 7 - positionX;
    }

}
