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

package gg.packetloss.telegrambot.command.plugin;

import gg.packetloss.telegrambot.protocol.data.abstraction.TGUserID;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.enginehub.piston.annotation.Command;
import org.enginehub.piston.annotation.CommandContainer;
import org.enginehub.piston.annotation.param.Arg;

import java.util.Optional;

import static gg.packetloss.telegrambot.BotComponent.getBot;

@CommandContainer
public class TelegramBotCommands {
    @Command(name = "verify", desc = "Verify your telegram account")
    public void verifyCmd(Player player, @Arg(desc = "verification code") String verificationCode) {
        Optional<TGUserID> optMatch = getBot().getPendingVerificationDB().matches(player.getName(), verificationCode);
        if (optMatch.isPresent()) {
            getBot().getVerifiedDB().register(optMatch.get(), player.getUniqueId());
            player.sendMessage(ChatColor.YELLOW + "Verified!");
        } else {
            player.sendMessage(ChatColor.RED + "Invalid verification code for this player.");
        }
    }
}
