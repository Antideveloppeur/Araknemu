package fr.quatrevieux.araknemu.game.listener.player.chat;

import fr.quatrevieux.araknemu.core.event.Listener;
import fr.quatrevieux.araknemu.game.chat.event.BroadcastedMessage;
import fr.quatrevieux.araknemu.game.player.GamePlayer;
import fr.quatrevieux.araknemu.network.game.out.chat.MessageSent;

/**
 * Listen broadcasted messages
 */
final public class MessageReceived implements Listener<BroadcastedMessage> {
    final private GamePlayer player;

    public MessageReceived(GamePlayer player) {
        this.player = player;
    }

    @Override
    public void on(BroadcastedMessage event) {
        if (
            !player.subscriptions().contains(event.channel())
            && event.sender() != player
        ) {
            return;
        }

        player.send(
            new MessageSent(
                event.sender(),
                event.channel(),
                event.message(),
                event.extra()
            )
        );
    }

    @Override
    public Class<BroadcastedMessage> event() {
        return BroadcastedMessage.class;
    }
}