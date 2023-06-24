package br.com.eterniaserver.xadrez.rest.controllers;

import br.com.eterniaserver.xadrez.domain.repositories.RankRepository;
import br.com.eterniaserver.xadrez.rest.dtos.RankDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/rank/")
@RequiredArgsConstructor
public class RankController {

    private final RankRepository rankRepository;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<RankDto> listRanks() {
        return rankRepository.findAll(Sort.by(Sort.Direction.ASC, "moves"))
                .stream()
                .map(RankDto::new)
                .toList();
    }

}
