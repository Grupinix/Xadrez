package br.com.eterniaserver.xadrez.domain.service;

import br.com.eterniaserver.xadrez.Constants;
import br.com.eterniaserver.xadrez.domain.entities.Game;
import br.com.eterniaserver.xadrez.domain.entities.Piece;
import br.com.eterniaserver.xadrez.domain.enums.GameStatus;
import br.com.eterniaserver.xadrez.domain.enums.MoveType;
import br.com.eterniaserver.xadrez.domain.enums.PieceType;
import br.com.eterniaserver.xadrez.rest.dtos.BoardDto;
import br.com.eterniaserver.xadrez.rest.dtos.GameDto;
import br.com.eterniaserver.xadrez.rest.dtos.MoveDto;
import br.com.eterniaserver.xadrez.rest.dtos.PieceDto;
import br.com.eterniaserver.xadrez.rest.dtos.PositionDto;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;

@ExtendWith(MockitoExtension.class)
class GameServiceUnitTest {

    private GameService gameService;
    private final UUID whitePlayerUUID = UUID.randomUUID();
    private final UUID blackPlayerUUID = UUID.randomUUID();
    @Mock
    private List<MoveDto> whiteMovesMock;
    @Mock
    private List<MoveDto> blackMovesMock;

    @BeforeEach
    void init() {
        gameService = new GameService() {
            @Override
            public List<Game> getAllGames() {
                return null;
            }

            @Override
            public Game getGame(Integer gameId) throws ResponseStatusException {
                return null;
            }

            @Override
            public List<Game> getGames() {
                return null;
            }

            @Override
            public Game createGame(UUID whiteUUID) {
                return null;
            }

            @Override
            public boolean checkGame(Integer gameId) {
                return false;
            }

            @Override
            public void refreshGameTimer(Integer gameId) {
            }

            @Override
            public Game enterGame(UUID blackUUID, Integer gameId) {
                return null;
            }

            @Override
            public List<MoveDto> getPossibleMoves(GameDto game, PieceDto piece, UUID playerUUID) {
                if (whitePlayerUUID.equals(playerUUID)) {
                    return whiteMovesMock.stream().toList();
                }
                return blackMovesMock.stream().toList();
            }

            @Override
            public Game movePiece(Game game,
                                  UUID playerUUID,
                                  Piece piece,
                                  MoveDto moveTypePairPair) throws ResponseStatusException {
                return null;
            }
        };
    }

    @Test
    void getOptionalPieceDto() {
        PieceDto pieceDto = PieceDto.builder().pieceType(PieceType.PAWN).build();
        List<PieceDto> pieceDtoList = List.of(pieceDto);

        Optional<PieceDto> pieceDtoOptional = gameService.getPieceOptional(pieceDtoList, PieceType.PAWN);

        Assertions.assertTrue(pieceDtoOptional.isPresent());
        Assertions.assertEquals(pieceDto, pieceDtoOptional.get());
    }

    @Test
    void getOptionalPieceDtoEmpty() {
        PieceDto pieceDto = PieceDto.builder().pieceType(PieceType.PAWN).build();
        List<PieceDto> pieceDtoList = List.of(pieceDto);

        Optional<PieceDto> pieceDtoOptional = gameService.getPieceOptional(pieceDtoList, PieceType.KING);

        Assertions.assertTrue(pieceDtoOptional.isEmpty());
    }

    @Test
    void testWhiteWinsBecauseNoBlackKing() {
        PieceDto whiteKing = PieceDto.builder()
                .pieceType(PieceType.KING)
                .whitePiece(true)
                .build();
        BoardDto boardDto = BoardDto.builder()
                .whitePieces(List.of(whiteKing))
                .blackPieces(List.of())
                .histories(List.of())
                .pieceMatrix(new Integer[8][8][3])
                .build();
        GameDto gameDto = GameDto.builder()
                .board(boardDto)
                .build();

        GameStatus expectStatus = GameStatus.WHITE_WINS;
        GameStatus actualStatus = gameService.getGameStatus(gameDto, true);

        Assertions.assertEquals(expectStatus, actualStatus);
    }

    @Test
    void testBlackWinsBecauseNoWhiteKing() {
        PieceDto blackKing = PieceDto.builder()
                .pieceType(PieceType.KING)
                .whitePiece(false)
                .build();
        BoardDto boardDto = BoardDto.builder()
                .whitePieces(List.of())
                .blackPieces(List.of(blackKing))
                .histories(List.of())
                .pieceMatrix(new Integer[8][8][3])
                .build();
        GameDto gameDto = GameDto.builder()
                .board(boardDto)
                .build();

        GameStatus expectStatus = GameStatus.BLACK_WINS;
        GameStatus actualStatus = gameService.getGameStatus(gameDto, false);

        Assertions.assertEquals(expectStatus, actualStatus);
    }

