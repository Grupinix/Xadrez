package br.com.eterniaserver.xadrez.domain.service;

import br.com.eterniaserver.xadrez.domain.entities.Board;
import br.com.eterniaserver.xadrez.domain.entities.Game;
import br.com.eterniaserver.xadrez.domain.entities.Piece;
import br.com.eterniaserver.xadrez.domain.enums.GameDifficulty;
import br.com.eterniaserver.xadrez.domain.enums.GameType;
import br.com.eterniaserver.xadrez.domain.enums.MoveType;
import br.com.eterniaserver.xadrez.domain.ia.impl.GameIaImpl;
import br.com.eterniaserver.xadrez.domain.repositories.BoardRepository;
import br.com.eterniaserver.xadrez.domain.repositories.GameRepository;
import br.com.eterniaserver.xadrez.domain.repositories.HistoryRepository;
import br.com.eterniaserver.xadrez.domain.repositories.PieceRepository;
import br.com.eterniaserver.xadrez.domain.service.impl.BoardServiceImpl;
import br.com.eterniaserver.xadrez.domain.service.impl.ClassicPIAGameServiceImpl;
import br.com.eterniaserver.xadrez.rest.dtos.MoveDto;
import br.com.eterniaserver.xadrez.rest.dtos.PositionDto;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@ExtendWith(MockitoExtension.class)
class ClassicPIAGameServiceImplUnitTest {

    @Mock
    private BoardService boardService;
    @Mock
    private GameRepository gameRepository;
    @Mock
    private BoardRepository boardRepository;
    @Mock
    private PieceRepository pieceRepository;
    @Mock
    private HistoryRepository historyRepository;
    @Mock
    private GameIaImpl gameIa;
    @Mock
    private PlayerService playerService;

    private GameService gameService;

    @BeforeEach
    void init() {
        gameService = new ClassicPIAGameServiceImpl(
                boardService,
                gameRepository,
                historyRepository,
                pieceRepository,
                boardRepository,
                playerService,
                gameIa
        );
    }

    @Test
    void testGetInvalidGame() {
        Integer gameId = 1;
        Assertions.assertThrows(ResponseStatusException.class, () ->  gameService.getGame(gameId));
    }

    @Test
    void ensureThatGetAllGamesReturnNull() {
        List<Game> games = gameService.getAllGames();
        Assertions.assertNull(games);
    }

    @Test
    void ensureThatGetGamesReturnNull() {
        List<Game> games = gameService.getGames();
        Assertions.assertNull(games);
    }

    @Test
    void ensureThatEnterGameReturnNull() {
        UUID uuid = UUID.randomUUID();
        Integer gameId = 1;
        Game game = gameService.enterGame(uuid, gameId);
        Assertions.assertNull(game);
    }

    @Test
    void testGetGame() {
        UUID uuid = UUID.randomUUID();
        Integer gameId = 1;

        Game game = gameService.createGame(uuid);
        game.setId(1);

        Mockito.when(gameRepository.findById(gameId)).thenReturn(Optional.of(game));

        Game getGame = gameService.getGame(gameId);

        Assertions.assertEquals(game, getGame);
    }

    @Test
    void testCreateGame() {
        UUID uuid = UUID.randomUUID();

        Mockito.when(playerService.getGameDifficulty(uuid)).thenReturn(GameDifficulty.NORMAL);

        Game game = gameService.createGame(uuid);

        Assertions.assertEquals(GameType.PLAYER_IA_CLASSIC, game.getGameType());
        Assertions.assertEquals(GameDifficulty.NORMAL, game.getGameDifficulty());
        Assertions.assertTrue(game.getWhiteTurn());
        Assertions.assertEquals(uuid, game.getWhitePlayerUUID());
        Assertions.assertNull(game.getBlackPlayerUUID());
        Assertions.assertEquals(0, game.getWhiteMoves());
        Assertions.assertEquals(0, game.getBlackMoves());
    }

    @Test
    void testRefreshGameTimer() {
        Integer gameId = 1;
        Game game = Mockito.mock(Game.class);

        Mockito.when(gameRepository.findById(gameId)).thenReturn(Optional.of(game));

        gameService.refreshGameTimer(gameId);

        Mockito.verify(game, Mockito.times(1)).setTimer(Mockito.anyLong());
    }

