package br.com.eterniaserver.xadrez.domain.service;

import br.com.eterniaserver.xadrez.domain.entities.Board;
import br.com.eterniaserver.xadrez.domain.entities.Piece;
import br.com.eterniaserver.xadrez.domain.enums.PieceType;
import br.com.eterniaserver.xadrez.domain.repositories.BoardRepository;
import br.com.eterniaserver.xadrez.domain.repositories.PieceRepository;
import br.com.eterniaserver.xadrez.domain.service.impl.BoardServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class BoardServiceImplUnitTest {

    private static final int PAWN_START_INDEX = 8;
    private static final int WHITE_MAIN_X_POSITION = 7;
    private static final int BLACK_MAIN_X_POSITION = 0;

    @Mock
    private BoardRepository boardRepository;
    @Mock
    private PieceRepository pieceRepository;

    private BoardServiceImpl boardService;

    @BeforeEach
    void init() {
        boardService = new BoardServiceImpl(boardRepository, pieceRepository);
    }

    @Test
    void verifyPawns() {
        Board board = boardService.createBoard();

        for (int i = 0; i < 8; i++) {
            Piece whitePiece = board.getWhitePieces().get(i + PAWN_START_INDEX);
            Piece blackPiece = board.getBlackPieces().get(i + PAWN_START_INDEX);

            Assertions.assertEquals(PieceType.PAWN, whitePiece.getPieceType());
            Assertions.assertEquals(PieceType.PAWN, blackPiece.getPieceType());
        }
    }

    @Test
    void verifyQueenPosition() {
        Board board = boardService.createBoard();

        int queenIndex = 3;

        Piece whitePiece = board.getWhitePieces().get(queenIndex);
        Piece blackPiece = board.getBlackPieces().get(queenIndex);

        Assertions.assertEquals(PieceType.QUEEN, whitePiece.getPieceType());
        Assertions.assertEquals(PieceType.QUEEN, blackPiece.getPieceType());

        Assertions.assertEquals(PieceType.QUEEN, whitePiece.getPieceType());
        Assertions.assertEquals(WHITE_MAIN_X_POSITION, whitePiece.getPositionX());
        Assertions.assertEquals(queenIndex, whitePiece.getPositionY());

        Assertions.assertEquals(PieceType.QUEEN, blackPiece.getPieceType());
        Assertions.assertEquals(BLACK_MAIN_X_POSITION, blackPiece.getPositionX());
        Assertions.assertEquals(queenIndex, blackPiece.getPositionY());
    }

    @Test
    void verifyKingPosition() {
        Board board = boardService.createBoard();

        int kingIndex = 4;

        Piece whitePiece = board.getWhitePieces().get(kingIndex);
        Piece blackPiece = board.getBlackPieces().get(kingIndex);

        Assertions.assertEquals(PieceType.KING, whitePiece.getPieceType());
        Assertions.assertEquals(WHITE_MAIN_X_POSITION, whitePiece.getPositionX());
        Assertions.assertEquals(kingIndex, whitePiece.getPositionY());

        Assertions.assertEquals(PieceType.KING, blackPiece.getPieceType());
        Assertions.assertEquals(BLACK_MAIN_X_POSITION, blackPiece.getPositionX());
        Assertions.assertEquals(kingIndex, blackPiece.getPositionY());
    }

}
