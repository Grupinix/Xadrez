package br.com.eterniaserver.xadrez.domain.schedule;

import br.com.eterniaserver.xadrez.domain.entities.Game;
import br.com.eterniaserver.xadrez.domain.enums.GameType;
import br.com.eterniaserver.xadrez.domain.repositories.GameRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@ExtendWith(MockitoExtension.class)
class GameTimerScheduleUnitTest {

    @Mock
    private GameRepository gameRepository;
    private GameTimerScheduler gameTimerScheduler;

    @BeforeEach
    void init() {
        gameTimerScheduler = new GameTimerScheduler(gameRepository);
    }

    @Test
    void testShouldNotDeleteIaGame() {
        Game game = Mockito.mock(Game.class);

        Mockito.when(game.getTimer()).thenReturn(System.currentTimeMillis());
        Mockito.when(game.getGameType()).thenReturn(GameType.PLAYER_IA_CLASSIC);
        Mockito.when(game.getBlackPlayerUUID()).thenReturn(null);

        List<Game> gameList = List.of(game);

        Mockito.when(gameRepository.findAll()).thenReturn(gameList);

        gameTimerScheduler.run();

        Mockito.verify(gameRepository, Mockito.times(0)).delete(game);
    }

    @Test
    void testShouldDeleteIaGame() {
        Game game = Mockito.mock(Game.class);

        long currentTimeMillis = System.currentTimeMillis();
        long gameTimer = currentTimeMillis - TimeUnit.SECONDS.toMillis(901);

        Mockito.when(game.getTimer()).thenReturn(gameTimer);
        Mockito.when(game.getGameType()).thenReturn(GameType.PLAYER_IA_CLASSIC);
        Mockito.when(game.getBlackPlayerUUID()).thenReturn(null);

        List<Game> gameList = List.of(game);

        Mockito.when(gameRepository.findAll()).thenReturn(gameList);

        gameTimerScheduler.run();

        Mockito.verify(gameRepository, Mockito.times(1)).delete(game);
    }

    @Test
    void testShouldNotDeletePlayerGameLobby() {
        Game game = Mockito.mock(Game.class);

        Mockito.when(game.getTimer()).thenReturn(System.currentTimeMillis());
        Mockito.when(game.getGameType()).thenReturn(GameType.PLAYER_PLAYER_CLASSIC);
        Mockito.when(game.getBlackPlayerUUID()).thenReturn(null);

        List<Game> gameList = List.of(game);

        Mockito.when(gameRepository.findAll()).thenReturn(gameList);

        gameTimerScheduler.run();

        Mockito.verify(gameRepository, Mockito.times(0)).delete(game);
    }

    @Test
    void testShouldDeletePlayerGameLobby() {
        Game game = Mockito.mock(Game.class);

        long currentTimeMillis = System.currentTimeMillis();
        long gameTimer = currentTimeMillis - TimeUnit.SECONDS.toMillis(21);

        Mockito.when(game.getTimer()).thenReturn(gameTimer);
        Mockito.when(game.getGameType()).thenReturn(GameType.PLAYER_PLAYER_CLASSIC);
        Mockito.when(game.getBlackPlayerUUID()).thenReturn(null);

        List<Game> gameList = List.of(game);

        Mockito.when(gameRepository.findAll()).thenReturn(gameList);

        gameTimerScheduler.run();

        Mockito.verify(gameRepository, Mockito.times(1)).delete(game);
    }

    @Test
    void testShouldNotDeletePlayerGame() {
        Game game = Mockito.mock(Game.class);

        Mockito.when(game.getTimer()).thenReturn(System.currentTimeMillis());
        Mockito.when(game.getGameType()).thenReturn(GameType.PLAYER_PLAYER_CLASSIC);
        Mockito.when(game.getBlackPlayerUUID()).thenReturn(UUID.randomUUID());

        List<Game> gameList = List.of(game);

        Mockito.when(gameRepository.findAll()).thenReturn(gameList);

        gameTimerScheduler.run();

        Mockito.verify(gameRepository, Mockito.times(0)).delete(game);
    }

    @Test
    void testShouldDeletePlayerGame() {
        Game game = Mockito.mock(Game.class);

        long currentTimeMillis = System.currentTimeMillis();
        long gameTimer = currentTimeMillis - TimeUnit.SECONDS.toMillis(601);

        Mockito.when(game.getTimer()).thenReturn(gameTimer);
        Mockito.when(game.getGameType()).thenReturn(GameType.PLAYER_PLAYER_CLASSIC);
        Mockito.when(game.getBlackPlayerUUID()).thenReturn(UUID.randomUUID());

        List<Game> gameList = List.of(game);

        Mockito.when(gameRepository.findAll()).thenReturn(gameList);

        gameTimerScheduler.run();

        Mockito.verify(gameRepository, Mockito.times(1)).delete(game);
    }

}
