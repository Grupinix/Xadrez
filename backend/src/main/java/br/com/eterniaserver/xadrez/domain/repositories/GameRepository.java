package br.com.eterniaserver.xadrez.domain.repositories;

import br.com.eterniaserver.xadrez.domain.entities.Game;
import br.com.eterniaserver.xadrez.domain.enums.GameType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GameRepository extends JpaRepository<Game, Integer> {

    List<Game> findAllByBlackPlayerUUIDIsNull();

    List<Game> findAllByBlackPlayerUUIDIsNullAndGameTypeEquals(GameType gameType);

}