    @Test
    void testRefreshGameTimerWithInvalidGame() {
        Integer gameId = 1;
        Mockito.when(gameRepository.findById(gameId)).thenReturn(Optional.empty());

        gameService.refreshGameTimer(gameId);
    }

    @Test
    void testCheckGameShouldReturnTrue() {
        Integer gameId = 1;

        Mockito.when(gameRepository.existsById(gameId)).thenReturn(true);

        boolean result = gameService.checkGame(gameId);

        Assertions.assertTrue(result);
    }

    @Test
    void testCheckGameShouldReturnFalse() {
        Integer gameId = 1;

        Mockito.when(gameRepository.existsById(gameId)).thenReturn(false);

        boolean result = gameService.checkGame(gameId);

        Assertions.assertFalse(result);
    }

    @Nested
    class TestMoves {

        private Game game;
        private UUID white;
        private UUID black;


        @BeforeEach
        void init() {
            BoardServiceImpl boardService = new BoardServiceImpl(boardRepository, pieceRepository);
            Board board = boardService.createBoard();

            int idIncrement = 0;
            for (Piece piece : board.getWhitePieces()) {
                piece.setId(idIncrement++);
            }
            for (Piece piece : board.getBlackPieces()) {
                piece.setId(idIncrement++);
            }

            white = UUID.randomUUID();
            black = UUID.randomUUID();

            game = gameService.createGame(white);
            game.setBoard(board);
        }

        @Test
        void testPawnPossibleInitialMoves() {
            Piece pawn = game.getBoard().getWhitePieces().get(8);

            int positionX = pawn.getPositionX();
            int positionY = pawn.getPositionY();

            List<MoveDto> moves = gameService.getPossibleMoves(game.getGameDto(), pawn.getPieceDto(), white);

            Assertions.assertEquals(2, moves.size());
            Assertions.assertEquals(MoveType.NORMAL, moves.get(0).getFirst());
            Assertions.assertEquals(MoveType.NORMAL, moves.get(1).getFirst());
            Assertions.assertEquals(positionX -1, moves.get(0).getSecond().getFirst());
            Assertions.assertEquals(positionY, moves.get(0).getSecond().getSecond());
            Assertions.assertEquals(positionX -2, moves.get(1).getSecond().getFirst());
            Assertions.assertEquals(positionY, moves.get(1).getSecond().getSecond());
        }

        @Test
        void testWhitePawnCaptureMove() {
            Piece pawn = game.getBoard().getWhitePieces().get(8);
            pawn.setPositionX(pawn.getPositionX() - 2);

            Piece pawn2 = game.getBoard().getBlackPieces().get(9);
            pawn2.setPositionX(pawn2.getPositionX() + 2);

            int expectedX = pawn2.getPositionX();
            int expectedY = pawn2.getPositionY();

            List<MoveDto> moves = gameService.getPossibleMoves(game.getGameDto(), pawn.getPieceDto(), white);

            Assertions.assertEquals(2, moves.size());
            Assertions.assertEquals(MoveType.NORMAL, moves.get(0).getFirst());
            Assertions.assertEquals(MoveType.CAPTURE, moves.get(1).getFirst());
            Assertions.assertEquals(expectedX, moves.get(1).getSecond().getFirst());
            Assertions.assertEquals(expectedY, moves.get(1).getSecond().getSecond());
        }

        @Test
        void testBlackPawnCaptureMove() {
            Piece pawn = game.getBoard().getWhitePieces().get(8);
            pawn.setPositionX(pawn.getPositionX() - 2);

            Piece pawn2 = game.getBoard().getBlackPieces().get(9);
            pawn2.setPositionX(pawn2.getPositionX() + 2);

            int expectedX = pawn.getPositionX();
            int expectedY = pawn.getPositionY();

            List<MoveDto> moves = gameService.getPossibleMoves(game.getGameDto(), pawn2.getPieceDto(), black);

            Assertions.assertEquals(2, moves.size());
            Assertions.assertEquals(MoveType.NORMAL, moves.get(0).getFirst());
            Assertions.assertEquals(MoveType.CAPTURE, moves.get(1).getFirst());
            Assertions.assertEquals(expectedX, moves.get(1).getSecond().getFirst());
            Assertions.assertEquals(expectedY, moves.get(1).getSecond().getSecond());
        }

