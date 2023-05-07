package br.com.eterniaserver.xadrez.domain.entities;

import br.com.eterniaserver.xadrez.rest.dtos.BoardDto;
import jakarta.persistence.*;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "board")
@Data
public class Board {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    private Integer id;

    @OneToMany(mappedBy = "whiteBoard", cascade = CascadeType.REMOVE)
    private List<Piece> whitePieces;

    @OneToMany(mappedBy = "blackBoard", cascade = CascadeType.REMOVE)
    private List<Piece> blackPieces;

    @OneToMany(mappedBy = "board", cascade = CascadeType.REMOVE)
    private List<History> histories = new ArrayList<>();

    public Integer[][][] getPieceMatrix() {
        Integer[][][] pieceMatrix = new Integer[8][8][1];

        for (Piece piece : getWhitePieces()) {
            pieceMatrix[piece.getPositionX()][piece.getPositionY()] = new Integer[] {piece.getId(), 1};
        }
        for (Piece piece : getBlackPieces()) {
            pieceMatrix[piece.getPositionX()][piece.getPositionY()] = new Integer[] {piece.getId(), 0};
        }

        return pieceMatrix;
    }

    public BoardDto getBoardDto() {
        return BoardDto.builder()
                .id(getId())
                .whitePieces(getWhitePieces().stream().map(Piece::getPieceDto).toList())
                .blackPieces(getBlackPieces().stream().map(Piece::getPieceDto).toList())
                .histories(getHistories().stream().map(History::getHistoryDto).toList())
                .pieceMatrix(getPieceMatrix())
                .build();
    }


}
