package br.com.eterniaserver.xadrez.domain.service;

import br.com.eterniaserver.xadrez.domain.enums.GameDifficulty;
import br.com.eterniaserver.xadrez.domain.service.impl.PlayerServiceImpl;
import br.com.eterniaserver.xadrez.rest.dtos.PlayerDto;
import org.junit.jupiter.api.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;

class PlayerServiceImplUnitTest {

    private static final String VALID = "8AFSD35D";
    private static final String VALID_2 = "8A4SD35D";
    private static final String INVALID = "8AFSD";

    private PlayerService playerService;

    @BeforeEach
    void init() {
        playerService = new PlayerServiceImpl();
    }

    @Test
    void shouldRegister() {
        PlayerDto playerDto = playerService.register(VALID);
        Assertions.assertNotNull(playerDto);
    }

    @Test
    void shouldRaiseExceptionWhenIdentifyLengthIsnt8() {
        Assertions.assertThrows(ResponseStatusException.class, () -> playerService.register(INVALID));
    }

    @Nested
    class TestGetFromUUID {

        private PlayerDto registered;

        @BeforeEach
        void init() {
            registered = playerService.register(VALID);
        }

        @Test
        void shouldGetFromUUIDWhenGetFromRegisteredUUID() {
            PlayerDto playerDto = playerService.getFromUUID(registered.getUuid());

            Assertions.assertNotNull(playerDto);
        }

        @Test
        void shouldRaiseExceptionWheGetFromUnregisteredUUID() {
            final UUID uuid = UUID.randomUUID();
            Assertions.assertThrows(ResponseStatusException.class, () -> playerService.getFromUUID(uuid));
        }

    }

    @Test
    void verifyShouldReturnTrueWhenNotRegistered() {
        PlayerDto playerDto = new PlayerDto();
        playerDto.setUuid(UUID.randomUUID());
        playerDto.setIdentifier(VALID);

        Assertions.assertTrue(playerService.verify(playerDto));
    }

    @Test
    void verifyShouldReturnTrueWhenRegisteredWithSameInfos() {
        PlayerDto playerRegistered = playerService.register(VALID);

        PlayerDto playerDto = new PlayerDto();
        playerDto.setIdentifier(VALID);
        playerDto.setUuid(playerRegistered.getUuid());

        Assertions.assertTrue(playerService.verify(playerDto));
    }

    @Test
    void verifyShouldReturnFalseWhenRegisteredWithDifferentIdentifier() {
        PlayerDto playerRegistered = playerService.register(VALID);

        PlayerDto playerDto = new PlayerDto();
        playerDto.setIdentifier(VALID_2);
        playerDto.setUuid(playerRegistered.getUuid());

        Assertions.assertFalse(playerService.verify(playerDto));
    }

    @Test
    void verifyPlayerDifficulty() {
        PlayerDto playerRegistered = playerService.register(VALID);

        playerService.setGameDifficulty(playerRegistered.getUuid(), GameDifficulty.NORMAL);
        GameDifficulty gameDifficulty = playerService.getGameDifficulty(playerRegistered.getUuid());

        Assertions.assertEquals(GameDifficulty.NORMAL, gameDifficulty);
    }


}
