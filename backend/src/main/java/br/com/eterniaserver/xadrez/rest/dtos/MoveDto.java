package br.com.eterniaserver.xadrez.rest.dtos;

import br.com.eterniaserver.xadrez.domain.enums.MoveType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MoveDto {

    private MoveType first;
    private PositionDto second;

}
