package br.com.eterniaserver.xadrez.domain.repositories;

import br.com.eterniaserver.xadrez.domain.entities.Piece;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PieceRepository extends JpaRepository<Piece, Integer> {
}
