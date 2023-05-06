package br.com.eterniaserver.xadrez.rest.controllers;

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

    @PostMapping("{identifier}/")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public PlayerDto register(@PathVariable String identifier) {
        return playerService.registerPlayer(identifier);
    }

    @GetMapping("{uuid}/")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public PlayerDto get(@PathVariable UUID uuid) {
        return playerService.getFromUUID(uuid);
    }

}
