package br.com.eterniaserver.xadrez.domain.repositories;

import br.com.eterniaserver.xadrez.domain.entities.Rank;
import org.springframework.data.jpa.repository.JpaRepository;


public interface RankRepository extends JpaRepository<Rank, Integer> {
}
