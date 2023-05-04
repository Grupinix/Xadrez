package br.com.eterniaserver.xadrez.domain.entities;

import br.com.eterniaserver.xadrez.domain.enums.PieceType;
import br.com.eterniaserver.xadrez.rest.dtos.BoardDto;
import br.com.eterniaserver.xadrez.rest.dtos.PieceDto;
import jakarta.persistence.*;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "piece")
@Data
public class Piece {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "white_board_id", nullable = false)
    private Board whiteBoard;

    @ManyToOne
    @JoinColumn(name = "black_board_id", nullable = false)
    private Board blackBoard;

    @Enumerated(EnumType.STRING)
    @Column(name = "piece_type", nullable = false)
    private PieceType pieceType;

    @Column(name = "position_x", nullable = false)
    private Integer positionX;

    @Column(name = "position_y", nullable = false)
    private Integer positionY;

    public PieceDto getPieceDto(BoardDto boardDto) {
        PieceDto pieceDto = PieceDto.builder()
                .id(getId())
                .pieceType(getPieceType())
                .positionX(getPositionX())
                .positionY(getPositionY())
                .build();

        if (getWhiteBoard() != null) {
            pieceDto.setWhiteBoard(boardDto);
        }
        else {
            pieceDto.setBlackBoard(boardDto);
        }

        return pieceDto;
    }

}
