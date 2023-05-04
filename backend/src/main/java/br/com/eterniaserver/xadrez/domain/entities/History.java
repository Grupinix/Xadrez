package br.com.eterniaserver.xadrez.domain.entities;

import br.com.eterniaserver.xadrez.domain.enums.PieceType;
import br.com.eterniaserver.xadrez.rest.dtos.BoardDto;
import br.com.eterniaserver.xadrez.rest.dtos.HistoryDto;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "history")
@Data
public class History {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "board_id", nullable = false)
    private Board board;

    @Enumerated(EnumType.STRING)
    @Column(name = "piece_type", nullable = false)
    private PieceType pieceType;

    @Column(name = "old_position_x", nullable = false)
    private Integer oldPositionX;

    @Column(name = "old_position_y", nullable = false)
    private Integer oldPositionY;

    @Column(name = "new_position_x", nullable = false)
    private Integer newPositionX;

    @Column(name = "new_position_y", nullable = false)
    private Integer newPositionY;

    @Enumerated(EnumType.STRING)
    @Column(name = "killed_piece")
    private PieceType killedPiece;

    public HistoryDto getHistoryDto() {
        return HistoryDto.builder()
                .id(getId())
                .pieceType(getPieceType())
                .oldPositionX(getOldPositionX())
                .oldPositionY(getOldPositionY())
                .newPositionX(getNewPositionX())
                .newPositionY(getNewPositionY())
                .killedPiece(getKilledPiece())
                .build();
    }

}
