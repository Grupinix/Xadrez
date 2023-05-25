package br.com.eterniaserver.xadrez.domain.entities;

import br.com.eterniaserver.xadrez.domain.enums.PieceType;
import br.com.eterniaserver.xadrez.rest.dtos.PieceDto;
import jakarta.persistence.*;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "piece")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Piece {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "white_board_id")
    private Board whiteBoard;

    @ManyToOne
    @JoinColumn(name = "black_board_id")
    private Board blackBoard;

    @Enumerated(EnumType.STRING)
    @Column(name = "piece_type", nullable = false)
    private PieceType pieceType;

    @Column(name = "position_x", nullable = false)
    private Integer positionX;

    @Column(name = "position_y", nullable = false)
    private Integer positionY;

    public PieceDto getPieceDto() {
        return PieceDto.builder()
                .id(getId())
                .pieceType(getPieceType())
                .positionX(getPositionX())
                .positionY(getPositionY())
                .whitePiece(getWhiteBoard() != null)
                .build();
    }

}