        @Test
        void testWhiteQueenStartMoves() {
            Piece queen = game.getBoard().getWhitePieces().get(3);

            List<MoveDto> moves = gameService.getPossibleMoves(game.getGameDto(), queen.getPieceDto(), white);

            Assertions.assertEquals(0, moves.size());
        }

        @Test
        void testWhiteQueenMovesWithoutFrontPawn() {
            game.getBoard().getWhitePieces().remove(11);
            Piece queen = game.getBoard().getWhitePieces().get(3);

            List<MoveDto> moves = gameService.getPossibleMoves(game.getGameDto(), queen.getPieceDto(), white);

            Assertions.assertEquals(6, moves.size());
            Assertions.assertEquals(MoveType.NORMAL, moves.get(0).getFirst());
            Assertions.assertEquals(MoveType.NORMAL, moves.get(1).getFirst());
            Assertions.assertEquals(MoveType.NORMAL, moves.get(2).getFirst());
            Assertions.assertEquals(MoveType.NORMAL, moves.get(3).getFirst());
            Assertions.assertEquals(MoveType.NORMAL, moves.get(4).getFirst());
            Assertions.assertEquals(MoveType.CAPTURE, moves.get(5).getFirst());
        }

        @Test
        void testWhiteQueenMovesInD2() {
            game.getBoard().getWhitePieces().remove(11);
            Piece queen = game.getBoard().getWhitePieces().get(3);
            queen.setPositionX(6);

            List<MoveDto> moves = gameService.getPossibleMoves(game.getGameDto(), queen.getPieceDto(), white);

            Assertions.assertEquals(13, moves.size());
            Assertions.assertEquals(MoveType.NORMAL, moves.get(0).getFirst());
            Assertions.assertEquals(MoveType.NORMAL, moves.get(1).getFirst());
            Assertions.assertEquals(MoveType.NORMAL, moves.get(2).getFirst());
            Assertions.assertEquals(MoveType.NORMAL, moves.get(3).getFirst());
            Assertions.assertEquals(MoveType.NORMAL, moves.get(4).getFirst());
            Assertions.assertEquals(MoveType.CAPTURE, moves.get(5).getFirst());
            Assertions.assertEquals(MoveType.NORMAL, moves.get(6).getFirst());
            Assertions.assertEquals(MoveType.NORMAL, moves.get(7).getFirst());
            Assertions.assertEquals(MoveType.NORMAL, moves.get(8).getFirst());
            Assertions.assertEquals(MoveType.NORMAL, moves.get(9).getFirst());
            Assertions.assertEquals(MoveType.NORMAL, moves.get(10).getFirst());
            Assertions.assertEquals(MoveType.NORMAL, moves.get(11).getFirst());
            Assertions.assertEquals(MoveType.NORMAL, moves.get(12).getFirst());
        }

        @Test
        void testWhiteBishopStartMove() {
            Piece bishop = game.getBoard().getWhitePieces().get(2);

            List<MoveDto> moves = gameService.getPossibleMoves(game.getGameDto(), bishop.getPieceDto(), white);

            Assertions.assertEquals(0, moves.size());
        }

        @Test
        void testWhiteHorseStartMove() {
            Piece horse = game.getBoard().getWhitePieces().get(1);

            List<MoveDto> moves = gameService.getPossibleMoves(game.getGameDto(), horse.getPieceDto(), white);

            Assertions.assertEquals(2, moves.size());
        }

        @Test
        void testWhiteTowerStartMove() {
            Piece tower = game.getBoard().getWhitePieces().get(0);

            List<MoveDto> moves = gameService.getPossibleMoves(game.getGameDto(), tower.getPieceDto(), white);

            Assertions.assertEquals(0, moves.size());
        }

