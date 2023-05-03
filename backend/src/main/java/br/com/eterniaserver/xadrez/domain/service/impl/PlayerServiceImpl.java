package br.com.eterniaserver.xadrez.domain.service.impl;

import br.com.eterniaserver.xadrez.domain.service.PlayerService;
import br.com.eterniaserver.xadrez.rest.dtos.PlayerDto;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service("playerService")
@AllArgsConstructor
public class PlayerServiceImpl implements PlayerService {

    private static final Map<UUID, PlayerDto> uuidIdentifierMap = new HashMap<>();

    @Override
    public PlayerDto registerPlayer(String identifier) throws ResponseStatusException {
        if (identifier.length() != 8) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }

        UUID uuid = UUID.randomUUID();

        PlayerDto playerDto = new PlayerDto(uuid, identifier);
        uuidIdentifierMap.put(uuid, playerDto);

        return playerDto;
    }

    @Override
    public PlayerDto getFromUUID(UUID uuid) throws ResponseStatusException {
        PlayerDto playerDto = uuidIdentifierMap.get(uuid);

        if (playerDto == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }

        return playerDto;
    }
}
