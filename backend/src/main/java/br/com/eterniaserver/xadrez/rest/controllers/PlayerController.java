package br.com.eterniaserver.xadrez.rest.controllers;

import br.com.eterniaserver.xadrez.rest.dtos.PlayerDto;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/player/")
@RequiredArgsConstructor
public class PlayerController {

    @PutMapping("{identifier}/")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public PlayerDto register(@PathVariable String identifier) {
        UUID uuid = UUID.fromString(identifier);

        return new PlayerDto(uuid, identifier);
    }

}
