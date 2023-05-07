package br.com.eterniaserver.xadrez.domain.service;

import br.com.eterniaserver.xadrez.domain.enums.GameDifficulty;
import br.com.eterniaserver.xadrez.rest.dtos.PlayerDto;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;

public interface PlayerService {

    PlayerDto register(String identifier) throws ResponseStatusException;

    PlayerDto getFromUUID(UUID uuid) throws ResponseStatusException;

    Boolean verify(PlayerDto playerDto);

    void setGameDifficulty(UUID uuid, GameDifficulty gameDifficulty);

    GameDifficulty getGameDifficulty(UUID uuid);

}