    @Test
    void testWhiteCheck() {
        PieceDto whiteKing = PieceDto.builder()
                .pieceType(PieceType.KING)
                .positionX(6)
                .positionY(6)
                .whitePiece(true)
                .build();
        PieceDto whitePawn = PieceDto.builder()
                .pieceType(PieceType.PAWN)
                .positionX(3)
                .positionY(4)
                .whitePiece(true)
                .build();
        PieceDto blackKing = PieceDto.builder()
                .pieceType(PieceType.KING)
                .positionX(2)
                .positionY(5)
                .whitePiece(false)
                .build();
        BoardDto boardDto = BoardDto.builder()
                .whitePieces(List.of(whiteKing, whitePawn))
                .blackPieces(List.of(blackKing))
                .histories(List.of())
                .pieceMatrix(new Integer[8][8][3])
                .build();
        GameDto gameDto = GameDto.builder()
                .board(boardDto)
                .whiteTurn(true)
                .whitePlayerUUID(whitePlayerUUID)
                .blackPlayerUUID(blackPlayerUUID)
                .build();

        MoveDto pawnCaptureBlackKing = MoveDto.builder()
                .first(MoveType.CAPTURE)
                .second(PositionDto.builder()
                        .first(2)
                        .second(5)
                        .build()
                )
                .build();
        MoveDto pawnMove = MoveDto.builder()
                .first(MoveType.NORMAL)
                .second(PositionDto.builder()
                        .first(2)
                        .second(4)
                        .build()
                )
                .build();
        Mockito.when(whiteMovesMock.stream()).then(i -> Stream.of(pawnCaptureBlackKing, pawnMove));

        GameStatus expectStatus = GameStatus.WHITE_CHECK;
        GameStatus actualStatus = gameService.isCheck(gameDto, whiteKing, blackKing);

        Assertions.assertEquals(expectStatus, actualStatus);
    }

    @Test
    void testBlackCheck() {
        PieceDto whiteKing = PieceDto.builder()
                .pieceType(PieceType.KING)
                .positionX(6)
                .positionY(6)
                .whitePiece(true)
                .build();
        PieceDto blackPawn = PieceDto.builder()
                .pieceType(PieceType.PAWN)
                .positionX(5)
                .positionY(5)
                .whitePiece(false)
                .build();
        PieceDto blackKing = PieceDto.builder()
                .pieceType(PieceType.KING)
                .positionX(2)
                .positionY(5)
                .whitePiece(false)
                .build();
        BoardDto boardDto = BoardDto.builder()
                .whitePieces(List.of(whiteKing))
                .blackPieces(List.of(blackKing, blackPawn))
                .histories(List.of())
                .pieceMatrix(new Integer[8][8][3])
                .build();
        GameDto gameDto = GameDto.builder()
                .board(boardDto)
                .whitePlayerUUID(whitePlayerUUID)
                .blackPlayerUUID(blackPlayerUUID)
                .whiteTurn(true)
                .build();

        MoveDto pawnCaptureWhiteKing = MoveDto.builder()
                .first(MoveType.CAPTURE)
                .second(PositionDto.builder()
                        .first(6)
                        .second(6)
                        .build()
                )
                .build();
        MoveDto pawnMove = MoveDto.builder()
                .first(MoveType.CAPTURE)
                .second(PositionDto.builder()
                        .first(6)
                        .second(5)
                        .build()
                )
                .build();
        Mockito.when(blackMovesMock.stream()).then(i -> Stream.of(pawnCaptureWhiteKing, pawnMove));

        GameStatus expectStatus = GameStatus.BLACK_CHECK;
        GameStatus actualStatus = gameService.isCheck(gameDto, whiteKing, blackKing);

        Assertions.assertEquals(expectStatus, actualStatus);
    }

    @Test
    void verifyNoneIsInCheck() {
        PieceDto whiteKing = PieceDto.builder()
                .pieceType(PieceType.KING)
                .positionX(6)
                .positionY(6)
                .whitePiece(true)
                .build();
        PieceDto blackKing = PieceDto.builder()
                .pieceType(PieceType.KING)
                .positionX(2)
                .positionY(5)
                .whitePiece(false)
                .build();
        BoardDto boardDto = BoardDto.builder()
                .whitePieces(List.of(whiteKing))
                .blackPieces(List.of(blackKing))
                .histories(List.of())
                .pieceMatrix(new Integer[8][8][3])
                .build();
        GameDto gameDto = GameDto.builder()
                .board(boardDto)
                .whitePlayerUUID(whitePlayerUUID)
                .blackPlayerUUID(blackPlayerUUID)
                .whiteTurn(true)
                .build();

        GameStatus expectStatus = GameStatus.NORMAL;
        GameStatus actualStatus = gameService.isCheck(gameDto, whiteKing, blackKing);

        Assertions.assertEquals(expectStatus, actualStatus);
    }

