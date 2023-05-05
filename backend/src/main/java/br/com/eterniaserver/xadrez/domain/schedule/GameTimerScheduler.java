package br.com.eterniaserver.xadrez.domain.schedule;

import br.com.eterniaserver.xadrez.domain.entities.Game;
import br.com.eterniaserver.xadrez.domain.enums.GameType;
import br.com.eterniaserver.xadrez.domain.repositories.GameRepository;
import lombok.AllArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Component
@AllArgsConstructor
public class GameTimerScheduler {

    private static final int LOBBY_GAME_TTL = 20;
    private static final int PP_GAME_TTL = 600;
    private static final int PIA_GAME_TTL = 900;

    private final GameRepository gameRepository;

    @Scheduled(cron = "0 */1 * * * *")
    public void run() {
        List<Game> gameList = gameRepository.findAll();

        for (Game game : gameList) {
            long gameTimer = game.getTimer();
            long actualMillis = System.currentTimeMillis();

            long seconds = TimeUnit.MILLISECONDS.toSeconds(actualMillis - gameTimer);

            boolean isIaGame = game.getGameType() == GameType.PLAYER_IA_CLASSIC;
            boolean hasSecondPlayer = game.getBlackPlayerUUID() != null;

            boolean iaRemoveRule = isIaGame && seconds > PIA_GAME_TTL;
            boolean gameRemoveRule = !isIaGame && hasSecondPlayer && seconds > PP_GAME_TTL;
            boolean lobbyRemoveRule = !isIaGame && !hasSecondPlayer && seconds > LOBBY_GAME_TTL;

            if (iaRemoveRule || gameRemoveRule || lobbyRemoveRule) {
                gameRepository.delete(game);
            }
        }
    }

}
