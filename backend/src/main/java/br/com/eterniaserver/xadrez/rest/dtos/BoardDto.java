package br.com.eterniaserver.xadrez.rest.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BoardDto {

    private Integer id;
    private List<PieceDto> whitePieces;
    private List<PieceDto> blackPieces;
    private List<HistoryDto> histories;
    private Integer[][][] pieceMatrix;

    public BoardDto copy() {
        return BoardDto.builder()
                       .id(getId())
                       .whitePieces(getWhitePieces().stream().map(PieceDto::copy).toList())
                       .blackPieces(getBlackPieces().stream().map(PieceDto::copy).toList())
                       .histories(getHistories().stream().map(HistoryDto::copy).toList())
                       .pieceMatrix(getPieceMatrix().clone())
                       .build();
    }

}
