package br.com.eterniaserver.xadrez.domain.factory;

import br.com.eterniaserver.xadrez.domain.enums.PieceType;
import br.com.eterniaserver.xadrez.domain.ia.PieceEvaluation;
import br.com.eterniaserver.xadrez.domain.ia.impl.BishopEvaluationImpl;
import br.com.eterniaserver.xadrez.domain.ia.impl.HorseEvaluationImpl;
import br.com.eterniaserver.xadrez.domain.ia.impl.KingEvaluationImpl;
import br.com.eterniaserver.xadrez.domain.ia.impl.PawnEvaluationImpl;
import br.com.eterniaserver.xadrez.domain.ia.impl.QueenEvaluationImpl;
import br.com.eterniaserver.xadrez.domain.ia.impl.TowerEvaluationImpl;

public class PieceEvaluationFactory {

    private static final PieceEvaluation PAWN_EVALUATION = new PawnEvaluationImpl();
    private static final PieceEvaluation HORSE_EVALUATION = new HorseEvaluationImpl();
    private static final PieceEvaluation BISHOP_EVALUATION = new BishopEvaluationImpl();
    private static final PieceEvaluation TOWER_EVALUATION = new TowerEvaluationImpl();
    private static final PieceEvaluation QUEEN_EVALUATION = new QueenEvaluationImpl();
    private static final PieceEvaluation KING_EVALUATION = new KingEvaluationImpl();

    private PieceEvaluationFactory() {
        throw new IllegalStateException("Utility class");
    }

    public static PieceEvaluation getPieceEvaluation(PieceType pieceType) {
        return switch (pieceType) {
            case PAWN -> PAWN_EVALUATION;
            case HORSE -> HORSE_EVALUATION;
            case BISHOP -> BISHOP_EVALUATION;
            case TOWER -> TOWER_EVALUATION;
            case QUEEN -> QUEEN_EVALUATION;
            case KING -> KING_EVALUATION;
        };
    }

}
