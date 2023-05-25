package br.com.eterniaserver.xadrez.domain.ia;

import br.com.eterniaserver.xadrez.Constants;
import br.com.eterniaserver.xadrez.domain.entities.Board;
import br.com.eterniaserver.xadrez.domain.entities.Game;
import br.com.eterniaserver.xadrez.domain.enums.GameDifficulty;
import br.com.eterniaserver.xadrez.domain.enums.GameType;
import br.com.eterniaserver.xadrez.domain.enums.MoveType;
import br.com.eterniaserver.xadrez.domain.enums.PieceType;
import br.com.eterniaserver.xadrez.domain.ia.impl.GameIaImpl;
import br.com.eterniaserver.xadrez.domain.repositories.GameRepository;
import br.com.eterniaserver.xadrez.domain.repositories.PieceRepository;
import br.com.eterniaserver.xadrez.domain.service.GameService;
import br.com.eterniaserver.xadrez.rest.dtos.MoveDto;
import br.com.eterniaserver.xadrez.rest.dtos.PieceDto;
import br.com.eterniaserver.xadrez.rest.dtos.PositionDto;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.data.util.Pair;
import org.mockito.junit.jupiter.MockitoExtension;
import br.com.eterniaserver.xadrez.domain.entities.Piece;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@ExtendWith(MockitoExtension.class)
public class GameIaImplUnitTest {

    private GameIaImpl gameIa;
    @Mock
    private GameRepository gameRepository;
    @Mock
    private PieceRepository pieceRepository;
    @Mock
    private GameService gameService;
    private Board board;
    private Game game;
    private UUID uuidPlayer;


    @BeforeEach
    void init() {
        gameIa = new GameIaImpl(gameRepository, pieceRepository);
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


//    @Test
//    void testCountPiecesByType() {
//        // Create a mock board with some pieces
//        Board board = Mockito.mock(Board.class);
//        List<Piece> pieces = List.of(
//                new Piece(1, board, null, PieceType.TOWER, 7, 0),
//                new Piece(2, board, null, PieceType.TOWER, 7, 1),
//                new Piece(3, board, null, PieceType.BISHOP, 7, 2),
//                new Piece(4, board, null, PieceType.QUEEN, 7, 3),
//                new Piece(5, board, null, PieceType.KING, 7, 4),
//                new Piece(6, board, null, PieceType.BISHOP, 7, 5),
//                new Piece(7, board, null, PieceType.HORSE, 7, 6),
//                new Piece(8, board, null, PieceType.TOWER, 7, 7)
//        );
//        Mockito.when(board.getWhitePieces()).thenReturn(pieces);
//
//        // Create a new GameIaImpl instance and call the countPiecesByType() method
//        GameIaImpl gameIa = new GameIaImpl(null, null);
//        int count = gameIa.countPiecesByType(board.getBoardDto(), Constants.BLACK_COLOR, PieceType.TOWER);
//
//        // Verify that the count is correct
//        Assertions.assertEquals(3, count);
//    }

    @Test
    void movePiece_ShouldMakeMove_WhenGameExistsAndPiecesAvailable() {
        // Arrange
        game.setGameDifficulty(GameDifficulty.EASY);

        PieceDto pieceDto = new PieceDto();
        pieceDto.setId(1);
        PositionDto positionDto = new PositionDto();
        positionDto.setSecond(2);
        positionDto.setFirst(2);
        pieceDto.setPositionY(1);
        pieceDto.setPositionX(1);
        pieceDto.setWhitePiece(false);
        MoveDto moveDto = new MoveDto();
        moveDto.setFirst(MoveType.NORMAL);
        moveDto.setSecond(positionDto);

        Mockito.when(gameRepository.findById(game.getId())).thenReturn(Optional.of(game));
        Mockito.when(gameService.getPlayerLegalMoves(game.getGameDto(), Constants.BLACK_COLOR))
                .thenReturn(Map.of(pieceDto, List.of(moveDto)));
        Mockito.when(pieceRepository.findById(pieceDto.getId())).thenReturn(Optional.of(new Piece()));

        // Act
        gameIa.movePiece(game.getId(), uuidPlayer, gameService);

        // Assert
        Mockito.verify(gameRepository, Mockito.times(1)).findById(game.getId());
        Mockito.verify(gameService, Mockito.times(1)).getPlayerLegalMoves(game.getGameDto(), Constants.BLACK_COLOR);
        Mockito.verify(pieceRepository, Mockito.times(1)).findById(pieceDto.getId());
        Mockito.verify(gameService, Mockito.times(1)).movePiece(Mockito.eq(game), Mockito.eq(null), Mockito.any(Piece.class), Mockito.eq(moveDto));
    }

//    @Test
//    void testNotCaptureAllies() {
//
//    }
//
//    @Test
//    void testCapturePriority() {
//
//    }

    @Test
    void miniMax_ShouldReturnBestMove_WhenValidParametersGiven() {
        // Arrange
        GameService gameService = Mockito.mock(GameService.class);
        int opponentColor = Constants.BLACK_COLOR;
        int currentColor = Constants.WHITE_COLOR;
        int depth = 2;


        PieceDto pieceDto = new PieceDto();
        pieceDto.setId(1);
        pieceDto.setWhitePiece(false);
        MoveDto bestMoveDto = new MoveDto();
        MoveDto moveDto = new MoveDto();
        PositionDto positionDto = new PositionDto();
        PositionDto positionDto2 = new PositionDto();
        positionDto.setSecond(5);
        positionDto.setFirst(5);
        positionDto2.setSecond(2);
        positionDto2.setFirst(2);
        bestMoveDto.setFirst(MoveType.NORMAL);
        bestMoveDto.setSecond(positionDto2);
        moveDto.setFirst(MoveType.NORMAL);
        moveDto.setSecond(positionDto);
        MoveDto otherMoveDto = new MoveDto();
        PositionDto positionDto3 = new PositionDto();
        positionDto3.setSecond(3);
        positionDto3.setFirst(3);
        otherMoveDto.setFirst(MoveType.NORMAL);
        otherMoveDto.setSecond(positionDto3);

        List<MoveDto> moveDtos = List.of(moveDto, bestMoveDto, otherMoveDto);
        Map<PieceDto, List<MoveDto>> legalMovesMap = new HashMap<>();
        legalMovesMap.put(pieceDto, moveDtos);

         Mockito.when(gameService.getPlayerLegalMoves(game.getGameDto(), opponentColor)).thenReturn(legalMovesMap);
        Pair<PieceDto, MoveDto> move = gameIa.miniMax(gameService, board.getBoardDto(), game.getGameDto(), currentColor, opponentColor, depth - 1);
        moveDto = move.getSecond();
        Mockito.when(pieceRepository.findById(pieceDto.getId())).thenReturn(Optional.of(new Piece()));
        gameIa.movePieceOnBoard(board.getBoardDto(), pieceDto, moveDto);

       // Act

        // Assert
        Assertions.assertEquals(Pair.of(pieceDto,bestMoveDto), move);
        Mockito.verify(gameService, Mockito.times(1)).getPlayerLegalMoves(game.getGameDto(), opponentColor);
        Mockito.verify(pieceRepository, Mockito.times(1)).findById(pieceDto.getId());
        Mockito.verify(gameIa, Mockito.times(1)).movePieceOnBoard(Mockito.any(), Mockito.any(), Mockito.any());
        Mockito.verify(gameIa, Mockito.times(1)).evalutionFunction(gameService, board.getBoardDto(), opponentColor);

    }

//    @Test
//    void testIfCheckMate() {
//
//    }
//
//    @Test
//    void testIfCheck() {
//
//    }

}
