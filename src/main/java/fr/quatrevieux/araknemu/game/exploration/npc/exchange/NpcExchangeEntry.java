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
 * Copyright (c) 2017-2019 Vincent Quatrevieux
 */

package fr.quatrevieux.araknemu.game.exploration.npc.exchange;

import fr.quatrevieux.araknemu.data.world.entity.environment.npc.NpcExchange;
import fr.quatrevieux.araknemu.data.world.entity.item.ItemTemplate;
import fr.quatrevieux.araknemu.game.item.Item;
import fr.quatrevieux.araknemu.game.item.ItemService;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Single entry for an Npc exchange
 *
 * The entry associate a matching set of items and kamas to exchanged items and kamas
 */
final public class NpcExchangeEntry {
    /** Null instance for an exchange entry */
    final static public NpcExchangeEntry NULL_ENTRY = new NpcExchangeEntry(null, new NpcExchange(-1, -1, 0, null, 0, null), Collections.emptyMap());

    final private ItemService itemService;
    final private NpcExchange entity;
    final private Map<ItemTemplate, Integer> templatesAndQuantity;

    public NpcExchangeEntry(ItemService itemService, NpcExchange entity, Map<ItemTemplate, Integer> templatesAndQuantity) {
        this.itemService = itemService;
        this.entity = entity;
        this.templatesAndQuantity = templatesAndQuantity;
    }

    /**
     * Check if the entry match with the player exchange
     *
     * @param items Map of item templates with quantity
     * @param kamas The player exchanged kamas
     *
     * @return true if match
     */
    public boolean match(Map<Integer, Integer> items, long kamas) {
        return valid() && entity.requiredKamas() == kamas && entity.requiredItems().equals(items);
    }

    /**
     * Get the exchanged item templates associated with quantity
     */
    public Collection<Map.Entry<ItemTemplate, Integer>> items() {
        return templatesAndQuantity.entrySet();
    }

    /**
     * Get the exchanged kamas
     */
    public long kamas() {
        return entity.exchangedKamas();
    }

    /**
     * Generates the items
     *
     * @return The items associated with the generated quantity
     */
    public Map<Item, Integer> generate() {
        final Map<Item, Integer> items = new HashMap<>();

        templatesAndQuantity.forEach((template, quantity) -> items.putAll(itemService.createBulk(template, quantity)));

        return items;
    }

    /**
     * Check if the entry is valid
     */
    public boolean valid() {
        return itemService != null;
    }
}
