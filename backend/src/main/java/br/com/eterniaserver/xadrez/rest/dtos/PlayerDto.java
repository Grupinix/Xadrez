package br.com.eterniaserver.xadrez.rest.dtos;

import br.com.eterniaserver.xadrez.domain.enums.GameDifficulty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PlayerDto {

    private UUID uuid;
    private GameDifficulty gameDifficulty;
    private String identifier;

    public GameDifficulty getGameDifficulty() {
        return gameDifficulty == null ? GameDifficulty.EASY : gameDifficulty;
    }
}
