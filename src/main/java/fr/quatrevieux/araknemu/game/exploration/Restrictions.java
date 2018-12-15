package fr.quatrevieux.araknemu.game.exploration;

import fr.quatrevieux.araknemu.game.exploration.event.RestrictionsChanged;
import fr.quatrevieux.araknemu.util.BitSet;

/**
 * Handle exploration player restrictions
 */
final public class Restrictions {
    static public enum Restriction {
        /** Value : 1 */
        DENY_ASSAULT,
        /** Value : 2 */
        DENY_CHALLENGE,
        /** Value : 4 */
        DENY_EXCHANGE,
        /** Value : 8 */
        DENY_ATTACK,
        /** Value : 16 */
        FORCE_WALK,
        /** Value : 32 */
        IS_SLOW,
        /** Value : 64 */
        DENY_CREATURE_MODE,
        /** Value : 128 */
        IS_TOMB,
    }

    final static private class LocalToExplorationMapping {
        interface Checker {
            public boolean check(fr.quatrevieux.araknemu.game.player.Restrictions restrictions);
        }

        final private Checker checker;
        final private Restriction restriction;

        public LocalToExplorationMapping(Checker checker, Restriction restriction) {
            this.checker = checker;
            this.restriction = restriction;
        }
    }

    final static private LocalToExplorationMapping[] LOCAL_TO_EXPLORATION_MAPPING = new LocalToExplorationMapping[] {
        new LocalToExplorationMapping(fr.quatrevieux.araknemu.game.player.Restrictions::canAssault,   Restriction.DENY_ASSAULT),
        new LocalToExplorationMapping(fr.quatrevieux.araknemu.game.player.Restrictions::canChallenge, Restriction.DENY_CHALLENGE),
        new LocalToExplorationMapping(fr.quatrevieux.araknemu.game.player.Restrictions::canAttack,    Restriction.DENY_ATTACK),
        new LocalToExplorationMapping(fr.quatrevieux.araknemu.game.player.Restrictions::canExchange,  Restriction.DENY_EXCHANGE),
    };

    final private ExplorationPlayer player;
    final private BitSet<Restriction> set;

    public Restrictions(ExplorationPlayer player) {
        this.player = player;
        this.set = new BitSet<>();
    }

    /**
     * Get the integer value of restrictions
     */
    public int toInt() {
        return set.toInt();
    }

    /**
     * Check if the player can be assaulted (alignment fight)
     */
    public boolean canAssault() {
        return !set.check(Restriction.DENY_ASSAULT);
    }

    /**
     * Check if the can ask a duel to the player
     */
    public boolean canChallenge() {
        return !set.check(Restriction.DENY_CHALLENGE);
    }

    /**
     * Check if exchange is allowed with this player
     */
    public boolean canExchange() {
        return !set.check(Restriction.DENY_EXCHANGE);
    }

    /**
     * Check if the player (mutant) can be attacked
     */
    public boolean canAttack() {
        return !set.check(Restriction.DENY_ATTACK);
    }

    /**
     * Check if the player is forced to walk (run is denied)
     */
    public boolean forceWalk() {
        return set.check(Restriction.FORCE_WALK);
    }

    /**
     * Check if the player is move slowly
     */
    public boolean isSlow() {
        return set.check(Restriction.IS_SLOW);
    }

    /**
     * Check if the player is a tomb
     */
    public boolean isTomb() {
        return set.check(Restriction.IS_TOMB);
    }

    /**
     * Refresh the exploration restrictions according to the local player restrictions
     */
    public void refresh() {
        final fr.quatrevieux.araknemu.game.player.Restrictions localRestrictions = player.player().restrictions();

        boolean hasChanged = false;

        for (LocalToExplorationMapping mapping : LOCAL_TO_EXPLORATION_MAPPING) {
            if (!mapping.checker.check(localRestrictions)) {
                hasChanged |= set.set(mapping.restriction);
            } else {
                hasChanged |= set.unset(mapping.restriction);
            }
        }

        if (hasChanged && player.map() != null) {
            player.map().dispatch(new RestrictionsChanged(player, this));
        }
    }
}