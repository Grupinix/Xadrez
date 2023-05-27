package br.com.eterniaserver.xadrez.domain.ia;

import br.com.eterniaserver.xadrez.Constants;
import br.com.eterniaserver.xadrez.rest.dtos.BoardDto;
import br.com.eterniaserver.xadrez.rest.dtos.GameDto;
import br.com.eterniaserver.xadrez.rest.dtos.PieceDto;
import br.com.eterniaserver.xadrez.domain.enums.GameType;
import br.com.eterniaserver.xadrez.domain.enums.PieceType;
import br.com.eterniaserver.xadrez.domain.ia.impl.GameIaImpl;
import br.com.eterniaserver.xadrez.domain.repositories.GameRepository;
import br.com.eterniaserver.xadrez.domain.repositories.PieceRepository;
import br.com.eterniaserver.xadrez.domain.service.impl.ClassicPIAGameServiceImpl;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@ExtendWith(MockitoExtension.class)
class GameIaImplUnitTest {

    private GameIaImpl gameIa;
    @Mock
    private GameRepository gameRepository;
    @Mock
    private PieceRepository pieceRepository;
    @Mock
    private ClassicPIAGameServiceImpl classicPIAGameService;

    private BoardDto boardDto;
    private GameDto gameDto;

    @BeforeEach
    void init() {
        UUID uuidPlayer;
        gameIa = new GameIaImpl(gameRepository, pieceRepository, classicPIAGameService);
        boardDto = new BoardDto();
        boardDto.setId(1);
        uuidPlayer = UUID.randomUUID();
        List<PieceDto> whitePieces = List.of(
                new PieceDto(1, true, PieceType.TOWER, 7, 0),
                new PieceDto(2, true, PieceType.HORSE, 7, 1),
                new PieceDto(3, true, PieceType.BISHOP, 7, 2),
                new PieceDto(4, true, PieceType.QUEEN, 7, 3),
                new PieceDto(5, true, PieceType.KING, 7, 4),
                new PieceDto(6, true, PieceType.BISHOP, 7, 5),
                new PieceDto(7, true, PieceType.HORSE, 7, 6),
                new PieceDto(8, true, PieceType.TOWER, 7, 7),
                new PieceDto(9, true, PieceType.PAWN, 6, 0),
                new PieceDto(10, true, PieceType.PAWN, 6, 1),
                new PieceDto(11, true, PieceType.PAWN, 6, 2),
                new PieceDto(12, true, PieceType.PAWN, 6, 3),
                new PieceDto(13, true, PieceType.PAWN, 6, 4),
                new PieceDto(14, true, PieceType.PAWN, 6, 5),
                new PieceDto(15, true, PieceType.PAWN, 6, 6),
                new PieceDto(16, true, PieceType.PAWN, 6, 7)
        );
        List<PieceDto> blackPieces = List.of(
                new PieceDto(17, false, PieceType.TOWER, 0, 0),
                new PieceDto(18, false, PieceType.HORSE, 0, 1),
                new PieceDto(19, false, PieceType.BISHOP, 0, 2),
                new PieceDto(20, false, PieceType.QUEEN, 0, 3),
                new PieceDto(21, false, PieceType.KING, 0, 4),
                new PieceDto(22, false, PieceType.BISHOP, 0, 5),
                new PieceDto(23, false, PieceType.HORSE, 0, 6),
                new PieceDto(24, false, PieceType.TOWER, 0, 7),
                new PieceDto(25, false, PieceType.PAWN, 1, 0),
                new PieceDto(26, false, PieceType.PAWN, 1, 1),
                new PieceDto(27, false, PieceType.PAWN, 1, 2),
                new PieceDto(28, false, PieceType.PAWN, 1, 3),
                new PieceDto(29, false, PieceType.PAWN, 1, 4),
                new PieceDto(30, false, PieceType.PAWN, 1, 5),
                new PieceDto(31, false, PieceType.PAWN, 1, 6),
                new PieceDto(32, false, PieceType.PAWN, 1, 7)
        );

        Integer[][][] pieceMatrix = new Integer[8][8][1];
        for (PieceDto piece : whitePieces) {
            pieceMatrix[piece.getPositionX()][piece.getPositionY()] = new Integer[] {
                    piece.getId(), Constants.WHITE_COLOR, piece.getPieceType().ordinal()
            };
        }
        for (PieceDto piece : blackPieces) {
            pieceMatrix[piece.getPositionX()][piece.getPositionY()] = new Integer[] {
                    piece.getId(), Constants.BLACK_COLOR, piece.getPieceType().ordinal()
            };
        }

        boardDto.setWhitePieces(whitePieces);
        boardDto.setBlackPieces(blackPieces);
        boardDto.setPieceMatrix(pieceMatrix);
        boardDto.setHistories(new ArrayList<>());
        gameDto = new GameDto();
        gameDto.setId(1);
        gameDto.setGameType(GameType.PLAYER_IA_CLASSIC);
        gameDto.setBoard(boardDto);
        gameDto.setWhitePlayerUUID(uuidPlayer);
    }

    @Test
    void testCountPiecesByTypeShouldReturnCorrectCountWhenValidParametersGiven() {
        int pawnCount = gameIa.countPiecesByType(boardDto, Constants.WHITE_COLOR, PieceType.PAWN);
        int bishopCount = gameIa.countPiecesByType(boardDto, Constants.WHITE_COLOR, PieceType.BISHOP);
        int towerCount = gameIa.countPiecesByType(boardDto, Constants.WHITE_COLOR, PieceType.TOWER);
        int queenCount = gameIa.countPiecesByType(boardDto, Constants.WHITE_COLOR, PieceType.QUEEN);

        Assertions.assertEquals(8, pawnCount);
        Assertions.assertEquals(2, bishopCount);
        Assertions.assertEquals(2, towerCount);
        Assertions.assertEquals(1, queenCount);
    }

    @Test
    void testEvaluationFunctionShouldReturnCorrectValueWhenValidParametersGiven() {
        int evaluation = gameIa.evaluationFunction(gameDto, Constants.WHITE_COLOR);

        int expectedEvaluation = 0;
        Assertions.assertEquals(expectedEvaluation, evaluation);
    }

    @Test
    void testEvaluationPerPositioningShouldReturnCorrectValueWhenValidParametersGiven() {
        int evaluation = gameIa.evaluationPerPositioning(boardDto, Constants.WHITE_COLOR);

        int expectedEvaluation = 0;
        Assertions.assertEquals(expectedEvaluation, evaluation);
    }
}
