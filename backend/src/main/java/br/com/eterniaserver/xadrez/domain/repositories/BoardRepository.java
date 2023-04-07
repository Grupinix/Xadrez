package br.com.eterniaserver.xadrez.domain.repositories;

import br.com.eterniaserver.xadrez.domain.entities.Board;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BoardRepository extends JpaRepository<Board, Integer> {
}
