package br.com.eterniaserver.xadrez.domain.service;

import br.com.eterniaserver.xadrez.domain.entities.Game;
import br.com.eterniaserver.xadrez.domain.enums.GameDifficulty;
import br.com.eterniaserver.xadrez.domain.enums.GameType;
import br.com.eterniaserver.xadrez.domain.repositories.GameRepository;
import br.com.eterniaserver.xadrez.domain.service.impl.ClassicPIAGameServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

@ExtendWith(MockitoExtension.class)
class ClassicPIAGameServiceImplUnitTest {

    @Mock
    private GameRepository gameRepository;
    @Mock
    private BoardService boardService;

    private GameService gameService;

    @BeforeEach
    void init() {
        gameService = new ClassicPIAGameServiceImpl(gameRepository, boardService);
    }

    @Test
    void testCreateGame() {
        UUID uuid = UUID.randomUUID();

        Game game = gameService.createGame(uuid);

        Assertions.assertEquals(GameType.PLAYER_IA_CLASSIC, game.getGameType());
        Assertions.assertEquals(GameDifficulty.NORMAL, game.getGameDifficulty());
        Assertions.assertTrue(game.getWhiteTurn());
        Assertions.assertEquals(uuid, game.getWhitePlayerUUID());
        Assertions.assertNull(game.getBlackPlayerUUID());
        Assertions.assertEquals(0, game.getWhiteMoves());
        Assertions.assertEquals(0, game.getBlackMoves());
    }

    @Test
    void testRefreshGameTimer() {
        Integer gameId = 1;
        Game game = Mockito.mock(Game.class);

        Mockito.when(gameRepository.findById(gameId)).thenReturn(Optional.of(game));

        gameService.refreshGameTimer(gameId);

        Mockito.verify(game, Mockito.times(1)).setTimer(Mockito.anyLong());
    }

    @Test
    void testCheckGameShouldReturnTrue() {
        Integer gameId = 1;

        Mockito.when(gameRepository.existsById(gameId)).thenReturn(true);

        boolean result = gameService.checkGame(gameId);

        Assertions.assertTrue(result);
    }

    @Test
    void testCheckGameShouldReturnFalse() {
        Integer gameId = 1;

        Mockito.when(gameRepository.existsById(gameId)).thenReturn(false);

        boolean result = gameService.checkGame(gameId);

        Assertions.assertFalse(result);
    }

}
