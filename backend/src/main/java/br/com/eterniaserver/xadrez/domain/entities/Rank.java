package br.com.eterniaserver.xadrez.domain.entities;


import br.com.eterniaserver.xadrez.domain.enums.GameType;
import jakarta.persistence.*;
import jakarta.persistence.Table;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "rank")
@Data
public class Rank {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(name = "identifier", nullable = false)
    private String identifier;

    @Enumerated(EnumType.STRING)
    @Column(name = "game_type", nullable = false)
    private GameType gameType;

    @Column(name = "moves", nullable = false)
    private Integer moves;

    @Column(name = "date_time", nullable = false)
    private LocalDateTime dateTime;

}
