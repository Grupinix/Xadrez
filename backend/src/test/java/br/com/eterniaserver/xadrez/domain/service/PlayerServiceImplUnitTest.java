package br.com.eterniaserver.xadrez.domain.service;

import br.com.eterniaserver.xadrez.domain.service.impl.PlayerServiceImpl;
import br.com.eterniaserver.xadrez.rest.dtos.PlayerDto;
import org.junit.jupiter.api.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;

class PlayerServiceImplUnitTest {

    private static final String VALID = "8AFSD35D";
    private static final String INVALID = "8AFSD";

    private PlayerService playerService;

    @BeforeEach
    void init() {
        playerService = new PlayerServiceImpl();
    }

    @Test
    void shouldRegister() {
        PlayerDto playerDto = playerService.registerPlayer(VALID);
        Assertions.assertNotNull(playerDto);
    }

    @Test
    void shouldRaiseExceptionWhenIdentifyLengthIsnt8() {
        Assertions.assertThrows(ResponseStatusException.class, () -> playerService.registerPlayer(INVALID));
    }

    @Nested
    class TestGetFromUUID {

        private PlayerDto registered;

        @BeforeEach
        void init() {
            registered = playerService.registerPlayer(VALID);
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

}
