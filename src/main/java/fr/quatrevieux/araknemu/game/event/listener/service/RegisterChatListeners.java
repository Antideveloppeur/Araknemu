package fr.quatrevieux.araknemu.game.event.listener.service;

import fr.quatrevieux.araknemu.game.event.Listener;
import fr.quatrevieux.araknemu.game.event.common.PlayerLoaded;
import fr.quatrevieux.araknemu.game.event.listener.player.chat.InitializeChat;
import fr.quatrevieux.araknemu.game.event.listener.player.chat.MessageReceived;

/**
 * Register all chat listeners
 */
final public class RegisterChatListeners implements Listener<PlayerLoaded> {
    @Override
    public void on(PlayerLoaded event) {
        event.player().dispatcher().add(
            new InitializeChat(event.player())
        );

        event.player().dispatcher().add(
            new MessageReceived(event.player())
        );
    }

    @Override
    public Class<PlayerLoaded> event() {
        return PlayerLoaded.class;
    }
}