    @Test
    void verifyIsWhiteMatte() {
        PieceDto whiteKing = PieceDto.builder()
                .pieceType(PieceType.KING)
                .positionX(6)
                .positionY(6)
                .whitePiece(true)
                .build();
        PieceDto whitePawn = PieceDto.builder()
                .pieceType(PieceType.PAWN)
                .positionX(3)
                .positionY(4)
                .whitePiece(true)
                .build();
        PieceDto blackKing = PieceDto.builder()
                .pieceType(PieceType.KING)
                .positionX(2)
                .positionY(5)
                .whitePiece(false)
                .build();
        BoardDto boardDto = BoardDto.builder()
                .whitePieces(List.of(whiteKing, whitePawn))
                .blackPieces(List.of(blackKing))
                .histories(List.of())
                .pieceMatrix(new Integer[8][8][3])
                .build();
        GameDto gameDto = GameDto.builder()
                .board(boardDto)
                .whiteTurn(true)
                .whitePlayerUUID(whitePlayerUUID)
                .blackPlayerUUID(blackPlayerUUID)
                .build();

        MoveDto pawnCaptureBlackKing = MoveDto.builder()
                .first(MoveType.CAPTURE)
                .second(PositionDto.builder()
                        .first(2)
                        .second(5)
                        .build()
                )
                .build();
        Mockito.when(whiteMovesMock.stream()).then(i -> Stream.of(pawnCaptureBlackKing));

        GameStatus expectedStatus = GameStatus.WHITE_WINS;
        GameStatus actualStatus = gameService.getGameStatus(gameDto, true);

        Assertions.assertEquals(expectedStatus, actualStatus);
    }

    @Test
    void verifyIsBlackMatte() {
        PieceDto whiteKing = PieceDto.builder()
                .pieceType(PieceType.KING)
                .positionX(6)
                .positionY(6)
                .whitePiece(true)
                .build();
        PieceDto blackPawn = PieceDto.builder()
                .pieceType(PieceType.PAWN)
                .positionX(5)
                .positionY(5)
                .whitePiece(false)
                .build();
        PieceDto blackKing = PieceDto.builder()
                .pieceType(PieceType.KING)
                .positionX(2)
                .positionY(5)
                .whitePiece(false)
                .build();
        BoardDto boardDto = BoardDto.builder()
                .whitePieces(List.of(whiteKing))
                .blackPieces(List.of(blackKing, blackPawn))
                .histories(List.of())
                .pieceMatrix(new Integer[8][8][3])
                .build();
        GameDto gameDto = GameDto.builder()
                .board(boardDto)
                .whitePlayerUUID(whitePlayerUUID)
                .blackPlayerUUID(blackPlayerUUID)
                .whiteTurn(true)
                .build();

        MoveDto pawnCaptureWhiteKing = MoveDto.builder()
                .first(MoveType.CAPTURE)
                .second(PositionDto.builder()
                        .first(6)
                        .second(6)
                        .build()
                )
                .build();
        Mockito.when(blackMovesMock.stream()).then(i -> Stream.of(pawnCaptureWhiteKing));

        GameStatus expectedStatus = GameStatus.BLACK_WINS;
        GameStatus actualStatus = gameService.getGameStatus(gameDto, false);

        Assertions.assertEquals(expectedStatus, actualStatus);
    }

    @Test
    void getGameStatusShouldReturnNormal() {
        PieceDto whiteKing = PieceDto.builder()
                .pieceType(PieceType.KING)
                .positionX(6)
                .positionY(6)
                .whitePiece(true)
                .build();
        PieceDto blackKing = PieceDto.builder()
                .pieceType(PieceType.KING)
                .positionX(2)
                .positionY(5)
                .whitePiece(false)
                .build();
        BoardDto boardDto = BoardDto.builder()
                .whitePieces(List.of(whiteKing))
                .blackPieces(List.of(blackKing))
                .histories(List.of())
                .pieceMatrix(new Integer[8][8][3])
                .build();
        GameDto gameDto = GameDto.builder()
                .board(boardDto)
                .whitePlayerUUID(whitePlayerUUID)
                .blackPlayerUUID(blackPlayerUUID)
                .whiteTurn(true)
                .build();

        GameStatus expectedStatus = GameStatus.NORMAL;
        GameStatus actualStatus = gameService.getGameStatus(gameDto, true);

        Assertions.assertEquals(expectedStatus, actualStatus);
    }

    @Test
    void getGameStatusIsWhiteCheck() {
        PieceDto whiteKing = PieceDto.builder()
                .pieceType(PieceType.KING)
                .positionX(6)
                .positionY(6)
                .whitePiece(true)
                .build();
        PieceDto whitePawn = PieceDto.builder()
                .pieceType(PieceType.PAWN)
                .positionX(3)
                .positionY(4)
                .whitePiece(true)
                .build();
        PieceDto blackKing = PieceDto.builder()
                .pieceType(PieceType.KING)
                .positionX(2)
                .positionY(5)
                .whitePiece(false)
                .build();
        BoardDto boardDto = BoardDto.builder()
                .whitePieces(List.of(whiteKing, whitePawn))
                .blackPieces(List.of(blackKing))
                .histories(List.of())
                .pieceMatrix(new Integer[8][8][3])
                .build();
        GameDto gameDto = GameDto.builder()
                .board(boardDto)
                .whiteTurn(true)
                .whitePlayerUUID(whitePlayerUUID)
                .blackPlayerUUID(blackPlayerUUID)
                .build();

        MoveDto pawnCaptureBlackKing = MoveDto.builder()
                .first(MoveType.CAPTURE)
                .second(PositionDto.builder()
                        .first(2)
                        .second(5)
                        .build()
                )
                .build();
        MoveDto blackKingMove = MoveDto.builder()
                .first(MoveType.NORMAL)
                .second(PositionDto.builder()
                        .first(2)
                        .second(4)
                        .build()
                )
                .build();

        Mockito.when(whiteMovesMock.stream()).then(i -> Stream.of(pawnCaptureBlackKing)).then(i -> Stream.of());
        Mockito.when(blackMovesMock.stream()).then(i -> Stream.of()).then(i -> Stream.of(blackKingMove));


        GameStatus expectedStatus = GameStatus.WHITE_CHECK;
        GameStatus actualStatus = gameService.getGameStatus(gameDto, false);

        Assertions.assertEquals(expectedStatus, actualStatus);
    }

