package br.com.eterniaserver.xadrez.domain.entities;

import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Entity
@Table(name = "board")
@Data
public class Board {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    private Integer id;

    @OneToMany(mappedBy = "whiteBoard")
    private List<Piece> whitePieces;

    @OneToMany(mappedBy = "blackBoard")
    private List<Piece> blackPieces;

    @OneToMany(mappedBy = "board")
    private List<History> histories;


}
