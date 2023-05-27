package br.com.eterniaserver.xadrez.domain.ia;

import br.com.eterniaserver.xadrez.Constants;
import br.com.eterniaserver.xadrez.domain.entities.Board;
import br.com.eterniaserver.xadrez.domain.entities.Game;
import br.com.eterniaserver.xadrez.domain.enums.GameType;
import br.com.eterniaserver.xadrez.domain.enums.PieceType;
import br.com.eterniaserver.xadrez.domain.ia.impl.GameIaImpl;
import br.com.eterniaserver.xadrez.domain.repositories.GameRepository;
import br.com.eterniaserver.xadrez.domain.repositories.PieceRepository;
import br.com.eterniaserver.xadrez.domain.service.impl.ClassicPIAGameServiceImpl;
import br.com.eterniaserver.xadrez.domain.entities.Piece;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

@ExtendWith(MockitoExtension.class)
public class GameIaImplUnitTest {

    private GameIaImpl gameIa;
    @Mock
    private GameRepository gameRepository;
    @Mock
    private PieceRepository pieceRepository;
    @Mock
    private ClassicPIAGameServiceImpl classicPIAGameService;
    private Board board;
    private Game game;

    @BeforeEach
    void init() {
        UUID uuidPlayer;
        gameIa = new GameIaImpl(gameRepository, pieceRepository, classicPIAGameService);
        board = new Board();
        board.setId(1);
        uuidPlayer = UUID.randomUUID();
        List<Piece> whitePieces = List.of(
                new Piece(1, board, null, PieceType.TOWER, 7, 0),
                new Piece(2, board, null, PieceType.HORSE, 7, 1),
                new Piece(3, board, null, PieceType.BISHOP, 7, 2),
                new Piece(4, board, null, PieceType.QUEEN, 7, 3),
                new Piece(5, board, null, PieceType.KING, 7, 4),
                new Piece(6, board, null, PieceType.BISHOP, 7, 5),
                new Piece(7, board, null, PieceType.HORSE, 7, 6),
                new Piece(8, board, null, PieceType.TOWER, 7, 7),
                new Piece(9, board, null, PieceType.PAWN, 6, 0),
                new Piece(10, board, null, PieceType.PAWN, 6, 1),
                new Piece(11, board, null, PieceType.PAWN, 6, 2),
                new Piece(12, board, null, PieceType.PAWN, 6, 3),
                new Piece(13, board, null, PieceType.PAWN, 6, 4),
                new Piece(14, board, null, PieceType.PAWN, 6, 5),
                new Piece(15, board, null, PieceType.PAWN, 6, 6),
                new Piece(16, board, null, PieceType.PAWN, 6, 7)
        );
        List<Piece> blackPieces = List.of(
                new Piece(17, null, board, PieceType.TOWER, 0, 0),
                new Piece(18, null, board, PieceType.HORSE, 0, 1),
                new Piece(19, null, board, PieceType.BISHOP, 0, 2),
                new Piece(20, null, board, PieceType.QUEEN, 0, 3),
                new Piece(21, null, board, PieceType.KING, 0, 4),
                new Piece(22, null, board, PieceType.BISHOP, 0, 5),
                new Piece(23, null, board, PieceType.HORSE, 0, 6),
                new Piece(24, null, board, PieceType.TOWER, 0, 7),
                new Piece(25, null, board, PieceType.PAWN, 1, 0),
                new Piece(26, null, board, PieceType.PAWN, 1, 1),
                new Piece(27, null, board, PieceType.PAWN, 1, 2),
                new Piece(28, null, board, PieceType.PAWN, 1, 3),
                new Piece(29, null, board, PieceType.PAWN, 1, 4),
                new Piece(30, null, board, PieceType.PAWN, 1, 5),
                new Piece(31, null, board, PieceType.PAWN, 1, 6),
                new Piece(32, null, board, PieceType.PAWN, 1, 7)
        );
        board.setWhitePieces(whitePieces);
        board.setBlackPieces(blackPieces);
        game = new Game();
        game.setId(1);
        game.setGameType(GameType.PLAYER_IA_CLASSIC);
        game.setBoard(board);
        game.setWhitePlayerUUID(uuidPlayer);
    }

    @Test
    void testCountPiecesByType_ShouldReturnCorrectCount_WhenValidParametersGiven() {
        int pawnCount = gameIa.countPiecesByType(board.getBoardDto(), Constants.WHITE_COLOR, PieceType.PAWN);
        int bishopCount = gameIa.countPiecesByType(board.getBoardDto(), Constants.WHITE_COLOR, PieceType.BISHOP);
        int towerCount = gameIa.countPiecesByType(board.getBoardDto(), Constants.WHITE_COLOR, PieceType.TOWER);
        int queenCount = gameIa.countPiecesByType(board.getBoardDto(), Constants.WHITE_COLOR, PieceType.QUEEN);

        Assertions.assertEquals(8, pawnCount);
        Assertions.assertEquals(2, bishopCount);
        Assertions.assertEquals(2, towerCount);
        Assertions.assertEquals(1, queenCount);
    }

    @Test
    void testEvaluationFunction_ShouldReturnCorrectValue_WhenValidParametersGiven() {
        int evaluation = gameIa.evaluationFunction(game.getGameDto(), Constants.WHITE_COLOR);

        int expectedEvaluation = 0;
        Assertions.assertEquals(expectedEvaluation, evaluation);
    }

    @Test
    void testEvaluationPerPositioning_ShouldReturnCorrectValue_WhenValidParametersGiven() {
        int evaluation = gameIa.evaluationPerPositioning(board.getBoardDto(), Constants.WHITE_COLOR);

        int expectedEvaluation = 0;
        Assertions.assertEquals(expectedEvaluation, evaluation);
    }
}