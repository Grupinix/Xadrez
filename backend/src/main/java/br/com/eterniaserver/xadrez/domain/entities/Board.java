package br.com.eterniaserver.xadrez.domain.entities;

import br.com.eterniaserver.xadrez.rest.dtos.BoardDto;
import jakarta.persistence.*;
import lombok.Data;

import java.util.List;
import java.util.stream.Collectors;

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
    private List<History> histories;

    public BoardDto getBoardDto() {
        final BoardDto boardDto = BoardDto.builder().id(getId()).build();

        boardDto.setWhitePieces(
                getWhitePieces().stream()
                        .map(whitePiece -> whitePiece.getPieceDto(boardDto))
                        .collect(Collectors.toList())
        );
        boardDto.setBlackPieces(
                getBlackPieces().stream()
                        .map(blackPiece -> blackPiece.getPieceDto(boardDto))
                        .collect(Collectors.toList())
        );
        boardDto.setHistories(
                getHistories().stream()
                        .map(history -> history.getHistoryDto(boardDto))
                        .collect(Collectors.toList())
        );

        return boardDto;
    }


}
