/*
 * This file is part of Araknemu.
 *
 * Araknemu is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Araknemu is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Araknemu.  If not, see <https://www.gnu.org/licenses/>.
 *
 * Copyright (c) 2017-2020 Vincent Quatrevieux
 */

package fr.quatrevieux.araknemu.game.item.type;

import fr.arakne.utils.value.Interval;
import fr.quatrevieux.araknemu.data.world.entity.item.ItemTemplate;
import fr.quatrevieux.araknemu.data.world.entity.item.ItemType;
import fr.quatrevieux.araknemu.game.item.GameItemSet;
import fr.quatrevieux.araknemu.game.item.effect.CharacteristicEffect;
import fr.quatrevieux.araknemu.game.item.effect.ItemEffect;
import fr.quatrevieux.araknemu.game.item.effect.SpecialEffect;
import fr.quatrevieux.araknemu.game.item.effect.WeaponEffect;
import fr.quatrevieux.araknemu.game.spell.effect.area.SpellEffectArea;

import java.util.ArrayList;
import java.util.List;

/**
 * Class for weapons
 */
final public class Weapon extends Equipment {
    final static public class WeaponInfo {
        final private int apCost;
        final private Interval range;
        final private int criticalRate;
        final private int failureRate;
        final private int criticalBonus;
        final private boolean isTwoHanded;

        public WeaponInfo(int apCost, Interval range, int criticalRate, int failureRate, int criticalBonus, boolean isTwoHanded) {
            this.apCost = apCost;
            this.range = range;
            this.criticalRate = criticalRate;
            this.failureRate = failureRate;
            this.criticalBonus = criticalBonus;
            this.isTwoHanded = isTwoHanded;
        }

        public int apCost() {
            return apCost;
        }

        public Interval range() {
            return range;
        }

        public int criticalRate() {
            return criticalRate;
        }

        public int failureRate() {
            return failureRate;
        }

        public int criticalBonus() {
            return criticalBonus;
        }

        public boolean isTwoHanded() {
            return isTwoHanded;
        }
    }

    final private List<WeaponEffect> weaponEffects;
    final private WeaponInfo info;
    final private SpellEffectArea area;

    public Weapon(ItemTemplate template, ItemType type, GameItemSet set, List<WeaponEffect> weaponEffects, List<CharacteristicEffect> characteristics, List<SpecialEffect> specials, WeaponInfo info, SpellEffectArea area) {
        super(template, type, set, characteristics, specials);

        this.weaponEffects = weaponEffects;
        this.info = info;
        this.area = area;
    }

    @Override
    public List<? extends ItemEffect> effects() {
        List<ItemEffect> effects = new ArrayList<>(weaponEffects);

        effects.addAll(super.effects());

        return effects;
    }

    public List<WeaponEffect> weaponEffects() {
        return weaponEffects;
    }

    /**
     * Get the weapon effects info
     */
    public WeaponInfo info() {
        return info;
    }

    /**
     * Get the area of the weapon effects
     */
    public SpellEffectArea effectArea() {
        return area;
    }

    @Override
    public boolean equals(Object obj) {
        if (!super.equals(obj)) {
            return false;
        }

        Weapon weapon = (Weapon) obj;

        return weaponEffects.equals(weapon.weaponEffects);
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();

        result = 31 * result + weaponEffects.hashCode();

        return result;
    }
}
