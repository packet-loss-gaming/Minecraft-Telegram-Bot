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
import gg.packetloss.telegrambot.event.AttachmentReceivedEvent;
import gg.packetloss.telegrambot.event.TextMessageReceivedEvent;
import gg.packetloss.telegrambot.event.TextMessageUpdatedEvent;
import gg.packetloss.telegrambot.protocol.data.Attachment;
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
import java.util.UUID;

import static gg.packetloss.telegrambot.BotComponent.getBot;

public class ChatBridgeListener implements Listener {
    private static final List<ChatColor> COLOR_OPTIONS = Lists.newArrayList(
            ChatColor.AQUA, ChatColor.BLUE, ChatColor.GREEN, ChatColor.DARK_GREEN,
            ChatColor.RED, ChatColor.DARK_RED, ChatColor.DARK_PURPLE, ChatColor.LIGHT_PURPLE,
            ChatColor.GOLD, ChatColor.DARK_AQUA
    );

    private ChatColor getNameColor(String name) {
        return COLOR_OPTIONS.get(Math.abs(name.hashCode()) % COLOR_OPTIONS.size());
    }

    private UUID getMinecraftID(Sender sender) {
        return getBot().getVerifiedDB().getMinecraftID(sender.getID());
    }

    private String getSenderName(Sender sender, UUID minecraftID) {
        if (minecraftID != null) {
            return Bukkit.getOfflinePlayer(minecraftID).getName();
        }

        return sender.getName();
    }

    private void broadcast(Text text) {
        var builtText = text.build();
        Bukkit.broadcast(builtText);
        Bukkit.getConsoleSender().sendMessage(builtText); // console doesn't receive messages from broadcast when
                                                          // created with text components (sadly)
    }

    private void sendActionBroadcast(String senderName, boolean verified, String messageBody) {
        var text = Text.of(
            Text.of(
                ChatColor.BLUE,
                "* ",
                Text.of(getNameColor(senderName), senderName),
                (verified ? " " : "* "),
                TextAction.Hover.showText(Text.of("Sent via Telegram", verified ? "" : " - Unverified")),
                TextAction.Click.openURL("https://t.me/skelril")
            ),
            messageBody
        );

        broadcast(text);
    }

    @EventHandler
    public void onAttachmentReceivedEvent(AttachmentReceivedEvent event) {
        UUID minecraftID = event.getSender().map(this::getMinecraftID).orElse(null);

        String senderName = event.getSender().map(sender -> getSenderName(sender, minecraftID)).orElse("Unknown Sender");
        Attachment.AttachmentKind kind = event.getAttachmentKind();
        String messageBody = "sent " + (kind.usesAn() ? "an " : "a ") + kind.getFriendlyName();

        sendActionBroadcast(senderName, minecraftID != null, messageBody);
    }

    private void sendMessageBroadcast(String senderName, boolean verified, String messageBody) {
        var text = Text.of(
                Text.of(
                        ChatColor.BLUE,
                        "<",
                        Text.of(getNameColor(senderName), senderName),
                        (verified ? "" : "*"),
                        "> ",
                        TextAction.Hover.showText(Text.of("Sent via Telegram", verified ? "" : " - Unverified")),
                        TextAction.Click.openURL("https://t.me/skelril")
                ),
                messageBody
        );

        broadcast(text);
    }

    @EventHandler
    public void onMessageReceivedEvent(TextMessageReceivedEvent event) {
        TextMessage message = event.getMessage();

        UUID minecraftID = message.getSender().map(this::getMinecraftID).orElse(null);

        String senderName = message.getSender().map(sender -> getSenderName(sender, minecraftID)).orElse("Unknown Sender");
        String messageBody = message.getBody();

        sendMessageBroadcast(senderName, minecraftID != null, messageBody);
    }

    @EventHandler
    public void onMessageUpdatedEvent(TextMessageUpdatedEvent event) {
        TextMessage message = event.getMessage();

        UUID minecraftID = message.getSender().map(this::getMinecraftID).orElse(null);

        String senderName = message.getSender().map(sender -> getSenderName(sender, minecraftID)).orElse("Unknown Sender");
        String messageBody = message.getBody();

        sendMessageBroadcast(senderName, minecraftID != null, messageBody + "*");
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
