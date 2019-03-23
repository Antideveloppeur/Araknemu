package fr.quatrevieux.araknemu.game.monster.reward;

import fr.quatrevieux.araknemu.data.value.Interval;

/**
 * Store rewards for all monster grades
 */
public interface MonsterGradesReward {
    /**
     * The win kamas interface
     */
    public Interval kamas();

    /**
     * Get the base experience for the given grade
     *
     * @param gradeNumber The monster grade number (starts at 1)
     *
     * @see fr.quatrevieux.araknemu.game.monster.Monster#gradeNumber()
     */
    public long experience(int gradeNumber);

    /**
     * Creates the reward for the given monster grade
     *
     * @param gradeNumber The monster grade number (starts at 1)
     *
     * @see fr.quatrevieux.araknemu.game.monster.Monster#gradeNumber()
     */
    public MonsterReward grade(int gradeNumber);
}
