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

}
