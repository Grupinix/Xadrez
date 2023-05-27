package br.com.eterniaserver.xadrez.domain.ia.impl;

import br.com.eterniaserver.xadrez.domain.enums.PieceType;
import br.com.eterniaserver.xadrez.domain.ia.PieceEvaluation;
import br.com.eterniaserver.xadrez.rest.dtos.BoardDto;
import br.com.eterniaserver.xadrez.rest.dtos.PieceDto;

import java.util.List;

public class KingEvaluationImpl implements PieceEvaluation {

    private static final int END_GAME_MIN_PIECES = 4;

    private static final int[][] NORMAL_TABLE = new int[][] {
            {-30, -40, -40, -50, -50, -40, -40, -30},
            {-30, -40, -40, -50, -50, -40, -40, -30},
            {-30, -40, -40, -50, -50, -40, -40, -30},
            {-30, -40, -40, -50, -50, -40, -40, -30},
            {-20, -30, -30, -40, -40, -30, -30, -20},
            {-10, -20, -20, -20, -20, -20, -20, -10},
            {20, 20, 0, 0, 0, 0, 20, 20},
            {20, 30, 10, 0, 0, 10, 30, 20}
    };

    private static final int[][] END_TABLE = new int[][] {
            {-50, -40, -30, -20, -20, -30, -40, -50},
            {-30, -20, -10, 0, 0, -10, -20, -30},
            {-30, -10, 20, 30, 30, 20, -10, -30},
            {-30, -10, 30, 40, 40, 30, -10, -30},
            {-30, -10, 30, 40, 40, 30, -10, -30},
            {-30, -10, 20, 30, 30, 20, -10, -30},
            {-30, -30, 0, 0, 0, 0, -30, -30},
            {-50, -30, -30, -30, -30, -30, -30, -50}
    };

    @Override
    public int getPieceValue() {
        return 20000;
    }

    @Override
    public int getEvaluation(BoardDto boardDto, int positionX, int positionY, boolean isWhite) {
        List<PieceDto> whitePieces = boardDto.getWhitePieces();
        List<PieceDto> blackPieces = boardDto.getBlackPieces();

        PieceDto whiteQueen = whitePieces.stream()
                                         .filter(p -> p.getPieceType() == PieceType.QUEEN)
                                         .findFirst()
                                         .orElse(null);

        PieceDto blackQueen = blackPieces.stream()
                                         .filter(p -> p.getPieceType() == PieceType.QUEEN)
                                         .findFirst()
                                         .orElse(null);

        boolean blackInEnd = blackQueen == null || blackPieces.size() <= END_GAME_MIN_PIECES;
        boolean whiteInEnd = whiteQueen == null || whitePieces.size() <= END_GAME_MIN_PIECES;
        boolean endGame = blackInEnd && whiteInEnd;

        positionX = getXPositionValueByColor(positionX, isWhite);

        return endGame ? END_TABLE[positionX][positionY] : NORMAL_TABLE[positionX][positionY];
    }

}
