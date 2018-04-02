package fr.quatrevieux.araknemu.game.fight;

import fr.quatrevieux.araknemu.game.fight.exception.InvalidFightStateException;
import fr.quatrevieux.araknemu.game.fight.fighter.Fighter;
import fr.quatrevieux.araknemu.game.fight.map.FightMap;
import fr.quatrevieux.araknemu.game.fight.state.*;
import fr.quatrevieux.araknemu.game.fight.team.FightTeam;
import fr.quatrevieux.araknemu.game.fight.type.FightType;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Handle fight
 */
final public class Fight {
    final private FightType type;
    final private FightMap map;
    final private List<FightTeam> teams;
    final private StatesFlow statesFlow;

    public Fight(FightType type, FightMap map, List<FightTeam> teams, StatesFlow statesFlow) {
        this.type = type;
        this.map = map;
        this.teams = teams;
        this.statesFlow = statesFlow;
    }

    public Fight(FightType type, FightMap map, List<FightTeam> teams) {
        this(type, map, teams, new StatesFlow(
            new NullState(),
            new InitialiseState(),
            new PlacementState()
        ));
    }

    /**
     * Get all teams
     */
    public List<FightTeam> teams() {
        return teams;
    }

    /**
     * Get one team
     *
     * @param number The team number
     */
    public FightTeam team(int number) {
        return teams.get(number);
    }

    /**
     * Get all fighters
     */
    public List<Fighter> fighters() {
        return teams
            .stream()
            .flatMap(fightTeam -> fightTeam.fighters().stream())
            .collect(Collectors.toList())
        ;
    }

    /**
     * Get the fight map
     */
    public FightMap map() {
        return map;
    }

    /**
     * Get the current fight state
     */
    public FightState state() {
        return statesFlow.current();
    }

    /**
     * Get the current fight state if the type corresponds
     */
    public <T extends FightState> T state(Class<T> type) {
        if (!type.isInstance(statesFlow.current())) {
            throw new InvalidFightStateException(type);
        }

        return (T) statesFlow.current();
    }

    /**
     * Start the next fight state
     */
    public void nextState() {
        statesFlow.next(this);
    }

    /**
     * Get the fight type
     */
    public FightType type() {
        return type;
    }

    /**
     * Send packet to all players
     */
    public void send(Object packet) {
        String sPacket = packet.toString();

        for (FightTeam team : teams) {
            team.send(sPacket);
        }
    }
}