    @Test
    void getGameStatusIsBlackCheck() {
        PieceDto whiteKing = PieceDto.builder()
                .pieceType(PieceType.KING)
                .positionX(6)
                .positionY(6)
                .whitePiece(true)
                .build();
        PieceDto blackPawn = PieceDto.builder()
                .pieceType(PieceType.PAWN)
                .positionX(5)
                .positionY(5)
                .whitePiece(false)
                .build();
        PieceDto blackKing = PieceDto.builder()
                .pieceType(PieceType.KING)
                .positionX(2)
                .positionY(5)
                .whitePiece(false)
                .build();
        BoardDto boardDto = BoardDto.builder()
                .whitePieces(List.of(whiteKing))
                .blackPieces(List.of(blackKing, blackPawn))
                .histories(List.of())
                .pieceMatrix(new Integer[8][8][3])
                .build();
        GameDto gameDto = GameDto.builder()
                .board(boardDto)
                .whitePlayerUUID(whitePlayerUUID)
                .blackPlayerUUID(blackPlayerUUID)
                .whiteTurn(true)
                .build();

        MoveDto pawnCaptureWhiteKing = MoveDto.builder()
                .first(MoveType.CAPTURE)
                .second(PositionDto.builder()
                        .first(6)
                        .second(6)
                        .build()
                )
                .build();
        MoveDto whiteKingMove = MoveDto.builder()
                .first(MoveType.NORMAL)
                .second(PositionDto.builder()
                        .first(6)
                        .second(5)
                        .build()
                )
                .build();
        Mockito.when(blackMovesMock.stream()).then(i -> Stream.of(pawnCaptureWhiteKing)).then(i -> Stream.of());
        Mockito.when(whiteMovesMock.stream()).then(i -> Stream.of(whiteKingMove)).then(i -> Stream.of());

        GameStatus expectedStatus = GameStatus.BLACK_CHECK;
        GameStatus actualStatus = gameService.getGameStatus(gameDto, true);

        Assertions.assertEquals(expectedStatus, actualStatus);
    }

    @Test
    void testMovePawnInBoard() {
        PieceDto whiteKing = PieceDto.builder()
                .pieceType(PieceType.KING)
                .positionX(7)
                .positionY(7)
                .whitePiece(true)
                .build();
        PieceDto blackPawn = PieceDto.builder()
                .pieceType(PieceType.PAWN)
                .positionX(5)
                .positionY(5)
                .whitePiece(false)
                .build();
        PieceDto blackKing = PieceDto.builder()
                .pieceType(PieceType.KING)
                .positionX(2)
                .positionY(5)
                .whitePiece(false)
                .build();
        BoardDto boardDto = BoardDto.builder()
                .whitePieces(List.of(whiteKing))
                .blackPieces(List.of(blackKing, blackPawn))
                .histories(List.of())
                .pieceMatrix(new Integer[8][8][3])
                .build();
        GameDto gameDto = GameDto.builder()
                .board(boardDto)
                .whitePlayerUUID(whitePlayerUUID)
                .blackPlayerUUID(blackPlayerUUID)
                .whiteTurn(true)
                .build();

        MoveDto pawnMove = MoveDto.builder()
                .first(MoveType.NORMAL)
                .second(PositionDto.builder()
                        .first(6)
                        .second(5)
                        .build()
                )
                .build();

        gameService.movePieceOnBoardDto(gameDto.getBoard(), blackPawn, pawnMove);

        Assertions.assertEquals(pawnMove.getSecond().getFirst(), blackPawn.getPositionX());
        Assertions.assertEquals(pawnMove.getSecond().getSecond(), blackPawn.getPositionY());
    }

