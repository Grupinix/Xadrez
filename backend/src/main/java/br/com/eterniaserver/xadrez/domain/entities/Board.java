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

    @OneToMany(mappedBy = "whiteBoard")
    private List<Piece> whitePieces;

    @OneToMany(mappedBy = "blackBoard")
    private List<Piece> blackPieces;

    @OneToMany(mappedBy = "board")
    private List<History> histories = new ArrayList<>();

    public BoardDto getBoardDto() {
        return BoardDto.builder()
                .id(getId())
                .whitePieces(getWhitePieces().stream().map(Piece::getPieceDto).toList())
                .blackPieces(getBlackPieces().stream().map(Piece::getPieceDto).toList())
                .histories(getHistories().stream().map(History::getHistoryDto).toList())
                .build();
    }


}
