package br.com.eterniaserver.xadrez.domain.schedule;

import br.com.eterniaserver.xadrez.domain.entities.Game;
import br.com.eterniaserver.xadrez.domain.entities.Rank;
import br.com.eterniaserver.xadrez.domain.enums.GameStatus;
import br.com.eterniaserver.xadrez.domain.enums.GameType;
import br.com.eterniaserver.xadrez.domain.repositories.GameRepository;
import br.com.eterniaserver.xadrez.domain.repositories.RankRepository;
import br.com.eterniaserver.xadrez.domain.service.PlayerService;
import br.com.eterniaserver.xadrez.rest.dtos.PlayerDto;
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
    @Mock
    private RankRepository rankRepository;
    @Mock
    private PlayerService playerService;
    private GameTimerScheduler gameTimerScheduler;

    @BeforeEach
    void init() {
        gameTimerScheduler = new GameTimerScheduler(gameRepository, rankRepository, playerService);
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
        Mockito.when(game.getGameStatus()).thenReturn(GameStatus.NORMAL);
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
        Mockito.when(game.getGameStatus()).thenReturn(GameStatus.NORMAL);
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
        Mockito.when(game.getGameStatus()).thenReturn(GameStatus.NORMAL);
        Mockito.when(game.getBlackPlayerUUID()).thenReturn(UUID.randomUUID());

        List<Game> gameList = List.of(game);

        Mockito.when(gameRepository.findAll()).thenReturn(gameList);

        gameTimerScheduler.run();

        Mockito.verify(gameRepository, Mockito.times(1)).delete(game);
    }

    @Test
    void testShouldDeletePlayerGameWinningWhite() {
        Game game = Mockito.mock(Game.class);
        PlayerDto playerDto = Mockito.mock(PlayerDto.class);

        long currentTimeMillis = System.currentTimeMillis();
        long gameTimer = currentTimeMillis - TimeUnit.SECONDS.toMillis(601);
        UUID whiteUUID = UUID.randomUUID();

        Mockito.when(playerDto.getIdentifier()).thenReturn("playasd2");

        Mockito.when(game.getTimer()).thenReturn(gameTimer);
        Mockito.when(game.getGameType()).thenReturn(GameType.PLAYER_PLAYER_CLASSIC);
        Mockito.when(game.getGameStatus()).thenReturn(GameStatus.WHITE_WINS);
        Mockito.when(game.getWhitePlayerUUID()).thenReturn(whiteUUID);
        Mockito.when(game.getBlackPlayerUUID()).thenReturn(UUID.randomUUID());

        Mockito.when(playerService.getFromUUID(whiteUUID)).thenReturn(playerDto);

        List<Game> gameList = List.of(game);

        Mockito.when(gameRepository.findAll()).thenReturn(gameList);

        gameTimerScheduler.run();

        Mockito.verify(gameRepository, Mockito.times(1)).delete(game);
        Mockito.verify(rankRepository, Mockito.times(1)).save(Mockito.any(Rank.class));
    }

    @Test
    void testShouldDeletePlayerGameWinningBlack() {
        Game game = Mockito.mock(Game.class);
        PlayerDto playerDto = Mockito.mock(PlayerDto.class);

        long currentTimeMillis = System.currentTimeMillis();
        long gameTimer = currentTimeMillis - TimeUnit.SECONDS.toMillis(601);
        UUID blackUUID = UUID.randomUUID();

        Mockito.when(playerDto.getIdentifier()).thenReturn("playasd2");

        Mockito.when(game.getTimer()).thenReturn(gameTimer);
        Mockito.when(game.getGameType()).thenReturn(GameType.PLAYER_PLAYER_CLASSIC);
        Mockito.when(game.getGameStatus()).thenReturn(GameStatus.BLACK_WINS);
        Mockito.when(game.getBlackPlayerUUID()).thenReturn(blackUUID);

        Mockito.when(playerService.getFromUUID(blackUUID)).thenReturn(playerDto);

        List<Game> gameList = List.of(game);

        Mockito.when(gameRepository.findAll()).thenReturn(gameList);

        gameTimerScheduler.run();

        Mockito.verify(gameRepository, Mockito.times(1)).delete(game);
        Mockito.verify(rankRepository, Mockito.times(1)).save(Mockito.any(Rank.class));
    }

}
