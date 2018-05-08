package fr.quatrevieux.araknemu.game.fight.castable.effect;

import fr.quatrevieux.araknemu.game.fight.Fight;
import fr.quatrevieux.araknemu.game.fight.FightBaseCase;
import fr.quatrevieux.araknemu.game.spell.Spell;
import fr.quatrevieux.araknemu.game.spell.effect.SpellEffect;
import fr.quatrevieux.araknemu.network.game.out.fight.action.ActionEffect;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.*;

class EffectsHandlerTest extends FightBaseCase {
    private Fight fight;
    private EffectsHandler handler;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        fight = createFight();
        fight.nextState();
        handler = new EffectsHandler(fight);

        requestStack.clear();
    }

    @Test
    void applyUndefinedEffect() {
        SpellEffect effect = Mockito.mock(SpellEffect.class);
        Mockito.when(effect.effect()).thenReturn(-1);

        handler.apply(player.fighter(), Mockito.mock(Spell.class), effect, fight.map().get(123));

        requestStack.assertEmpty();
    }

    @Test
    void applyDamage() {
        SpellEffect effect = Mockito.mock(SpellEffect.class);

        Mockito.when(effect.effect()).thenReturn(100);
        Mockito.when(effect.min()).thenReturn(10);

        handler.apply(player.fighter(), Mockito.mock(Spell.class), effect, other.fighter().cell());

        requestStack.assertLast(ActionEffect.alterLifePoints(player.fighter(), other.fighter(), -15));
    }

    @Test
    void applyStealLife() {
        SpellEffect effect = Mockito.mock(SpellEffect.class);

        Mockito.when(effect.effect()).thenReturn(95);
        Mockito.when(effect.min()).thenReturn(10);

        player.fighter().life().alter(player.fighter(), -20);
        requestStack.clear();

        handler.apply(player.fighter(), Mockito.mock(Spell.class), effect, other.fighter().cell());

        requestStack.assertAll(
            ActionEffect.alterLifePoints(player.fighter(), other.fighter(), -15),
            ActionEffect.alterLifePoints(player.fighter(), player.fighter(), 7)
        );
    }
}
