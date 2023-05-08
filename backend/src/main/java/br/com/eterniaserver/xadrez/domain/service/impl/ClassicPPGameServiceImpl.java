package br.com.eterniaserver.xadrez.domain.service.impl;

import br.com.eterniaserver.xadrez.domain.entities.Game;
import br.com.eterniaserver.xadrez.domain.entities.Piece;
import br.com.eterniaserver.xadrez.domain.enums.GameStatus;
import br.com.eterniaserver.xadrez.domain.enums.GameType;
import br.com.eterniaserver.xadrez.domain.repositories.GameRepository;
import br.com.eterniaserver.xadrez.domain.service.GameService;
import br.com.eterniaserver.xadrez.rest.dtos.GameDto;
import br.com.eterniaserver.xadrez.rest.dtos.MoveDto;
import br.com.eterniaserver.xadrez.rest.dtos.PieceDto;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;


@Service("classicPPGameService")
@AllArgsConstructor
public class ClassicPPGameServiceImpl implements GameService {

    private final GameRepository gameRepository;

    @Override
    public List<Game> getAllGames() {
        return gameRepository.findAllByBlackPlayerUUIDIsNull();
    }

    @Override
    public Game getGame(Integer gameId) throws ResponseStatusException {
        return null;
    }

    @Override
    public List<Game> getGames() {
        return gameRepository.findAllByBlackPlayerUUIDIsNullAndGameTypeEquals(
                GameType.PLAYER_PLAYER_CLASSIC
        );
    }

    @Override
    public Game createGame(UUID whiteUUID) {
        return null;
    }

    @Override
    public boolean checkGame(Integer gameId) {
        return false;
    }

    @Override
    public void refreshGameTimer(Integer gameId) {
    }

    @Override
    public Game enterGame(UUID blackUUID, Integer gameId) {
        return null;
    }

    @Override
    public List<MoveDto> getPossibleMoves(GameDto game, PieceDto piece, UUID playerUUID) {
        return null;
    }

    @Override
    public GameStatus getGameStatus(Integer gameId) {
        return null;
    }

    @Override
    public Game movePiece(Game game,
                          UUID playerUUID,
                          Piece piece,
                          MoveDto moveTypePairPair) throws ResponseStatusException {

        return null;

    }

}
