package br.com.eterniaserver.xadrez.rest.controllers;

import br.com.eterniaserver.xadrez.domain.entities.Game;
import br.com.eterniaserver.xadrez.domain.entities.Piece;
import br.com.eterniaserver.xadrez.domain.enums.GameStatus;
import br.com.eterniaserver.xadrez.domain.enums.GameType;
import br.com.eterniaserver.xadrez.domain.ia.GameIa;
import br.com.eterniaserver.xadrez.domain.repositories.PieceRepository;
import br.com.eterniaserver.xadrez.domain.service.GameService;
import br.com.eterniaserver.xadrez.domain.service.impl.ClassicPIAGameServiceImpl;
import br.com.eterniaserver.xadrez.domain.service.impl.ClassicPPGameServiceImpl;
import br.com.eterniaserver.xadrez.rest.dtos.BoardDto;
import br.com.eterniaserver.xadrez.rest.dtos.GameDto;
import br.com.eterniaserver.xadrez.rest.dtos.MoveDto;
import br.com.eterniaserver.xadrez.rest.dtos.PieceDto;
import br.com.eterniaserver.xadrez.rest.dtos.PlayerDto;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@RestController
@RequestMapping("/api/game/")
@RequiredArgsConstructor
public class GameController {

    private final ConcurrentMap<Integer, Integer> iaGamesMap = new ConcurrentHashMap<>();
    private final ClassicPIAGameServiceImpl classicPIAGameService;
    private final ClassicPPGameServiceImpl classicPPGameService;
    private final PieceRepository pieceRepository;
    private final GameIa gameIa;

    private GameService getGameService(GameType gameType) {
        return switch (gameType) {
            case PLAYER_IA_CLASSIC -> classicPIAGameService;
            case PLAYER_PLAYER_CLASSIC -> classicPPGameService;
        };
    }

    @Scheduled(cron = "*/2 * * * * *")
    public void runIaMove() {
        List<Integer> loadedGames = new ArrayList<>();

        for (Map.Entry<Integer, Integer> entry : iaGamesMap.entrySet()) {
            gameIa.movePiece(entry.getKey());
            loadedGames.add(entry.getKey());
        }

        for (Integer gameId : loadedGames) {
            iaGamesMap.remove(gameId);
        }
    }

    @GetMapping("check/{type}/{gameId}/")
    @ResponseStatus(HttpStatus.OK)
    public boolean checkGame(@PathVariable GameType type, @PathVariable Integer gameId) {
        return getGameService(type).checkGame(gameId);
    }

    @GetMapping("get/{type}/{gameId}/")
    @ResponseStatus(HttpStatus.OK)
    public GameDto getGame(@PathVariable GameType type, @PathVariable Integer gameId) {
        return getGameService(type).getGame(gameId).getGameDto();
    }

    @PostMapping("move/{type}/{gameId}/{uuid}/{pieceId}/")
    @ResponseStatus(HttpStatus.OK)
    private GameDto movePiece(@PathVariable GameType type,
                              @PathVariable Integer gameId,
                              @PathVariable UUID uuid,
                              @PathVariable Integer pieceId,
                              @RequestBody MoveDto move) {
        GameService gameService = getGameService(type);
        Game game = gameService.getGame(gameId);
        Piece piece = pieceRepository.findById(pieceId).orElseThrow();

        iaGamesMap.put(gameId, gameId);

        return gameService.movePiece(game, uuid, piece, move).getGameDto();
    }

    @PutMapping("getPiecePossibleMoves/{type}/{gameId}/{uuid}/")
    @ResponseStatus(HttpStatus.OK)
    public List<MoveDto> getPiecePossibleMoves(@PathVariable GameType type,
                                               @PathVariable Integer gameId,
                                               @PathVariable UUID uuid,
                                               @RequestBody Piece piece) {

        GameService gameService = getGameService(type);
        Game game = gameService.getGame(gameId);

        GameDto gameDto = game.getGameDto();
        BoardDto boardDto = gameDto.getBoard();
        boolean isWhiteTurn = game.getWhiteTurn();
        boolean isWhitePlayer = game.getWhitePlayerUUID().equals(uuid);
        List<PieceDto> playerPieces = isWhitePlayer ? boardDto.getWhitePieces() : boardDto.getBlackPieces();
        PieceDto pieceDto = playerPieces.stream()
                .filter(p -> p.getId().equals(piece.getId()))
                .findFirst()
                .orElseThrow();

        List<MoveDto> possibleMoves = gameService.getPossibleMoves(gameDto, pieceDto, uuid);

        List<GameStatus> validStatus = isWhitePlayer
                ? List.of(GameStatus.NORMAL, GameStatus.WHITE_CHECK, GameStatus.WHITE_WINS)
                : List.of(GameStatus.NORMAL, GameStatus.BLACK_CHECK, GameStatus.BLACK_WINS);
        
        return gameService.getValidMoves(possibleMoves, validStatus, gameDto, pieceDto, isWhiteTurn);
    }

    @GetMapping("refresh/{type}/{gameId}/")
    @ResponseStatus(HttpStatus.OK)
    public void refreshGameTimer(@PathVariable GameType type, @PathVariable Integer gameId) {
        getGameService(type).refreshGameTimer(gameId);
    }

    @GetMapping("status/{type}/{gameId}/")
    @ResponseStatus(HttpStatus.OK)
    public GameStatus getGameStatus(@PathVariable GameType type, @PathVariable Integer gameId) {
        GameService gameService = getGameService(type);
        Game game = gameService.getGame(gameId);

        return gameService.getGameStatus(game.getGameDto());
    }

    @GetMapping("list/")
    @ResponseStatus(HttpStatus.OK)
    public List<GameDto> listGames() {
        return classicPPGameService.getAllGames().stream().map(Game::getGameDto).toList();
    }

    @GetMapping("list/{type}/")
    @ResponseStatus(HttpStatus.OK)
    public List<GameDto> listGamesByType(@PathVariable GameType type) {
        return getGameService(type).getGames().stream().map(Game::getGameDto).toList();
    }

    @PostMapping("create/{type}/")
    @ResponseStatus(HttpStatus.OK)
    public GameDto createGame(@PathVariable GameType type, @RequestBody PlayerDto playerDto) {
        return getGameService(type).createGame(playerDto.getUuid()).getGameDto();
    }

    @PutMapping("enter/{type}/{gameId}/")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public GameDto enterGame(@PathVariable GameType type,
                             @PathVariable Integer gameId,
                             @RequestBody PlayerDto playerDto) {
        return getGameService(type).enterGame(playerDto.getUuid(), gameId).getGameDto();
    }
}
