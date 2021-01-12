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

package gg.packetloss.telegrambot.factory;

import gg.packetloss.telegrambot.protocol.data.Sender;
import gg.packetloss.telegrambot.protocol.data.TextMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.User;

public class TextMessageFactory {
    private TextMessageFactory() { }

    private static Sender getSender(Message message) {
        User sender = message.getFrom();
        if (sender == null) {
            return null;
        }

        return SenderFactory.build(message.getFrom());
    }

    private static String getText(Message message) {
        if (message.getText() != null) {
            return message.getText();
        }

        if (message.getCaption() != null) {
            return message.getCaption();
        }

        throw new IllegalStateException("Not a text message!");
    }

    public static TextMessage build(Message message) {
        return new TextMessage(getSender(message), getText(message));
    }
}
