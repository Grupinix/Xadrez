package br.com.eterniaserver.xadrez.domain.service.impl;

import br.com.eterniaserver.xadrez.domain.enums.GameDifficulty;
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
    private static final Map<UUID, GameDifficulty> uuidGameDifficultyMap = new HashMap<>();

    @Override
    public PlayerDto register(String identifier) throws ResponseStatusException {
        if (identifier.length() != 8) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "O identificador precisa ter 8 caracteres!");
        }

        UUID uuid = UUID.randomUUID();

        PlayerDto playerDto = new PlayerDto(uuid, GameDifficulty.EASY, identifier);
        uuidIdentifierMap.put(uuid, playerDto);

        return playerDto;
    }

    @Override
    public PlayerDto getFromUUID(UUID uuid) throws ResponseStatusException {
        PlayerDto playerDto = uuidIdentifierMap.get(uuid);

        if (playerDto == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Jogador não encontrado!");
        }

        return playerDto;
    }

    @Override
    public Boolean verify(PlayerDto playerDto) {
        UUID uuid = playerDto.getUuid();

        PlayerDto savedPlayerDto = uuidIdentifierMap.get(uuid);
        if (savedPlayerDto == null) {
            uuidIdentifierMap.put(uuid, playerDto);
            return true;
        }

        return savedPlayerDto.equals(playerDto);
    }

    @Override
    public void setGameDifficulty(UUID uuid, GameDifficulty gameDifficulty) {
        uuidGameDifficultyMap.put(uuid, gameDifficulty);
    }

    @Override
    public GameDifficulty getGameDifficulty(UUID uuid) {
        return uuidGameDifficultyMap.get(uuid);
    }

}
