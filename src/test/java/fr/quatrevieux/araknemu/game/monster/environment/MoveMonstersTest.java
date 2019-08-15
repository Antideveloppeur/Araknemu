package fr.quatrevieux.araknemu.game.monster.environment;

import fr.quatrevieux.araknemu.data.value.Position;
import fr.quatrevieux.araknemu.data.world.entity.monster.MonsterGroupPosition;
import fr.quatrevieux.araknemu.game.GameBaseCase;
import fr.quatrevieux.araknemu.game.activity.ActivityService;
import fr.quatrevieux.araknemu.game.exploration.interaction.action.ActionType;
import fr.quatrevieux.araknemu.game.exploration.map.ExplorationMap;
import fr.quatrevieux.araknemu.game.exploration.map.ExplorationMapService;
import fr.quatrevieux.araknemu.game.exploration.map.cell.ExplorationMapCell;
import fr.quatrevieux.araknemu.game.monster.group.MonsterGroup;
import fr.quatrevieux.araknemu.network.game.out.game.action.GameActionResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.helpers.NOPLogger;

import java.sql.SQLException;
import java.time.Duration;
import java.util.Collection;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

class MoveMonstersTest extends GameBaseCase {
    private MoveMonsters task;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        dataSet
            .pushMonsterTemplates()
            .pushMonsterSpells()
            .pushMonsterGroups()
            .pushMaps()
        ;

        task = new MoveMonsters(
            container.get(MonsterEnvironmentService.class),
            Duration.ofSeconds(10),
            100
        );
    }

    @Test
    void getters() {
        assertEquals(Duration.ofSeconds(10), task.delay());
        assertFalse(task.retry(container.get(ActivityService.class)));
        assertEquals("Move monsters", task.toString());
    }

    @Test
    void executeWithoutMonsters() {
        task.execute(NOPLogger.NOP_LOGGER);
    }

    @Test
    void moveSingleGroup() throws SQLException {
        dataSet.pushMonsterGroupPosition(new MonsterGroupPosition(new Position(10340, -1), 3));
        ExplorationMap map = container.get(ExplorationMapService.class).load(10340);

        explorationPlayer().join(map);
        requestStack.clear();

        MonsterGroup group = container.get(MonsterEnvironmentService.class).byMap(10340).stream().findFirst().get().available().get(0);
        ExplorationMapCell lastCell = group.cell();

        task.execute(NOPLogger.NOP_LOGGER);

        assertNotEquals(lastCell, group.cell());
        requestStack.assertLast(new GameActionResponse("", ActionType.MOVE, group.id(), "ab-fbVha3"));
    }

    @Test
    void fixedGroupShouldNotMove() throws SQLException {
        dataSet.pushMonsterGroupPosition(new MonsterGroupPosition(new Position(10340, 123), 3));
        ExplorationMap map = container.get(ExplorationMapService.class).load(10340);

        explorationPlayer().join(map);
        requestStack.clear();

        MonsterGroup group = container.get(MonsterEnvironmentService.class).byMap(10340).stream().findFirst().get().available().get(0);

        task.execute(NOPLogger.NOP_LOGGER);

        assertEquals(map.get(123), group.cell());
        requestStack.assertEmpty();
    }

    @Test
    void moveOnlyOneGroupPerMap() throws SQLException {
        dataSet.pushMonsterGroupPosition(new MonsterGroupPosition(new Position(10340, -1), 2));
        ExplorationMap map = container.get(ExplorationMapService.class).load(10340);

        explorationPlayer().join(map);
        requestStack.clear();

        LivingMonsterGroupPosition position = container.get(MonsterEnvironmentService.class).byMap(10340).stream().findFirst().get();
        Collection<ExplorationMapCell> baseCells = position.available().stream().map(MonsterGroup::cell).collect(Collectors.toList());

        task.execute(NOPLogger.NOP_LOGGER);

        // Diff on current cells
        baseCells.removeAll(position.available().stream().map(MonsterGroup::cell).collect(Collectors.toList()));
        assertCount(1, baseCells); // Count the changed cells
    }

    @Test
    void moveChance() throws SQLException {
        task = new MoveMonsters(container.get(MonsterEnvironmentService.class), Duration.ofSeconds(10), 25);

        dataSet.pushMonsterGroupPosition(new MonsterGroupPosition(new Position(10340, -1), 3));
        container.get(ExplorationMapService.class).load(10340);

        MonsterGroup group = container.get(MonsterEnvironmentService.class).byMap(10340).stream().findFirst().get().available().get(0);

        int moveCount = 0;

        for (int i = 0; i < 100; ++i) {
            ExplorationMapCell lastCell = group.cell();

            task.execute(NOPLogger.NOP_LOGGER);

            if (!group.cell().equals(lastCell)) {
                ++moveCount;
            }
        }

        assertBetween(15, 35, moveCount);
    }
}