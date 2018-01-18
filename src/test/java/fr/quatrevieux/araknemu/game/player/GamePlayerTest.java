package fr.quatrevieux.araknemu.game.player;

import fr.quatrevieux.araknemu.core.di.ContainerException;
import fr.quatrevieux.araknemu.data.constant.Characteristic;
import fr.quatrevieux.araknemu.data.constant.Race;
import fr.quatrevieux.araknemu.data.constant.Sex;
import fr.quatrevieux.araknemu.data.living.entity.account.Account;
import fr.quatrevieux.araknemu.data.living.entity.player.Player;
import fr.quatrevieux.araknemu.data.value.Colors;
import fr.quatrevieux.araknemu.data.value.Position;
import fr.quatrevieux.araknemu.data.world.entity.character.PlayerRace;
import fr.quatrevieux.araknemu.game.GameBaseCase;
import fr.quatrevieux.araknemu.game.account.AccountService;
import fr.quatrevieux.araknemu.game.account.GameAccount;
import fr.quatrevieux.araknemu.game.exploration.ExplorationPlayer;
import fr.quatrevieux.araknemu.game.world.creature.characteristics.DefaultCharacteristics;
import fr.quatrevieux.araknemu.game.world.creature.characteristics.MutableCharacteristics;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class GamePlayerTest extends GameBaseCase {
    private GamePlayer player;
    private Player entity;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        dataSet
            .pushMaps()
            .pushRaces()
        ;

        MutableCharacteristics characteristics = new DefaultCharacteristics();

        characteristics.set(Characteristic.INTELLIGENCE, 150);
        characteristics.set(Characteristic.VITALITY, 50);

        entity = dataSet.push(new Player(5, 2, 1, "Other", Race.CRA, Sex.MALE, new Colors(-1, -1, -1), 50, characteristics, new Position(10300, 308)));

        player = new GamePlayer(
            new GameAccount(
                new Account(2),
                container.get(AccountService.class),
                1
            ),
            entity,
            dataSet.refresh(new PlayerRace(Race.CRA)),
            session,
            container.get(PlayerService.class)
        );
    }

    @Test
    void send() {
        player.send("test");

        requestStack.assertLast("test");
    }

    @Test
    void characteristics() {
        assertEquals(150, player.characteristics().get(Characteristic.INTELLIGENCE));
        assertEquals(1, player.characteristics().get(Characteristic.INITIATIVE));
        assertEquals(6, player.characteristics().get(Characteristic.ACTION_POINT));
    }

    @Test
    void position() {
        assertEquals(new Position(10300, 308), player.position());
    }

    @Test
    void isExploring() {
        assertFalse(player.isExploring());

        session.setExploration(
            new ExplorationPlayer(player)
        );

        assertTrue(player.isExploring());
    }

    @Test
    void explorationNotExploring() {
        assertThrows(IllegalStateException.class, () -> player.exploration(), "The current player is not an exploration state");
    }

    @Test
    void exploration() {
        session.setExploration(
            new ExplorationPlayer(player)
        );

        assertSame(session.exploration(), player.exploration());
    }

    @Test
    void save() throws ContainerException {
        player.setPosition(
            new Position(7894, 12)
        );

        player.characteristics().set(Characteristic.AGILITY, 123);

        player.save();

        assertEquals(new Position(7894, 12), dataSet.refresh(entity).position());
        assertEquals(123, player.characteristics().get(Characteristic.AGILITY));
    }
}
