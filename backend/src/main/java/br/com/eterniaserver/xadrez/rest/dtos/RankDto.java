package br.com.eterniaserver.xadrez.rest.dtos;

import br.com.eterniaserver.xadrez.domain.entities.Rank;
import br.com.eterniaserver.xadrez.domain.enums.GameType;
import lombok.Getter;

import java.time.format.DateTimeFormatter;

@Getter
public class RankDto {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    private final String identifier;
    private final GameType gameType;
    private final Integer moves;
    private final String dateTime;

    public RankDto(Rank rank) {
        this.identifier = rank.getIdentifier();
        this.gameType = rank.getGameType();
        this.moves = rank.getMoves();
        this.dateTime = rank.getDateTime().format(DATE_FORMATTER);
    }
}
