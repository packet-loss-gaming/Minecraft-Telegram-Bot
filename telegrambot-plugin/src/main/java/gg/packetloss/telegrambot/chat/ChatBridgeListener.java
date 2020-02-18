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

import com.google.common.collect.Lists;
import gg.packetloss.bukkittext.Text;
import gg.packetloss.bukkittext.TextAction;
import gg.packetloss.telegrambot.event.TextMessageReceivedEvent;
import gg.packetloss.telegrambot.event.TextMessageUpdatedEvent;
import gg.packetloss.telegrambot.protocol.data.Sender;
import gg.packetloss.telegrambot.protocol.data.TextMessage;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.List;

import static gg.packetloss.telegrambot.BotComponent.getBot;

public class ChatBridgeListener implements Listener {
    private static final List<ChatColor> COLOR_OPTIONS = Lists.newArrayList(
            ChatColor.AQUA, ChatColor.BLUE, ChatColor.GREEN, ChatColor.DARK_GREEN,
            ChatColor.RED, ChatColor.DARK_RED, ChatColor.DARK_PURPLE, ChatColor.LIGHT_PURPLE,
            ChatColor.GOLD, ChatColor.DARK_AQUA
    );

    private ChatColor getNameColor(String name) {
        return COLOR_OPTIONS.get(name.hashCode() % COLOR_OPTIONS.size());
    }

    private void sendMessageBroadcast(String senderName, String messageBody) {
        Bukkit.broadcast(Text.of(
                Text.of(
                    ChatColor.BLUE,
                    "<",
                    Text.of(getNameColor(senderName), senderName),
                    "> ",
                    TextAction.Hover.showText(Text.of("Sent via Telegram"))
                ),
                messageBody
        ).build());
    }

    @EventHandler
    public void onMessageReceivedEvent(TextMessageReceivedEvent event) {
        TextMessage message = event.getMessage();

        String senderName = message.getSender().map(Sender::getName).orElse("Unknown Sender");
        String messageBody = message.getBody();

        sendMessageBroadcast(senderName, messageBody);
    }

    @EventHandler
    public void onMessageUpdatedEvent(TextMessageUpdatedEvent event) {
        TextMessage message = event.getMessage();

        String senderName = message.getSender().map(Sender::getName).orElse("Unknown Sender");
        String messageBody = message.getBody();

        sendMessageBroadcast(senderName, messageBody + "*");
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerJoin(PlayerJoinEvent event) {
        getBot().sendMessageToSyncChannels(ChatColor.stripColor(event.getJoinMessage()));
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerLeave(PlayerQuitEvent event) {
        getBot().sendMessageToSyncChannels(ChatColor.stripColor(event.getQuitMessage()));
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerDeath(PlayerDeathEvent event) {
        String message = event.getDeathMessage();

        getBot().sendMessageToSyncChannels(ChatColor.stripColor(message));
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        String sender = event.getPlayer().getName();
        String message = event.getMessage();

        getBot().sendMessageToSyncChannels(sender, ChatColor.stripColor(message));
    }
}