    @Test
    void testMoveWhitePawnInBoardToCapture() {
        PieceDto whiteKing = PieceDto.builder()
                .pieceType(PieceType.KING)
                .positionX(6)
                .positionY(6)
                .whitePiece(true)
                .build();
        PieceDto whitePawn = PieceDto.builder()
                .pieceType(PieceType.PAWN)
                .positionX(4)
                .positionY(4)
                .whitePiece(true)
                .build();
        PieceDto blackKing = PieceDto.builder()
                .pieceType(PieceType.KING)
                .positionX(5)
                .positionY(5)
                .id(1)
                .whitePiece(false)
                .build();
        Integer[][][] pieceMatrix = new Integer[8][8][3];
        pieceMatrix[5][5][0] = 1;
        pieceMatrix[5][5][1] = Constants.BLACK_COLOR;
        pieceMatrix[5][5][2] = PieceType.KING.ordinal();
        BoardDto boardDto = BoardDto.builder()
                .whitePieces(List.of(whiteKing, whitePawn))
                .blackPieces(List.of(blackKing))
                .histories(List.of())
                .pieceMatrix(pieceMatrix)
                .build();
        GameDto gameDto = GameDto.builder()
                .board(boardDto)
                .whitePlayerUUID(whitePlayerUUID)
                .blackPlayerUUID(blackPlayerUUID)
                .whiteTurn(true)
                .build();

        MoveDto pawnMove = MoveDto.builder()
                .first(MoveType.CAPTURE)
                .second(PositionDto.builder()
                        .first(5)
                        .second(5)
                        .build()
                )
                .build();

        gameService.movePieceOnBoardDto(gameDto.getBoard(), whitePawn, pawnMove);

        Assertions.assertEquals(pawnMove.getSecond().getFirst(), whitePawn.getPositionX());
        Assertions.assertEquals(pawnMove.getSecond().getSecond(), whitePawn.getPositionY());

        Assertions.assertEquals(0, boardDto.getBlackPieces().size());
    }

    @Test
    void testMoveBlackPawnInBoardToCapture() {
        PieceDto whiteKing = PieceDto.builder()
                .pieceType(PieceType.KING)
                .positionX(6)
                .positionY(6)
                .id(1)
                .whitePiece(true)
                .build();
        PieceDto blackPawn = PieceDto.builder()
                .pieceType(PieceType.PAWN)
                .positionX(5)
                .positionY(6)
                .whitePiece(false)
                .build();
        PieceDto blackKing = PieceDto.builder()
                .pieceType(PieceType.KING)
                .positionX(2)
                .positionY(5)
                .whitePiece(false)
                .build();
        Integer[][][] pieceMatrix = new Integer[8][8][3];
        pieceMatrix[6][6][0] = 1;
        pieceMatrix[6][6][1] = Constants.WHITE_COLOR;
        pieceMatrix[5][6][2] = PieceType.KING.ordinal();
        BoardDto boardDto = BoardDto.builder()
                .whitePieces(List.of(whiteKing))
                .blackPieces(List.of(blackKing, blackPawn))
                .histories(List.of())
                .pieceMatrix(pieceMatrix)
                .build();
        GameDto gameDto = GameDto.builder()
                .board(boardDto)
                .whitePlayerUUID(whitePlayerUUID)
                .blackPlayerUUID(blackPlayerUUID)
                .whiteTurn(true)
                .build();

        MoveDto pawnMove = MoveDto.builder()
                .first(MoveType.CAPTURE)
                .second(PositionDto.builder()
                        .first(6)
                        .second(6)
                        .build()
                )
                .build();

        gameService.movePieceOnBoardDto(gameDto.getBoard(), blackPawn, pawnMove);

        Assertions.assertEquals(pawnMove.getSecond().getFirst(), blackPawn.getPositionX());
        Assertions.assertEquals(pawnMove.getSecond().getSecond(), blackPawn.getPositionY());

        Assertions.assertEquals(0, boardDto.getWhitePieces().size());
    }

    @Test
    void testMovePawnInBoardToCaptureNotFound() {
        PieceDto whiteKing = PieceDto.builder()
                .pieceType(PieceType.KING)
                .positionX(6)
                .positionY(6)
                .id(1)
                .whitePiece(true)
                .build();
        PieceDto blackPawn = PieceDto.builder()
                .pieceType(PieceType.PAWN)
                .positionX(5)
                .positionY(6)
                .whitePiece(false)
                .build();
        PieceDto blackKing = PieceDto.builder()
                .pieceType(PieceType.KING)
                .positionX(2)
                .positionY(5)
                .whitePiece(false)
                .build();
        BoardDto boardDto = BoardDto.builder()
                .whitePieces(List.of(whiteKing))
                .blackPieces(List.of(blackKing, blackPawn))
                .histories(List.of())
                .pieceMatrix(new Integer[8][8][3])
                .build();
        GameDto gameDto = GameDto.builder()
                .board(boardDto)
                .whitePlayerUUID(whitePlayerUUID)
                .blackPlayerUUID(blackPlayerUUID)
                .whiteTurn(true)
                .build();

        MoveDto pawnMove = MoveDto.builder()
                .first(MoveType.CAPTURE)
                .second(PositionDto.builder()
                        .first(6)
                        .second(6)
                        .build()
                )
                .build();

        gameService.movePieceOnBoardDto(gameDto.getBoard(), blackPawn, pawnMove);

        Assertions.assertEquals(pawnMove.getSecond().getFirst(), blackPawn.getPositionX());
        Assertions.assertEquals(pawnMove.getSecond().getSecond(), blackPawn.getPositionY());

        Assertions.assertEquals(1, boardDto.getWhitePieces().size());
    }

