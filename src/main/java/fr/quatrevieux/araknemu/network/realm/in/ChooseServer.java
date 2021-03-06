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

package fr.quatrevieux.araknemu.network.realm.in;

import fr.quatrevieux.araknemu.core.network.parser.Packet;
import fr.quatrevieux.araknemu.core.network.parser.ParsePacketException;
import fr.quatrevieux.araknemu.core.network.parser.SinglePacketParser;

/**
 * Choose a game server
 */
final public class ChooseServer implements Packet {
    final static public class Parser implements SinglePacketParser<ChooseServer> {
        @Override
        public ChooseServer parse(String input) throws ParsePacketException {
            try {
                return new ChooseServer(Integer.parseInt(input));
            } catch (NumberFormatException e) {
                throw new ParsePacketException(code(), "Invalid server number", e);
            }
        }

        @Override
        public String code() {
            return "AX";
        }
    }

    final private int id;

    public ChooseServer(int id) {
        this.id = id;
    }

    /**
     * The server race
     */
    public int id() {
        return id;
    }
}
