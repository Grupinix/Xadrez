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
public class PieceDto {

    private Integer id;
    private Boolean whitePiece;
    private PieceType pieceType;
    private Integer positionX;
    private Integer positionY;

    public PieceDto copy() {
        return PieceDto.builder()
                       .id(getId())
                       .whitePiece(getWhitePiece())
                       .pieceType(getPieceType())
                       .positionX(getPositionX())
                       .positionY(getPositionY())
                       .build();
    }

}