    @Test
    void testMovePawnInBoardToCaptureIdNotMatch() {
        PieceDto whiteKing = PieceDto.builder()
                .pieceType(PieceType.KING)
                .positionX(6)
                .positionY(6)
                .id(1)
                .whitePiece(true)
                .build();
        PieceDto blackPawn = PieceDto.builder()
                .pieceType(PieceType.PAWN)
                .positionX(5)
                .positionY(6)
                .whitePiece(false)
                .build();
        PieceDto blackKing = PieceDto.builder()
                .pieceType(PieceType.KING)
                .positionX(2)
                .positionY(5)
                .whitePiece(false)
                .build();
        Integer[][][] pieceMatrix = new Integer[8][8][3];
        pieceMatrix[6][6][0] = 2;
        pieceMatrix[6][6][1] = Constants.WHITE_COLOR;
        pieceMatrix[5][6][2] = PieceType.KING.ordinal();
        BoardDto boardDto = BoardDto.builder()
                .whitePieces(List.of(whiteKing))
                .blackPieces(List.of(blackKing, blackPawn))
                .histories(List.of())
                .pieceMatrix(pieceMatrix)
                .build();
        GameDto gameDto = GameDto.builder()
                .board(boardDto)
                .whitePlayerUUID(whitePlayerUUID)
                .blackPlayerUUID(blackPlayerUUID)
                .whiteTurn(true)
                .build();

        MoveDto pawnMove = MoveDto.builder()
                .first(MoveType.CAPTURE)
                .second(PositionDto.builder()
                        .first(6)
                        .second(6)
                        .build()
                )
                .build();

        gameService.movePieceOnBoardDto(gameDto.getBoard(), blackPawn, pawnMove);

        Assertions.assertEquals(pawnMove.getSecond().getFirst(), blackPawn.getPositionX());
        Assertions.assertEquals(pawnMove.getSecond().getSecond(), blackPawn.getPositionY());

        Assertions.assertEquals(1, boardDto.getWhitePieces().size());
    }

    @Test
    void testGetPlayerLegalMovesWhenNoWhiteKing() {
        PieceDto blackKing = PieceDto.builder()
                .pieceType(PieceType.KING)
                .positionX(2)
                .positionY(5)
                .id(1)
                .whitePiece(false)
                .build();
        PieceDto blackPawn = PieceDto.builder()
                .pieceType(PieceType.PAWN)
                .positionX(5)
                .positionY(6)
                .id(2)
                .whitePiece(false)
                .build();

        BoardDto boardDto = BoardDto.builder()
                .whitePieces(List.of())
                .blackPieces(List.of(blackKing, blackPawn))
                .histories(List.of())
                .pieceMatrix(new Integer[8][8][3])
                .build();

        GameDto gameDto = GameDto.builder()
                .board(boardDto)
                .whitePlayerUUID(whitePlayerUUID)
                .blackPlayerUUID(blackPlayerUUID)
                .whiteTurn(true)
                .build();

        Map<PieceDto, List<MoveDto>> legalMoves = gameService.getPlayerLegalMoves(gameDto, Constants.WHITE_COLOR);

        Assertions.assertEquals(0, legalMoves.size());
    }

    @Test
    void testGetPlayerLegalMovesWhenNoBlackKing() {
        PieceDto whiteKing = PieceDto.builder()
                .pieceType(PieceType.KING)
                .positionX(2)
                .positionY(5)
                .id(1)
                .whitePiece(true)
                .build();
        PieceDto whitePawn = PieceDto.builder()
                .pieceType(PieceType.PAWN)
                .positionX(5)
                .positionY(6)
                .id(2)
                .whitePiece(true)
                .build();

        BoardDto boardDto = BoardDto.builder()
                .whitePieces(List.of(whiteKing, whitePawn))
                .blackPieces(List.of())
                .histories(List.of())
                .pieceMatrix(new Integer[8][8][3])
                .build();

        GameDto gameDto = GameDto.builder()
                .board(boardDto)
                .whitePlayerUUID(whitePlayerUUID)
                .blackPlayerUUID(blackPlayerUUID)
                .whiteTurn(false)
                .build();

        Map<PieceDto, List<MoveDto>> legalMoves = gameService.getPlayerLegalMoves(gameDto, Constants.WHITE_COLOR);

        Assertions.assertEquals(0, legalMoves.size());
    }

    @Test
    void testPlayerLegalMovesWhitePawnMoves() {
        PieceDto whiteKing = PieceDto.builder()
                .pieceType(PieceType.KING)
                .positionX(2)
                .positionY(5)
                .id(1)
                .whitePiece(true)
                .build();
        PieceDto blackKing = PieceDto.builder()
                .pieceType(PieceType.KING)
                .positionX(6)
                .positionY(6)
                .id(3)
                .whitePiece(false)
                .build();
        PieceDto whitePawn = PieceDto.builder()
                .pieceType(PieceType.PAWN)
                .positionX(5)
                .positionY(6)
                .id(2)
                .whitePiece(true)
                .build();

        BoardDto boardDto = BoardDto.builder()
                .whitePieces(List.of(whiteKing, whitePawn))
                .blackPieces(List.of(blackKing))
                .histories(List.of())
                .pieceMatrix(new Integer[8][8][3])
                .build();

        GameDto gameDto = GameDto.builder()
                .board(boardDto)
                .whitePlayerUUID(whitePlayerUUID)
                .blackPlayerUUID(blackPlayerUUID)
                .whiteTurn(false)
                .build();

        MoveDto move = MoveDto.builder()
                .first(MoveType.NORMAL)
                .second(PositionDto.builder()
                        .first(5)
                        .second(6)
                        .build()
                )
                .build();

        Mockito.when(whiteMovesMock.stream()).then(i -> Stream.of(move));

        Map<PieceDto, List<MoveDto>> legalMoves = gameService.getPlayerLegalMoves(gameDto, Constants.WHITE_COLOR);

        Assertions.assertEquals(1, legalMoves.get(whitePawn).size());
        Assertions.assertEquals(MoveType.NORMAL, legalMoves.get(whitePawn).get(0).getFirst());
    }

