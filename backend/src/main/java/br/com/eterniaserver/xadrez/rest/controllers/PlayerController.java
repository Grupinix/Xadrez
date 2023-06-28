package br.com.eterniaserver.xadrez.rest.controllers;

import br.com.eterniaserver.xadrez.domain.enums.GameDifficulty;
import br.com.eterniaserver.xadrez.domain.enums.PieceType;
import br.com.eterniaserver.xadrez.domain.service.PlayerService;
import br.com.eterniaserver.xadrez.rest.dtos.PlayerDto;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/player/")
@RequiredArgsConstructor
public class PlayerController {

    private final PlayerService playerService;

    @PutMapping("verify/")
    @ResponseStatus(HttpStatus.OK)
    public Boolean verify(@RequestBody PlayerDto playerDto) {
        return playerService.verify(playerDto);
    }

    @PutMapping("{identifier}/")
    @ResponseStatus(HttpStatus.OK)
    public PlayerDto register(@PathVariable String identifier) {
        return playerService.register(identifier);
    }

    @GetMapping("{uuid}/")
    @ResponseStatus(HttpStatus.OK)
    public PlayerDto get(@PathVariable UUID uuid) {
        return playerService.getFromUUID(uuid);
    }

    @PutMapping("setDifficulty/{gameDifficulty}/")
    @ResponseStatus(HttpStatus.OK)
    public PlayerDto setGameDifficulty(@PathVariable GameDifficulty gameDifficulty, @RequestBody PlayerDto playerDto) {
        playerDto.setGameDifficulty(gameDifficulty);
        playerService.setGameDifficulty(playerDto.getUuid(), gameDifficulty);
        return playerDto;
    }

    @PutMapping("setPawnToPiece/{piece}/")
    @ResponseStatus(HttpStatus.OK)
    public void setPawnToPiece(@PathVariable PieceType piece, @RequestBody PlayerDto playerDto) {
        playerService.setPawnToPiece(playerDto.getUuid(), piece);
    }

}
