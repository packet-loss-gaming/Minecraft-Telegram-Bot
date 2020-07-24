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

package gg.packetloss.telegrambot.command.daemon;

import gg.packetloss.telegrambot.protocol.data.Chat;
import gg.packetloss.telegrambot.protocol.data.Sender;
import org.enginehub.piston.annotation.Command;
import org.enginehub.piston.annotation.CommandContainer;

import static gg.packetloss.telegrambot.BotComponent.getBot;

@CommandContainer
public class PingCommands {
    @Command(name = "ping", desc = "Ping the bot")
    public void pingCmd(Chat chat) {
        getBot().sendMessageToChat(chat, "Pong!");
    }

    @Command(name = "pong", desc = "Ping the bot (with a twist)")
    public void pongCmd(Chat chat, Sender sender) {
        getBot().sendMessageToChat(chat, sender.getName() + " likes cute, rabid, cows!");
    }
}
