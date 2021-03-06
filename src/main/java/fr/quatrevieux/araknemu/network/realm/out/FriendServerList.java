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

package fr.quatrevieux.araknemu.network.realm.out;

import fr.quatrevieux.araknemu.data.value.ServerCharacters;

import java.util.Collection;
import java.util.stream.Collectors;

/**
 * List of friend servers
 *
 * https://github.com/Emudofus/Dofus/blob/1.29/dofus/aks/Account.as#L1235
 */
final public class FriendServerList {
    final private Collection<ServerCharacters> servers;

    public FriendServerList(Collection<ServerCharacters> servers) {
        this.servers = servers;
    }

    @Override
    public String toString() {
        return "AF" + servers.stream()
            .map(serverCharacters -> serverCharacters.serverId() + "," + serverCharacters.charactersCount())
            .collect(Collectors.joining(";"))
        ;
    }
}
