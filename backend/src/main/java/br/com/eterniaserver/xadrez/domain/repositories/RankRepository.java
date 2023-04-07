package br.com.eterniaserver.xadrez.domain.repositories;

import br.com.eterniaserver.xadrez.domain.entities.Rank;
import br.com.eterniaserver.xadrez.domain.enums.GameType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RankRepository extends JpaRepository<Rank, Integer> {

    List<Rank> findAllByGameType(GameType gameType);

}
