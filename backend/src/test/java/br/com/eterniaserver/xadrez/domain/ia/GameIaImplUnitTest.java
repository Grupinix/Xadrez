package br.com.eterniaserver.xadrez.domain.ia;

import br.com.eterniaserver.xadrez.Constants;
import br.com.eterniaserver.xadrez.domain.entities.Piece;
import br.com.eterniaserver.xadrez.domain.enums.GameStatus;
import br.com.eterniaserver.xadrez.domain.enums.MoveType;
import br.com.eterniaserver.xadrez.domain.repositories.BoardRepository;
import br.com.eterniaserver.xadrez.domain.repositories.HistoryRepository;
import br.com.eterniaserver.xadrez.domain.service.BoardService;
import br.com.eterniaserver.xadrez.domain.service.PlayerService;
import br.com.eterniaserver.xadrez.rest.dtos.BoardDto;
import br.com.eterniaserver.xadrez.rest.dtos.GameDto;
import br.com.eterniaserver.xadrez.rest.dtos.MoveDto;
import br.com.eterniaserver.xadrez.rest.dtos.PieceDto;
import br.com.eterniaserver.xadrez.domain.enums.GameType;
import br.com.eterniaserver.xadrez.domain.enums.PieceType;
import br.com.eterniaserver.xadrez.domain.ia.impl.GameIaImpl;
import br.com.eterniaserver.xadrez.domain.repositories.GameRepository;
import br.com.eterniaserver.xadrez.domain.repositories.PieceRepository;
import br.com.eterniaserver.xadrez.domain.service.impl.ClassicPIAGameServiceImpl;
import br.com.eterniaserver.xadrez.domain.entities.Game;
import br.com.eterniaserver.xadrez.domain.enums.GameDifficulty;