    @Test
    void testPlayerLegalMovesBlackPawnMoves() {
        PieceDto whiteKing = PieceDto.builder()
                .pieceType(PieceType.KING)
                .positionX(2)
                .positionY(5)
                .id(1)
                .whitePiece(true)
                .build();
        PieceDto blackKing = PieceDto.builder()
                .pieceType(PieceType.KING)
                .positionX(6)
                .positionY(6)
                .id(3)
                .whitePiece(false)
                .build();
        PieceDto blackPawn = PieceDto.builder()
                .pieceType(PieceType.PAWN)
                .positionX(5)
                .positionY(6)
                .id(2)
                .whitePiece(false)
                .build();

        BoardDto boardDto = BoardDto.builder()
                .whitePieces(List.of(whiteKing))
                .blackPieces(List.of(blackKing, blackPawn))
                .histories(List.of())
                .pieceMatrix(new Integer[8][8][3])
                .build();

        GameDto gameDto = GameDto.builder()
                .board(boardDto)
                .whitePlayerUUID(whitePlayerUUID)
                .blackPlayerUUID(blackPlayerUUID)
                .whiteTurn(true)
                .build();

        MoveDto move = MoveDto.builder()
                .first(MoveType.NORMAL)
                .second(PositionDto.builder()
                        .first(5)
                        .second(6)
                        .build()
                )
                .build();

        Mockito.when(blackMovesMock.stream()).then(i -> Stream.of(move));

        Map<PieceDto, List<MoveDto>> legalMoves = gameService.getPlayerLegalMoves(gameDto, Constants.BLACK_COLOR);

        Assertions.assertEquals(1, legalMoves.get(blackPawn).size());
        Assertions.assertEquals(MoveType.NORMAL, legalMoves.get(blackPawn).get(0).getFirst());
    }

    @Test
    void testPlayerLegalMovesBlackWhenCheckMatte() {
        PieceDto whiteKing = PieceDto.builder()
                .pieceType(PieceType.KING)
                .positionX(2)
                .positionY(5)
                .id(1)
                .whitePiece(true)
                .build();
        PieceDto blackKing = PieceDto.builder()
                .pieceType(PieceType.KING)
                .positionX(6)
                .positionY(6)
                .id(3)
                .whitePiece(false)
                .build();
        PieceDto blackPawn = PieceDto.builder()
                .pieceType(PieceType.PAWN)
                .positionX(1)
                .positionY(6)
                .id(2)
                .whitePiece(false)
                .build();

        BoardDto boardDto = BoardDto.builder()
                .whitePieces(List.of(whiteKing))
                .blackPieces(List.of(blackKing, blackPawn))
                .histories(List.of())
                .pieceMatrix(new Integer[8][8][3])
                .build();

        GameDto gameDto = GameDto.builder()
                .board(boardDto)
                .whitePlayerUUID(whitePlayerUUID)
                .blackPlayerUUID(blackPlayerUUID)
                .whiteTurn(true)
                .build();

        MoveDto move = MoveDto.builder()
                .first(MoveType.CAPTURE)
                .second(PositionDto.builder()
                        .first(2)
                        .second(5)
                        .build()
                )
                .build();

        Mockito.when(blackMovesMock.stream()).then(i -> Stream.of(move));

        Map<PieceDto, List<MoveDto>> legalMoves = gameService.getPlayerLegalMoves(gameDto, Constants.WHITE_COLOR);

        Assertions.assertEquals(1, legalMoves.size());
        Assertions.assertEquals(0, legalMoves.get(whiteKing).size());
    }

