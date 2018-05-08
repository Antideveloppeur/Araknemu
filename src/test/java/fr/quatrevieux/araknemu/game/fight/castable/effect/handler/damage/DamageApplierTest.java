package fr.quatrevieux.araknemu.game.fight.castable.effect.handler.damage;

import fr.quatrevieux.araknemu.data.constant.Characteristic;
import fr.quatrevieux.araknemu.game.fight.FightBaseCase;
import fr.quatrevieux.araknemu.game.fight.castable.effect.Element;
import fr.quatrevieux.araknemu.game.fight.fighter.player.PlayerFighter;
import fr.quatrevieux.araknemu.game.spell.effect.SpellEffect;
import fr.quatrevieux.araknemu.network.game.out.fight.action.ActionEffect;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.*;

class DamageApplierTest extends FightBaseCase {
    private PlayerFighter caster;
    private PlayerFighter target;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        createFight().nextState();

        caster = player.fighter();
        target = other.fighter();

        requestStack.clear();
    }

    @Test
    void applyFixedWithoutBoost() {
        SpellEffect effect = Mockito.mock(SpellEffect.class);

        Mockito.when(effect.min()).thenReturn(10);

        DamageApplier applier = new DamageApplier(caster, effect, Element.AIR);

        int value = applier.apply(target);

        assertEquals(-10, value);
        assertEquals(10, target.life().max() - target.life().current());

        requestStack.assertLast(ActionEffect.alterLifePoints(caster, target, -10));
    }

    @Test
    void applyRandomWithoutBoost() {
        SpellEffect effect = Mockito.mock(SpellEffect.class);

        Mockito.when(effect.min()).thenReturn(10);
        Mockito.when(effect.max()).thenReturn(15);

        DamageApplier applier = new DamageApplier(caster, effect, Element.AIR);

        int value = applier.apply(target);

        assertBetween(-15, -10, value);
        assertEquals(value, target.life().current() - target.life().max());

        requestStack.assertLast(ActionEffect.alterLifePoints(caster, target, value));
    }

    @Test
    void applyWithBoost() {
        SpellEffect effect = Mockito.mock(SpellEffect.class);

        Mockito.when(effect.min()).thenReturn(10);

        DamageApplier applier = new DamageApplier(caster, effect, Element.AIR);

        player.characteristics().base().set(Characteristic.AGILITY, 50);
        player.characteristics().base().set(Characteristic.PERCENT_DAMAGE, 25);
        player.characteristics().base().set(Characteristic.FIXED_DAMAGE, 10);

        int value = applier.apply(target);

        assertEquals(-27, value);
    }

    @Test
    void applyWithResistance() {
        SpellEffect effect = Mockito.mock(SpellEffect.class);

        Mockito.when(effect.min()).thenReturn(10);

        DamageApplier applier = new DamageApplier(caster, effect, Element.AIR);

        other.characteristics().base().set(Characteristic.RESISTANCE_PERCENT_AIR, 25);
        other.characteristics().base().set(Characteristic.RESISTANCE_AIR, 5);

        int value = applier.apply(target);

        assertEquals(-2, value);
    }

    @Test
    void applyWithTooHighResistance() {
        SpellEffect effect = Mockito.mock(SpellEffect.class);

        Mockito.when(effect.min()).thenReturn(10);

        DamageApplier applier = new DamageApplier(caster, effect, Element.AIR);

        other.characteristics().base().set(Characteristic.RESISTANCE_AIR, 100);

        int value = applier.apply(target);

        assertEquals(0, value);
    }

    @Test
    void applyWithValueHigherThanTargetLife() {
        SpellEffect effect = Mockito.mock(SpellEffect.class);

        Mockito.when(effect.min()).thenReturn(1000);

        DamageApplier applier = new DamageApplier(caster, effect, Element.AIR);

        int value = applier.apply(target);

        assertEquals(-50, value);
        assertTrue(target.dead());

        requestStack.assertLast(ActionEffect.fighterDie(caster, target));
    }
}
