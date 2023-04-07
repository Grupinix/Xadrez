package br.com.eterniaserver.xadrez.domain.repositories;

import br.com.eterniaserver.xadrez.domain.entities.History;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HistoryRepository extends JpaRepository<History, Integer> {

}
