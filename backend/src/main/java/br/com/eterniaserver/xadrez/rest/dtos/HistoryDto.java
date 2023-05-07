package br.com.eterniaserver.xadrez.rest.dtos;

import br.com.eterniaserver.xadrez.domain.enums.PieceType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class HistoryDto {

    private Integer id;
    private PieceType pieceType;
    private Boolean isWhite;
    private Integer oldPositionX;
    private Integer oldPositionY;
    private Integer newPositionX;
    private Integer newPositionY;
    private PieceType killedPiece;

}
