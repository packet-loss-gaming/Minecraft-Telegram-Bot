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

package gg.packetloss.telegrambot.protocol.data;

import java.util.Optional;

public class TextMessage {
    private final Sender sender;
    private final String messageText;

    public TextMessage(Sender sender, String messageText) {
        this.sender = sender;
        this.messageText = messageText;
    }

    public Optional<Sender> getSender() {
        return Optional.ofNullable(sender);
    }

    public String getBody() {
        return messageText;
    }
}
