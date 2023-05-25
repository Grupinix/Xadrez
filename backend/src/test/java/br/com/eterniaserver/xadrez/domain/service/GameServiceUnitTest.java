package br.com.eterniaserver.xadrez.domain.service;

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
                .pieceMatrix(new Integer[8][8][2])
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
                .pieceMatrix(new Integer[8][8][2])
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
                .pieceMatrix(new Integer[8][8][2])
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
                .pieceMatrix(new Integer[8][8][2])
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
                .pieceMatrix(new Integer[8][8][2])
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
                .pieceMatrix(new Integer[8][8][2])
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
                .pieceMatrix(new Integer[8][8][2])
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
                .pieceMatrix(new Integer[8][8][2])
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
                .pieceMatrix(new Integer[8][8][2])
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
                .pieceMatrix(new Integer[8][8][2])
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

}