        @Test
        void testWhiteKingStartMove() {
            Piece king = game.getBoard().getWhitePieces().get(4);

            List<MoveDto> moves = gameService.getPossibleMoves(game.getGameDto(), king.getPieceDto(), white);

            Assertions.assertEquals(0, moves.size());
        }

        @Test
        void testMoveWhiteInBlackTurn() {
            game.setWhiteTurn(false);
            Piece pawn = game.getBoard().getWhitePieces().get(8);
            int positionX = pawn.getPositionX();
            int positionY = pawn.getPositionY();

            PositionDto positionDto = PositionDto.builder()
                                                 .first(positionX - 2)
                                                 .second(positionY)
                                                 .build();
            MoveDto moveDto = MoveDto.builder()
                                     .first(MoveType.NORMAL)
                                     .second(positionDto)
                                     .build();

            Assertions.assertThrows(
                    ResponseStatusException.class, () ->  gameService.movePiece(game, white, pawn, moveDto)
            );
        }


        @Test
        void testMoveWhiteToPosition() {
            Piece pawn = game.getBoard().getWhitePieces().get(8);
            game.setId(0);
            int positionX = pawn.getPositionX();
            int positionY = pawn.getPositionY();

            PositionDto positionDto = PositionDto.builder()
                                                 .first(positionX - 2)
                                                 .second(positionY)
                                                 .build();
            MoveDto moveDto = MoveDto.builder()
                                     .first(MoveType.NORMAL)
                                     .second(positionDto)
                                     .build();

            gameService.movePiece(game, white, pawn, moveDto);

            Assertions.assertEquals(1, game.getBoard().getHistories().size());
            Assertions.assertEquals(positionX - 2, pawn.getPositionX());
            Assertions.assertEquals(positionY, pawn.getPositionY());
        }

        @Test
        void testMoveIaToPosition() {
            Piece pawn = game.getBoard().getBlackPieces().get(8);
            game.setWhiteTurn(false);
            game.setId(0);
            int positionX = pawn.getPositionX();
            int positionY = pawn.getPositionY();

            PositionDto positionDto = PositionDto.builder()
                    .first(positionX + 2)
                    .second(positionY)
                    .build();
            MoveDto moveDto = MoveDto.builder()
                    .first(MoveType.NORMAL)
                    .second(positionDto)
                    .build();

            gameService.movePiece(game, null, pawn, moveDto);

            Assertions.assertEquals(1, game.getBoard().getHistories().size());
            Assertions.assertEquals(positionX + 2, pawn.getPositionX());
            Assertions.assertEquals(positionY, pawn.getPositionY());
        }

        @Test
        void testMoveWhiteToSameTeamPieceShouldRaiseError() {
            Piece pawn = game.getBoard().getWhitePieces().get(8);
            int positionX = pawn.getPositionX();
            int positionY = pawn.getPositionY();

            PositionDto positionDto = PositionDto.builder()
                                                 .first(positionX)
                                                 .second(positionY + 1)
                                                 .build();
            MoveDto moveDto = MoveDto.builder()
                                     .first(MoveType.CAPTURE)
                                     .second(positionDto)
                                     .build();

            Assertions.assertThrows(
                    ResponseStatusException.class, () ->  gameService.movePiece(game, white, pawn, moveDto)
            );
        }

        @Test
        void testMoveWhiteToCaptureVoidShouldRaiseError() {
            Piece pawn = game.getBoard().getWhitePieces().get(8);
            int positionX = pawn.getPositionX();
            int positionY = pawn.getPositionY();

            PositionDto positionDto = PositionDto.builder()
                                                 .first(positionX -2)
                                                 .second(positionY)
                                                 .build();
            MoveDto moveDto = MoveDto.builder()
                                     .first(MoveType.CAPTURE)
                                     .second(positionDto)
                                     .build();

            Assertions.assertThrows(
                    ResponseStatusException.class, () ->  gameService.movePiece(game, white, pawn, moveDto)
            );
        }

