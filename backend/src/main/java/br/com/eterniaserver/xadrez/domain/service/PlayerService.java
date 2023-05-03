package br.com.eterniaserver.xadrez.domain.service;

import br.com.eterniaserver.xadrez.rest.dtos.PlayerDto;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;

public interface PlayerService {

    PlayerDto registerPlayer(String identifier) throws ResponseStatusException;

    PlayerDto getFromUUID(UUID uuid) throws ResponseStatusException;

}
