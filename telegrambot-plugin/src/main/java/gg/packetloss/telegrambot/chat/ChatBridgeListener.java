/*
 * This file is part of the Minecraft Telegram Bot.
 *
 * Minecraft Telegram Bot is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Minecraft Telegram Bot is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Minecraft Telegram Bot.  If not, see <https://www.gnu.org/licenses/>.
 */

package gg.packetloss.telegrambot.chat;

import gg.packetloss.telegrambot.protocol.data.TextMessage;
import gg.packetloss.telegrambot.protocol.data.Sender;
import gg.packetloss.telegrambot.event.TextMessageReceivedEvent;
import gg.packetloss.telegrambot.event.TextMessageUpdatedEvent;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import static gg.packetloss.telegrambot.BotComponent.getBot;

public class ChatBridgeListener implements Listener {
    @EventHandler
    public void onMessageReceivedEvent(TextMessageReceivedEvent event) {
        TextMessage message = event.getMessage();

        String senderName = message.getSender().map(Sender::getName).orElse("Unknown Sender");
        String messageBody = message.getBody();

        Bukkit.broadcastMessage("[TG] <" + senderName + "> " + messageBody);
    }

    @EventHandler
    public void onMessageUpdatedEvent(TextMessageUpdatedEvent event) {
        TextMessage message = event.getMessage();

        String senderName = message.getSender().map(Sender::getName).orElse("Unknown Sender");
        String messageBody = message.getBody();

        Bukkit.broadcastMessage("[TG] <" + senderName + "> " + messageBody + "*");
    }

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        String sender = event.getPlayer().getName();
        String message = event.getMessage();

        getBot().sendMessageToSyncChannels(sender, message);
    }
}
