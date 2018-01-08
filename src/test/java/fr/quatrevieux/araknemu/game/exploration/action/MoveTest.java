package fr.quatrevieux.araknemu.game.exploration.action;

import fr.quatrevieux.araknemu.game.GameBaseCase;
import fr.quatrevieux.araknemu.game.exploration.map.ExplorationMapService;
import fr.quatrevieux.araknemu.game.player.GamePlayer;
import fr.quatrevieux.araknemu.network.game.out.game.action.GameActionResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

class MoveTest extends GameBaseCase {
    private GamePlayer player;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        dataSet.pushMaps();

        player = gamePlayer();
        player.join(
            container.get(ExplorationMapService.class).load(10300)
        );
    }

    @Test
    void success() throws Exception {
        Move move = new Move(
            1,
            player,
            player.map().decoder().decodePath("bftdgl", 279)
        );

        player.actionQueue().push(move);

        requestStack.assertLast(
            new GameActionResponse(
                1,
                ActionType.MOVE,
                gamePlayer().id(),
                "aexbftdgl"
            )
        );

        assertTrue(player.actionQueue().isBusy());

        player.actionQueue().end(1);
        assertFalse(player.actionQueue().isBusy());

        assertEquals(395, player.position().cell());
    }

    @Test
    void invalidPath() {
        Move move = new Move(
            1,
            player,
            Collections.EMPTY_LIST
        );

        assertThrows(Exception.class, () -> player.actionQueue().push(move), "Empty path");
        assertFalse(player.actionQueue().isBusy());
    }
}