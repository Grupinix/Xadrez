package br.com.eterniaserver.xadrez.rest.controllers;

import br.com.eterniaserver.xadrez.domain.enums.GameType;
import br.com.eterniaserver.xadrez.domain.service.GameService;
import br.com.eterniaserver.xadrez.domain.service.impl.ClassicPIAGameService;
import br.com.eterniaserver.xadrez.domain.service.impl.ClassicPPGameService;
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

    private final ClassicPIAGameService classicPIAGameService;
    private final ClassicPPGameService classicPPGameService;

    private GameService getGameService(GameType gameType) {
        return switch (gameType) {
            case PLAYER_IA_CLASSIC -> classicPIAGameService;
            case PLAYER_PLAYER_CLASSIC -> classicPPGameService;
        };
    }

    @GetMapping("list/")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public List<GameDto> listGames() {
        return classicPPGameService.getAllGames();
    }

    @GetMapping("list/{type}/")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public List<GameDto> listGamesByType(@PathVariable GameType type) {
        return getGameService(type).getGames();
    }

    @PostMapping("create/{type}")
    public GameDto createGame(@PathVariable GameType type, @RequestBody PlayerDto playerDto) {
        return getGameService(type).createGame(playerDto.getUuid());
    }

}
