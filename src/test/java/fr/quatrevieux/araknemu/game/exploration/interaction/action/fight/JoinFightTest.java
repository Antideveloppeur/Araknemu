package fr.quatrevieux.araknemu.game.exploration.interaction.action.fight;

import fr.quatrevieux.araknemu.core.di.ContainerException;
import fr.quatrevieux.araknemu.game.exploration.ExplorationPlayer;
import fr.quatrevieux.araknemu.game.exploration.interaction.action.ActionQueue;
import fr.quatrevieux.araknemu.game.exploration.interaction.action.ActionType;
import fr.quatrevieux.araknemu.game.exploration.interaction.action.BlockingAction;
import fr.quatrevieux.araknemu.game.exploration.map.ExplorationMap;
import fr.quatrevieux.araknemu.game.exploration.map.ExplorationMapService;
import fr.quatrevieux.araknemu.game.fight.Fight;
import fr.quatrevieux.araknemu.game.fight.FightBaseCase;
import fr.quatrevieux.araknemu.game.fight.JoinFightError;
import fr.quatrevieux.araknemu.game.fight.exception.JoinFightException;
import fr.quatrevieux.araknemu.game.fight.fighter.Fighter;
import fr.quatrevieux.araknemu.game.fight.fighter.player.PlayerFighter;
import fr.quatrevieux.araknemu.network.game.out.game.AddSprites;
import fr.quatrevieux.araknemu.network.game.out.game.FightStartPositions;
import fr.quatrevieux.araknemu.network.game.out.game.action.GameActionResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

class JoinFightTest extends FightBaseCase {
    private Fight fight;
    private JoinFight action;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        ExplorationMap map = container.get(ExplorationMapService.class).load(10340);
        explorationPlayer().join(map);

        fight = createSimpleFight(map);
        action = new JoinFight(explorationPlayer(), fight, fight.team(0));

        requestStack.clear();
    }

    @Test
    void busy() throws SQLException, ContainerException {
        explorationPlayer().interactions().push(new MyBlockingAction());
        assertTrue(explorationPlayer().interactions().busy());

        action.start(new ActionQueue());

        requestStack.assertLast(
            new GameActionResponse("", ActionType.JOIN_FIGHT, player.id() + "", new Object[] {"o"})
        );
    }

    @Test
    void tooLate() {
        fight.nextState();

        action.start(new ActionQueue());

        requestStack.assertLast(
            new GameActionResponse("", ActionType.JOIN_FIGHT, player.id() + "", new Object[] {"l"})
        );
    }

    @Test
    void badMap() throws SQLException, ContainerException {
        explorationPlayer().join(container.get(ExplorationMapService.class).load(10540));

        action.start(new ActionQueue());

        requestStack.assertLast(
            new GameActionResponse("", ActionType.JOIN_FIGHT, player.id() + "", new Object[] {"p"})
        );
    }

    @Test
    void fullTeam() throws SQLException, ContainerException, JoinFightException, InterruptedException {
        for (int i = 10; fight.team(0).fighters().size() < fight.team(0).startPlaces().size(); ++i) {
            fight.team(0).join(new PlayerFighter(makeSimpleGamePlayer(i)));
        }

        action.start(new ActionQueue());
        Thread.sleep(5);

        requestStack.assertLast(
            new GameActionResponse("", ActionType.JOIN_FIGHT, player.id() + "", new Object[] {"t"})
        );
    }

    @Test
    void success() throws InterruptedException {
        action.start(new ActionQueue());
        Thread.sleep(5);

        assertTrue(player.isFighting());
        assertFalse(player.isExploring());
        assertSame(fight, player.fighter().fight());
        assertContains(player.fighter().cell().id(), fight.team(0).startPlaces());
        assertContains(player.fighter(), fight.team(0).fighters());

        requestStack.assertAll(
            new fr.quatrevieux.araknemu.network.game.out.fight.JoinFight(fight),
            new AddSprites(fight.fighters().stream().map(Fighter::sprite).collect(Collectors.toList())),
            new FightStartPositions(new List[] { fight.team(0).startPlaces(), fight.team(1).startPlaces() }, 0),
            new AddSprites(Collections.singleton(player.fighter().sprite()))
        );
    }

    public static class MyBlockingAction implements BlockingAction {
        private int id;

        @Override
        public void cancel(String argument) { }

        @Override
        public void end() { }

        @Override
        public int id() {
            return id;
        }

        @Override
        public void setId(int id) {
            this.id = id;
        }

        @Override
        public void start(ActionQueue queue) {
            queue.setPending(this);
        }

        @Override
        public ExplorationPlayer performer() {
            return null;
        }

        @Override
        public ActionType type() {
            return null;
        }

        @Override
        public Object[] arguments() {
            return new Object[0];
        }
    }
}