import br.com.eterniaserver.xadrez.rest.dtos.PositionDto;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.data.util.Pair;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
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
    void testMovePieceShouldNotInvokeMovePieceServiceWhenGameNotFound() {
        Mockito.when(gameRepository.findById(boardDto.getId())).thenReturn(Optional.empty());

        gameIa.movePiece(boardDto.getId());

        Mockito.verify(classicPIAGameService, Mockito.never()).movePiece(
                Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any()
        );
    }

    @Test
    void testMovePieceShouldInvokeMovePieceServiceWhenGameFound() {
        Game game = Mockito.mock(Game.class);
        Piece piece = Mockito.mock(Piece.class);
        Mockito.when(game.getId()).thenReturn(1);
        Mockito.when(game.getGameDto()).thenReturn(gameDto);
        PieceDto pieceDto = boardDto.getWhitePieces().get(8);
        MoveDto moveDto = new MoveDto();
        moveDto.setFirst(MoveType.NORMAL);
        moveDto.setSecond(new PositionDto(3,4));

        Pair <PieceDto, MoveDto> move = Pair.of(pieceDto, moveDto);
        gameDto.setGameDifficulty(GameDifficulty.EASY);
        gameDto.setWhiteTurn(false);

        Map<PieceDto, List<MoveDto>> legalmoves = new HashMap<>();
        legalmoves.put(pieceDto, List.of(moveDto));

        Mockito.when(classicPIAGameService.getGameStatus(Mockito.any(GameDto.class))).thenReturn(GameStatus.NORMAL);
        Mockito.when(classicPIAGameService.getPlayerLegalMoves(gameDto, Constants.BLACK_COLOR)).thenReturn(legalmoves);
        Mockito.when(gameRepository.findById(1)).thenReturn(Optional.of(game));
        Mockito.when(pieceRepository.findById(pieceDto.getId())).thenReturn(Optional.of(piece));

        gameIa.movePiece(game.getId());

        Mockito.verify(classicPIAGameService, Mockito.times(1)).movePiece(
                game, null, piece, move.getSecond()
        );
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

    @Test
    void testEvaluationPerPositioning() {
        for (int i = 0; i < 8; i++) {
            PieceDto piece = boardDto.getBlackPieces().get(i + 8);
            piece.setPositionY(4);
        }
        Integer[][][] pieceMatrix = new Integer[8][8][1];
        for (PieceDto piece : boardDto.getWhitePieces()) {
            pieceMatrix[piece.getPositionX()][piece.getPositionY()] = new Integer[]{
                    piece.getId(), Constants.WHITE_COLOR, piece.getPieceType().ordinal()
            };
        }
        for (PieceDto piece : boardDto.getBlackPieces()) {
            pieceMatrix[piece.getPositionX()][piece.getPositionY()] = new Integer[]{
                    piece.getId(), Constants.BLACK_COLOR, piece.getPieceType().ordinal()
            };
        }
        boardDto.setPieceMatrix(pieceMatrix);
    }

    @Test
    void testExtraPointsForCheckMateShouldReturnCorrectValueWhenValidParametersGiven() {

        Mockito.when(
                classicPIAGameService.getGameStatus(Mockito.any(GameDto.class))
        ).thenReturn(GameStatus.NORMAL);
        int extraPoints = gameIa.extraPointsForCheck(gameDto, Constants.WHITE_COLOR);

        int expectedExtraPoints = 0;
        Assertions.assertEquals(expectedExtraPoints, extraPoints);

        extraPoints = gameIa.extraPointsForCheck(gameDto, Constants.BLACK_COLOR);
        Assertions.assertEquals(expectedExtraPoints, extraPoints);

        Mockito.when(
                classicPIAGameService.getGameStatus(Mockito.any(GameDto.class))
        ).thenReturn(GameStatus.WHITE_WINS);

        int extraPointsWithWins = gameIa.extraPointsForCheck(gameDto, Constants.WHITE_COLOR);
        expectedExtraPoints = 5000;
        Assertions.assertEquals(expectedExtraPoints, extraPointsWithWins);

        Mockito.when(
                classicPIAGameService.getGameStatus(Mockito.any(GameDto.class))
        ).thenReturn(GameStatus.BLACK_WINS);

        extraPointsWithWins = gameIa.extraPointsForCheck(gameDto, Constants.BLACK_COLOR);
        Assertions.assertEquals(expectedExtraPoints, extraPointsWithWins);

        Mockito.when(
                classicPIAGameService.getGameStatus(Mockito.any(GameDto.class))
        ).thenReturn(GameStatus.WHITE_CHECK);

        int extraPointsWithCheck = gameIa.extraPointsForCheck(gameDto, Constants.WHITE_COLOR);
        expectedExtraPoints = 200;
        Assertions.assertEquals(expectedExtraPoints, extraPointsWithCheck);

        Mockito.when(
                classicPIAGameService.getGameStatus(Mockito.any(GameDto.class))
        ).thenReturn(GameStatus.BLACK_CHECK);

        extraPointsWithCheck = gameIa.extraPointsForCheck(gameDto, Constants.BLACK_COLOR);

        Assertions.assertEquals(expectedExtraPoints, extraPointsWithCheck);
    }

    @Nested
    class TestMiniMax {

        @Mock
        private BoardService boardService;
        @Mock
        private HistoryRepository historyRepository;
        @Mock
        private BoardRepository boardRepository;
        @Mock
        private PlayerService playerService;

        private GameIaImpl gameIaMove;

        @BeforeEach
        void init() {
            ClassicPIAGameServiceImpl classicImpl = new ClassicPIAGameServiceImpl(
                    boardService,
                    gameRepository,
                    historyRepository,
                    pieceRepository,
                    boardRepository,
                    playerService
            );
            gameIaMove = new GameIaImpl(gameRepository, pieceRepository, classicImpl);
        }


        @Test
        void testKingMovesIfInCheck() {
            PieceDto fiveWhitePawn = boardDto.getWhitePieces().get(12);
            fiveWhitePawn.setPositionX(4);

            PieceDto secondBlackHorse = boardDto.getBlackPieces().get(6);
            secondBlackHorse.setPositionX(2);
            secondBlackHorse.setPositionY(7);

            PieceDto secondWhiteBishop = boardDto.getWhitePieces().get(5);
            secondWhiteBishop.setPositionX(6);
            secondWhiteBishop.setPositionY(4);

            PieceDto sixBlackPawn = boardDto.getBlackPieces().get(13);
            sixBlackPawn.setPositionX(3);

            secondWhiteBishop.setPositionX(3);
            secondWhiteBishop.setPositionY(7);

            Integer[][][] pieceMatrix = new Integer[8][8][1];
            for (PieceDto piece : boardDto.getWhitePieces()) {
                pieceMatrix[piece.getPositionX()][piece.getPositionY()] = new Integer[] {
                        piece.getId(), Constants.WHITE_COLOR, piece.getPieceType().ordinal()
                };
            }
            for (PieceDto piece : boardDto.getBlackPieces()) {
                pieceMatrix[piece.getPositionX()][piece.getPositionY()] = new Integer[] {
                        piece.getId(), Constants.BLACK_COLOR, piece.getPieceType().ordinal()
                };
            }

            gameDto.setWhiteTurn(false);
            gameDto.setGameDifficulty(GameDifficulty.EASY);
            boardDto.setPieceMatrix(pieceMatrix);

            Pair<PieceDto, MoveDto> move = gameIaMove.miniMax(
                    gameDto, Constants.BLACK_COLOR, Constants.WHITE_COLOR, 1
            );

            Assertions.assertNotNull(move);
        }
    }
}
