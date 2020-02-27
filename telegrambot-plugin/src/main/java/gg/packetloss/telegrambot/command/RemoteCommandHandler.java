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

package gg.packetloss.telegrambot.command;

import gg.packetloss.telegrambot.event.CommandReceivedEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import static gg.packetloss.telegrambot.BotComponent.getBot;

public class RemoteCommandHandler implements Listener {
    @EventHandler
    public void onCommand(CommandReceivedEvent event) {
        switch (event.getCommandText().toLowerCase()) {
            case "ping":
                getBot().sendMessageToChat(event.getChat(), "pong!");
                break;
            case "pong":
                getBot().sendMessageToChat(event.getChat(), event.getSender().getName() + " likes cute, rabid, cows!");
                break;
            default:
                getBot().sendMessageToUserInChat(event.getSender(), event.getChat(), "unknown command.");
                break;
        }
    }
}
