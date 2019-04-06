package fr.quatrevieux.araknemu.game.fight.ending.reward.generator;

import fr.quatrevieux.araknemu.data.world.entity.monster.MonsterRewardItem;
import fr.quatrevieux.araknemu.game.fight.Fight;
import fr.quatrevieux.araknemu.game.fight.FightBaseCase;
import fr.quatrevieux.araknemu.game.fight.ending.EndFightResults;
import fr.quatrevieux.araknemu.game.fight.ending.reward.FightRewardsSheet;
import fr.quatrevieux.araknemu.game.fight.ending.reward.RewardType;
import fr.quatrevieux.araknemu.game.fight.ending.reward.generator.compute.PvmItemDropFormula;
import fr.quatrevieux.araknemu.game.fight.ending.reward.generator.compute.PvmKamasFormula;
import fr.quatrevieux.araknemu.game.fight.ending.reward.generator.compute.PvmXpFormula;
import fr.quatrevieux.araknemu.game.fight.fighter.player.PlayerFighter;
import fr.quatrevieux.araknemu.game.player.characteristic.SpecialEffects;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

class PvmRewardsGeneratorTest extends FightBaseCase {
    private PvmRewardsGenerator generator;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        dataSet.pushRewardItem(new MonsterRewardItem(31, 8213, 1, 100, 100));
        dataSet.pushRewardItem(new MonsterRewardItem(34, 8219, 1, 100, 100));

        generator = new PvmRewardsGenerator(
            Collections.emptyList(),
            Collections.emptyList(),
            new PvmXpFormula(),
            new PvmKamasFormula(),
            new PvmItemDropFormula()
        );
    }

    @Test
    void generate() throws Exception {
        Fight fight = createPvmFight();
        fight.nextState();

        EndFightResults results = new EndFightResults(
            fight,
            new ArrayList<>(fight.team(0).fighters()),
            new ArrayList<>(fight.team(1).fighters())
        );

        FightRewardsSheet sheet = generator.generate(results);

        assertSame(results, sheet.results());
        assertCount(fight.fighters().size(), sheet.rewards());
        assertEquals(FightRewardsSheet.Type.NORMAL, sheet.type());

        assertEquals(RewardType.WINNER, sheet.rewards().get(0).type());
        assertEquals(player.fighter(), sheet.rewards().get(0).fighter());
        assertEquals(241, sheet.rewards().get(0).xp());
        assertBetween(100, 140, sheet.rewards().get(0).kamas());
        assertEquals(2, sheet.rewards().get(0).items().size());
        assertEquals(1, (int) sheet.rewards().get(0).items().get(8213));
        assertEquals(1, (int) sheet.rewards().get(0).items().get(8219));

        assertEquals(RewardType.LOOSER, sheet.rewards().get(1).type());
        assertEquals(0, sheet.rewards().get(1).xp());
        assertEquals(0, sheet.rewards().get(1).kamas());
        assertEquals(0, sheet.rewards().get(1).items().size());
    }

    @Test
    void generateShouldSortWinnersByDiscernment() throws Exception {
        player.properties().characteristics().specials().add(SpecialEffects.Type.DISCERNMENT, 100);

        Fight fight = createPvmFight();
        fight.nextState();

        PlayerFighter other = makePlayerFighter(this.other);

        EndFightResults results = new EndFightResults(
            fight,
            Arrays.asList(other, player.fighter()),
            new ArrayList<>()
        );

        FightRewardsSheet sheet = generator.generate(results);

        assertEquals(player.fighter(), sheet.rewards().get(0).fighter());
        assertEquals(other, sheet.rewards().get(1).fighter());
    }

    @Test
    void generateLooser() throws Exception {
        Fight fight = createPvmFight();
        fight.nextState();

        EndFightResults results = new EndFightResults(
            fight,
            Collections.emptyList(),
            Collections.singletonList(player.fighter())
        );

        FightRewardsSheet sheet = generator.generate(results);

        assertSame(results, sheet.results());
        assertCount(1, sheet.rewards());
        assertEquals(FightRewardsSheet.Type.NORMAL, sheet.type());

        assertEquals(RewardType.LOOSER, sheet.rewards().get(0).type());
        assertEquals(0, sheet.rewards().get(0).xp());
        assertEquals(0, sheet.rewards().get(0).kamas());
    }
}