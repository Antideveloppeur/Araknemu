package fr.quatrevieux.araknemu.game.fight.castable.validator;

import fr.quatrevieux.araknemu.data.constant.Characteristic;
import fr.quatrevieux.araknemu.data.value.Interval;
import fr.quatrevieux.araknemu.game.fight.Fight;
import fr.quatrevieux.araknemu.game.fight.FightBaseCase;
import fr.quatrevieux.araknemu.game.fight.fighter.Fighter;
import fr.quatrevieux.araknemu.game.fight.turn.FightTurn;
import fr.quatrevieux.araknemu.game.spell.Spell;
import fr.quatrevieux.araknemu.game.spell.SpellConstraints;
import fr.quatrevieux.araknemu.network.game.out.info.Error;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;

class RangeValidatorTest  extends FightBaseCase {
    private Fight fight;
    private Fighter fighter;
    private FightTurn turn;
    private RangeValidator validator;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        fight = createFight();
        fighter = player.fighter();
        turn = new FightTurn(fighter, fight, Duration.ofSeconds(30));
        turn.start();

        fighter.move(fight.map().get(185));

        validator = new RangeValidator();
    }

    @Test
    void valid() {
        Spell spell = Mockito.mock(Spell.class);
        SpellConstraints constraints = Mockito.mock(SpellConstraints.class);

        Mockito.when(spell.constraints()).thenReturn(constraints);
        Mockito.when(constraints.range()).thenReturn(new Interval(1, 10));

        assertNull(validator.validate(turn, spell, fight.map().get(186)));
    }

    @Test
    void tooAway() {
        Spell spell = Mockito.mock(Spell.class);
        SpellConstraints constraints = Mockito.mock(SpellConstraints.class);

        Mockito.when(spell.constraints()).thenReturn(constraints);
        Mockito.when(constraints.range()).thenReturn(new Interval(1, 10));

        assertEquals(
            Error.cantCastBadRange(new Interval(1, 10), 21).toString(),
            validator.validate(turn, spell, fight.map().get(102)).toString()
        );
    }

    @Test
    void tooNear() {
        Spell spell = Mockito.mock(Spell.class);
        SpellConstraints constraints = Mockito.mock(SpellConstraints.class);

        Mockito.when(spell.constraints()).thenReturn(constraints);
        Mockito.when(constraints.range()).thenReturn(new Interval(1, 10));

        assertEquals(
            Error.cantCastBadRange(new Interval(1, 10), 0).toString(),
            validator.validate(turn, spell, fight.map().get(185)).toString()
        );
    }

    @Test
    void boostedRange() {
        Spell spell = Mockito.mock(Spell.class);
        SpellConstraints constraints = Mockito.mock(SpellConstraints.class);

        Mockito.when(spell.constraints()).thenReturn(constraints);
        Mockito.when(spell.modifiableRange()).thenReturn(true);
        Mockito.when(constraints.range()).thenReturn(new Interval(1, 10));

        fighter.characteristics().alter(Characteristic.SIGHT_BOOST, 15);

        assertNull(validator.validate(turn, spell, fight.map().get(102)));
    }

    @Test
    void rangeMalus() {
        Spell spell = Mockito.mock(Spell.class);
        SpellConstraints constraints = Mockito.mock(SpellConstraints.class);

        Mockito.when(spell.constraints()).thenReturn(constraints);
        Mockito.when(spell.modifiableRange()).thenReturn(true);
        Mockito.when(constraints.range()).thenReturn(new Interval(1, 10));

        fighter.characteristics().alter(Characteristic.SIGHT_BOOST, -5);

        assertEquals(
            Error.cantCastBadRange(new Interval(1, 5), 9).toString(),
            validator.validate(turn, spell, fight.map().get(195)).toString()
        );
    }
}