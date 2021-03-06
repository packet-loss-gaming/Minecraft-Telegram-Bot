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

import gg.packetloss.telegrambot.protocol.data.Chat;
import gg.packetloss.telegrambot.protocol.data.abstraction.TGChatID;

public class ChatFactory {
    private ChatFactory() { }

    private static Chat.Type getType(org.telegram.telegrambots.meta.api.objects.Chat chat) {
        if (chat.isGroupChat()) {
            return Chat.Type.GROUP;
        }

        if (chat.isUserChat()) {
            return Chat.Type.PRIVATE;
        }

        return Chat.Type.CHANNEL;
    }

    public static Chat build(org.telegram.telegrambots.meta.api.objects.Chat chat) {
        return new Chat(new TGChatID(chat.getId()), getType(chat));
    }
}
