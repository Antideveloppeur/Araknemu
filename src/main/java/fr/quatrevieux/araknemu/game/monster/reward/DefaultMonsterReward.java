package fr.quatrevieux.araknemu.game.monster.reward;

import fr.quatrevieux.araknemu.data.value.Interval;

/**
 * Base implementation for single monster grade reward
 */
final public class DefaultMonsterReward implements MonsterReward {
    final private MonsterGradesReward reward;
    final private int grade;

    public DefaultMonsterReward(MonsterGradesReward reward, int grade) {
        this.reward = reward;
        this.grade = grade;
    }

    @Override
    public Interval kamas() {
        return reward.kamas();
    }

    @Override
    public long experience() {
        return reward.experience(grade);
    }
}
