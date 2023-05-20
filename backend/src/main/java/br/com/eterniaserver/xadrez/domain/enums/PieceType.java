package br.com.eterniaserver.xadrez.domain.enums;

public enum PieceType {

    PAWN,
    BISHOP,
    HORSE,
    KING,
    QUEEN,
    TOWER;

    public static PieceType getFromOrdinal(int ord) {
        return switch (ord) {
            case 0 -> PAWN;
            case 1 -> BISHOP;
            case 2 -> HORSE;
            case 3 -> KING;
            case 4 -> QUEEN;
            case 5 -> TOWER;
            default -> null;
        };
    }

}
