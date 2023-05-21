package br.com.eterniaserver.xadrez.rest.controllers;

import br.com.eterniaserver.xadrez.domain.entities.Game;
import br.com.eterniaserver.xadrez.domain.entities.Piece;
import br.com.eterniaserver.xadrez.domain.enums.GameStatus;
import br.com.eterniaserver.xadrez.domain.enums.GameType;
import br.com.eterniaserver.xadrez.domain.repositories.PieceRepository;
import br.com.eterniaserver.xadrez.domain.service.GameService;
import br.com.eterniaserver.xadrez.domain.service.impl.ClassicPIAGameServiceImpl;
import br.com.eterniaserver.xadrez.domain.service.impl.ClassicPPGameServiceImpl;
import br.com.eterniaserver.xadrez.rest.dtos.GameDto;
import br.com.eterniaserver.xadrez.rest.dtos.MoveDto;
import br.com.eterniaserver.xadrez.rest.dtos.PlayerDto;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/game/")
@RequiredArgsConstructor
public class GameController {

    private final ClassicPIAGameServiceImpl classicPIAGameService;
    private final ClassicPPGameServiceImpl classicPPGameService;
    private final PieceRepository pieceRepository;

    private GameService getGameService(GameType gameType) {
        return switch (gameType) {
            case PLAYER_IA_CLASSIC -> classicPIAGameService;
            case PLAYER_PLAYER_CLASSIC -> classicPPGameService;
        };
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
        return getGameService(type).getPossibleMoves(game.getGameDto(), piece.getPieceDto(), uuid);

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

        boolean isWhiteTurn = game.getWhiteTurn();

        return gameService.getGameStatus(game.getBoard().getBoardDto(), isWhiteTurn);
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

}