    @Test
    void testPlayerLegalMovesBlackWhenCheck() {
        PieceDto whiteKing = PieceDto.builder()
                .pieceType(PieceType.KING)
                .positionX(2)
                .positionY(5)
                .id(1)
                .whitePiece(true)
                .build();
        PieceDto blackKing = PieceDto.builder()
                .pieceType(PieceType.KING)
                .positionX(6)
                .positionY(6)
                .id(3)
                .whitePiece(false)
                .build();
        PieceDto blackPawn = PieceDto.builder()
                .pieceType(PieceType.PAWN)
                .positionX(1)
                .positionY(6)
                .id(2)
                .whitePiece(false)
                .build();

        BoardDto boardDto = BoardDto.builder()
                .whitePieces(List.of(whiteKing))
                .blackPieces(List.of(blackKing, blackPawn))
                .histories(List.of())
                .pieceMatrix(new Integer[8][8][3])
                .build();

        GameDto gameDto = GameDto.builder()
                .board(boardDto)
                .whitePlayerUUID(whitePlayerUUID)
                .blackPlayerUUID(blackPlayerUUID)
                .whiteTurn(true)
                .build();

        MoveDto move = MoveDto.builder()
                .first(MoveType.CAPTURE)
                .second(PositionDto.builder()
                        .first(2)
                        .second(5)
                        .build()
                )
                .build();

        MoveDto moveKing = MoveDto.builder()
                .first(MoveType.NORMAL)
                .second(PositionDto.builder()
                        .first(6)
                        .second(6)
                        .build()
                )
                .build();

        Mockito.when(blackMovesMock.stream()).then(i -> Stream.of(move)).then(i -> Stream.of());
        Mockito.when(whiteMovesMock.stream()).then(i -> Stream.of(moveKing));

        Map<PieceDto, List<MoveDto>> legalMoves = gameService.getPlayerLegalMoves(gameDto, Constants.WHITE_COLOR);

        Assertions.assertEquals(1, legalMoves.size());
        Assertions.assertEquals(1, legalMoves.get(whiteKing).size());
    }

    @Test
    void testPlayerLegalMovesWhiteWhenCheckMatte() {
        PieceDto whiteKing = PieceDto.builder()
                .pieceType(PieceType.KING)
                .positionX(2)
                .positionY(5)
                .id(1)
                .whitePiece(true)
                .build();
        PieceDto blackKing = PieceDto.builder()
                .pieceType(PieceType.KING)
                .positionX(6)
                .positionY(6)
                .id(3)
                .whitePiece(false)
                .build();
        PieceDto whitePawn = PieceDto.builder()
                .pieceType(PieceType.PAWN)
                .positionX(7)
                .positionY(5)
                .id(2)
                .whitePiece(false)
                .build();

        BoardDto boardDto = BoardDto.builder()
                .whitePieces(List.of(whiteKing, whitePawn))
                .blackPieces(List.of(blackKing))
                .histories(List.of())
                .pieceMatrix(new Integer[8][8][3])
                .build();

        GameDto gameDto = GameDto.builder()
                .board(boardDto)
                .whitePlayerUUID(whitePlayerUUID)
                .blackPlayerUUID(blackPlayerUUID)
                .whiteTurn(true)
                .build();

        MoveDto move = MoveDto.builder()
                .first(MoveType.CAPTURE)
                .second(PositionDto.builder()
                        .first(6)
                        .second(6)
                        .build()
                )
                .build();

        Mockito.when(whiteMovesMock.stream()).then(i -> Stream.of(move));

        Map<PieceDto, List<MoveDto>> legalMoves = gameService.getPlayerLegalMoves(gameDto, Constants.BLACK_COLOR);

        Assertions.assertEquals(1, legalMoves.size());
        Assertions.assertEquals(0, legalMoves.get(blackKing).size());
    }

    @Test
    void testPlayerLegalMovesWhiteWhenCheck() {
        PieceDto whiteKing = PieceDto.builder()
                .pieceType(PieceType.KING)
                .positionX(2)
                .positionY(5)
                .id(1)
                .whitePiece(true)
                .build();
        PieceDto blackKing = PieceDto.builder()
                .pieceType(PieceType.KING)
                .positionX(6)
                .positionY(6)
                .id(3)
                .whitePiece(false)
                .build();
        PieceDto whitePawn = PieceDto.builder()
                .pieceType(PieceType.PAWN)
                .positionX(7)
                .positionY(5)
                .id(2)
                .whitePiece(false)
                .build();

        BoardDto boardDto = BoardDto.builder()
                .whitePieces(List.of(whiteKing, whitePawn))
                .blackPieces(List.of(blackKing))
                .histories(List.of())
                .pieceMatrix(new Integer[8][8][3])
                .build();

        GameDto gameDto = GameDto.builder()
                .board(boardDto)
                .whitePlayerUUID(whitePlayerUUID)
                .blackPlayerUUID(blackPlayerUUID)
                .whiteTurn(true)
                .build();

        MoveDto move = MoveDto.builder()
                .first(MoveType.CAPTURE)
                .second(PositionDto.builder()
                        .first(5)
                        .second(5)
                        .build()
                )
                .build();

        MoveDto moveKing = MoveDto.builder()
                .first(MoveType.NORMAL)
                .second(PositionDto.builder()
                        .first(6)
                        .second(6)
                        .build()
                )
                .build();

        Mockito.when(whiteMovesMock.stream()).then(i -> Stream.of(move)).then(i -> Stream.of());
        Mockito.when(blackMovesMock.stream()).then(i -> Stream.of(moveKing));

        Map<PieceDto, List<MoveDto>> legalMoves = gameService.getPlayerLegalMoves(gameDto, Constants.BLACK_COLOR);

        Assertions.assertEquals(1, legalMoves.size());
        Assertions.assertEquals(1, legalMoves.get(blackKing).size());
    }

}