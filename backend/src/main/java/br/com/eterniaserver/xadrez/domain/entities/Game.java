package br.com.eterniaserver.xadrez.domain.entities;

import br.com.eterniaserver.xadrez.domain.enums.GameDifficulty;
import br.com.eterniaserver.xadrez.domain.enums.GameType;
import br.com.eterniaserver.xadrez.rest.dtos.GameDto;
import jakarta.persistence.*;
import lombok.Data;

import java.util.UUID;

@Entity
@Table(name = "game")
@Data
public class Game {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Enumerated(EnumType.STRING)
    @Column(name = "game_type", nullable = false)
    private GameType gameType;

    @Enumerated(EnumType.STRING)
    @Column(name = "game_difficulty")
    private GameDifficulty gameDifficulty;

    @OneToOne
    @JoinColumn(name = "board_id", nullable = false)
    private Board board;

    @Column(name = "white_turn", nullable = false)
    private Boolean whiteTurn;

    @Column(name = "white_player_uuid", nullable = false)
    private UUID whitePlayerUUID;

    @Column(name = "white_moves", nullable = false)
    private Integer whiteMoves;

    @Column(name = "black_player_uuid")
    private UUID blackPlayerUUID;

    @Column(name = "black_moves", nullable = false)
    private Integer blackMoves;

    public GameDto getGameDto() {
        return GameDto.builder()
                .id(getId())
                .gameType(getGameType())
                .gameDifficulty(getGameDifficulty())
                .board(getBoard().getBoardDto())
                .whiteTurn(getWhiteTurn())
                .whitePlayerUUID(getWhitePlayerUUID())
                .whiteMoves(getWhiteMoves())
                .blackPlayerUUID(getBlackPlayerUUID())
                .blackMoves(getBlackMoves())
                .build();
    }


}
