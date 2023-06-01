package br.com.eterniaserver.xadrez.domain.entities;

import br.com.eterniaserver.xadrez.domain.enums.GameDifficulty;
import br.com.eterniaserver.xadrez.domain.enums.GameStatus;
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

    @Enumerated(EnumType.STRING)
    @Column(name = "game_status")
    private GameStatus gameStatus = GameStatus.NORMAL;

    @Column(name = "status_cached", nullable = false)
    private Boolean statusCached = Boolean.FALSE;

    @OneToOne(cascade = CascadeType.REMOVE)
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

    @Column(name = "time", nullable = false)
    private Long timer;

    public GameDto getGameDto() {
        return GameDto.builder()
                      .id(getId())
                      .gameType(getGameType())
                      .gameDifficulty(getGameDifficulty())
                      .gameStatus(getGameStatus())
                      .statusCached(getStatusCached())
                      .board(getBoard().getBoardDto())
                      .whiteTurn(getWhiteTurn())
                      .whitePlayerUUID(getWhitePlayerUUID())
                      .whiteMoves(getWhiteMoves())
                      .blackPlayerUUID(getBlackPlayerUUID())
                      .blackMoves(getBlackMoves())
                      .timer(getTimer())
                      .build();
    }

}
