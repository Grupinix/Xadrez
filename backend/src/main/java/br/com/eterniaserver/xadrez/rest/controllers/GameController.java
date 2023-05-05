package br.com.eterniaserver.xadrez.rest.controllers;

import br.com.eterniaserver.xadrez.domain.entities.Game;
import br.com.eterniaserver.xadrez.domain.enums.GameType;
import br.com.eterniaserver.xadrez.domain.service.GameService;
import br.com.eterniaserver.xadrez.domain.service.impl.ClassicPIAGameServiceImpl;
import br.com.eterniaserver.xadrez.domain.service.impl.ClassicPPGameServiceImpl;
import br.com.eterniaserver.xadrez.rest.dtos.GameDto;
import br.com.eterniaserver.xadrez.rest.dtos.PlayerDto;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/game/")
@RequiredArgsConstructor
public class GameController {

    private final ClassicPIAGameServiceImpl classicPIAGameService;
    private final ClassicPPGameServiceImpl classicPPGameService;

    private GameService getGameService(GameType gameType) {
        return switch (gameType) {
            case PLAYER_IA_CLASSIC -> classicPIAGameService;
            case PLAYER_PLAYER_CLASSIC -> classicPPGameService;
        };
    }

    @GetMapping("check/{type}/{gameId}/")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public boolean checkGame(@PathVariable GameType type, @PathVariable Integer gameId) {
        return getGameService(type).checkGame(gameId);
    }

    @GetMapping("refresh/{type}/{gameId}/")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void refreshGameTimer(@PathVariable GameType type, @PathVariable Integer gameId) {
        getGameService(type).refreshGameTimer(gameId);
    }

    @GetMapping("list/")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public List<GameDto> listGames() {
        return classicPPGameService.getAllGames().stream().map(Game::getGameDto).toList();
    }

    @GetMapping("list/{type}/")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public List<GameDto> listGamesByType(@PathVariable GameType type) {
        return getGameService(type).getGames().stream().map(Game::getGameDto).toList();
    }

    @PostMapping("create/{type}/")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public GameDto createGame(@PathVariable GameType type, @RequestBody PlayerDto playerDto) {
        return getGameService(type).createGame(playerDto.getUuid()).getGameDto();
    }

}
