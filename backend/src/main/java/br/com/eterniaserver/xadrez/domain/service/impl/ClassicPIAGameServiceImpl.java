package br.com.eterniaserver.xadrez.domain.service.impl;

import br.com.eterniaserver.xadrez.domain.entities.Game;
import br.com.eterniaserver.xadrez.domain.entities.Piece;
import br.com.eterniaserver.xadrez.domain.enums.GameStatus;
import br.com.eterniaserver.xadrez.domain.enums.GameType;
import br.com.eterniaserver.xadrez.domain.enums.MoveType;
import br.com.eterniaserver.xadrez.domain.repositories.GameRepository;
import br.com.eterniaserver.xadrez.domain.service.GameService;
import br.com.eterniaserver.xadrez.rest.dtos.GameDto;
import lombok.AllArgsConstructor;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service("classicPIAGameService")
@AllArgsConstructor
public class ClassicPIAGameServiceImpl implements GameService {

    private final GameRepository gameRepository;

    @Override
    public List<GameDto> getAllGames() {
        List<Game> gameList = gameRepository.findAllByBlackPlayerUUIDIsNull();
        return convertToDtoList(gameList);
    }

    @Override
    public List<GameDto> getGames() {
        List<Game> gameList = gameRepository.findAllByBlackPlayerUUIDIsNullAndGameTypeEquals(
                GameType.PLAYER_IA_CLASSIC
        );
        return convertToDtoList(gameList);
    }


    @Override
    public GameDto createGame(UUID whiteUUID) {
        return null;
    }

    @Override
    public GameDto enterGame(UUID blackUUID, Integer gameId) {
        return null;
    }

    @Override
    public Map<Pair<Integer, Integer>, List<Pair<MoveType, Pair<Integer, Integer>>>> getPossibleMoves(
            Game game,
            UUID playerUUID
    ) {
        return null;
    }

    @Override
    public GameStatus getGameStatus(GameDto game) {
        return null;
    }

    @Override
    public GameDto movePiece(
            GameDto game,
            UUID playerUUID,
            Piece piece,
            Pair<MoveType, Pair<Integer, Integer>> moveTypePairPair
    ) {
        return null;
    }

}