        @Test
        void testMoveWhiteToCaptureAnotherPawn() {
            game.setId(0);
            Piece pawn = game.getBoard().getWhitePieces().get(8);
            pawn.setPositionX(2);

            int positionX = pawn.getPositionX();
            int positionY = pawn.getPositionY();

            PositionDto positionDto = PositionDto.builder()
                                                 .first(positionX - 1)
                                                 .second(positionY + 1)
                                                 .build();
            MoveDto moveDto = MoveDto.builder()
                                     .first(MoveType.CAPTURE)
                                     .second(positionDto)
                                     .build();

            gameService.movePiece(game, white, pawn, moveDto);

            Assertions.assertEquals(1, game.getBoard().getHistories().size());
            Assertions.assertEquals(15, game.getBoard().getBlackPieces().size());
            Assertions.assertEquals(positionX - 1, pawn.getPositionX());
            Assertions.assertEquals(positionY + 1, pawn.getPositionY());
        }

        @Test
        void testMoveBlackToPosition() {
            game.setWhiteTurn(false);
            game.setId(0);
            Piece pawn = game.getBoard().getBlackPieces().get(8);
            int positionX = pawn.getPositionX();
            int positionY = pawn.getPositionY();

            PositionDto positionDto = PositionDto.builder()
                                                 .first(positionX + 2)
                                                 .second(positionY)
                                                 .build();
            MoveDto moveDto = MoveDto.builder()
                                     .first(MoveType.NORMAL)
                                     .second(positionDto)
                                     .build();

            gameService.movePiece(game, black, pawn, moveDto);

            Assertions.assertEquals(1, game.getBoard().getHistories().size());
            Assertions.assertEquals(positionX + 2, pawn.getPositionX());
            Assertions.assertEquals(positionY, pawn.getPositionY());
        }

        @Test
        void testMoveBlackToSameTeamPieceShouldRaiseError() {
            game.setWhiteTurn(false);
            Piece pawn = game.getBoard().getBlackPieces().get(8);
            int positionX = pawn.getPositionX();
            int positionY = pawn.getPositionY();

            PositionDto positionDto = PositionDto.builder()
                                                 .first(positionX)
                                                 .second(positionY + 1)
                                                 .build();
            MoveDto moveDto = MoveDto.builder()
                                     .first(MoveType.CAPTURE)
                                     .second(positionDto)
                                     .build();

            Assertions.assertThrows(
                    ResponseStatusException.class, () ->  gameService.movePiece(game, black, pawn, moveDto)
            );
        }

        @Test
        void testMoveBlackToCaptureVoidShouldRaiseError() {
            game.setWhiteTurn(false);
            Piece pawn = game.getBoard().getBlackPieces().get(8);
            int positionX = pawn.getPositionX();
            int positionY = pawn.getPositionY();

            PositionDto positionDto = PositionDto.builder()
                                                 .first(positionX + 2)
                                                 .second(positionY)
                                                 .build();
            MoveDto moveDto = MoveDto.builder()
                                     .first(MoveType.CAPTURE)
                                     .second(positionDto)
                                     .build();

            Assertions.assertThrows(
                    ResponseStatusException.class, () ->  gameService.movePiece(game, black, pawn, moveDto)
            );
        }

        @Test
        void testMoveBlackToCaptureAnotherPawn() {
            game.setWhiteTurn(false);
            game.setId(0);
            Piece pawn = game.getBoard().getBlackPieces().get(8);
            pawn.setPositionX(5);

            int positionX = pawn.getPositionX();
            int positionY = pawn.getPositionY();

            PositionDto positionDto = PositionDto.builder()
                                                 .first(positionX + 1)
                                                 .second(positionY + 1)
                                                 .build();
            MoveDto moveDto = MoveDto.builder()
                                     .first(MoveType.CAPTURE)
                                     .second(positionDto)
                                     .build();

            gameService.movePiece(game, black, pawn, moveDto);

            Assertions.assertEquals(1, game.getBoard().getHistories().size());
            Assertions.assertEquals(15, game.getBoard().getWhitePieces().size());
            Assertions.assertEquals(positionX + 1, pawn.getPositionX());
            Assertions.assertEquals(positionY + 1, pawn.getPositionY());
        }
    }

}
