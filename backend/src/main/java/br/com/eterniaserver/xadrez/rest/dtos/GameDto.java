package br.com.eterniaserver.xadrez.rest.dtos;

import br.com.eterniaserver.xadrez.domain.enums.GameDifficulty;
import br.com.eterniaserver.xadrez.domain.enums.GameType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GameDto {

    private Integer id;
    private GameType gameType;
    private GameDifficulty gameDifficulty;
    private BoardDto board;
    private Boolean whiteTurn;
    private UUID whitePlayerUUID;
    private Integer whiteMoves;
    private UUID blackPlayerUUID;
    private Integer blackMoves;

}
