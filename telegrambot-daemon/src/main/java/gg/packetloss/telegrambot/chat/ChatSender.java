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

import gg.packetloss.telegrambot.TelegramBot;
import gg.packetloss.telegrambot.protocol.data.Chat;
import gg.packetloss.telegrambot.protocol.data.Sender;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.exceptions.TelegramApiRequestException;
import org.telegram.telegrambots.meta.updateshandlers.SentCallback;

public class ChatSender {
    private final TelegramBot bot;

    public ChatSender(TelegramBot bot) {
        this.bot = bot;
    }

    private void sendMessageToSyncChannelsInternal(String message, boolean notify) {
        for (String syncChannel : bot.getConfig().getSyncChats()) {
            try {
                SendMessage telegramMessage = new SendMessage(syncChannel, message);
                if (!notify) {
                    telegramMessage.disableNotification();
                }

                bot.executeAsync(telegramMessage, new SentCallback<>() {
                    @Override
                    public void onResult(BotApiMethod<Message> method, Message response) {

                    }

                    @Override
                    public void onError(BotApiMethod<Message> method, TelegramApiRequestException apiException) {

                    }

                    @Override
                    public void onException(BotApiMethod<Message> method, Exception exception) {

                    }
                });
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        }
    }

    public void sendMessageToSyncChannels(String text) {
        sendMessageToSyncChannelsInternal(text, true);
    }

    public void sendMessageToSyncChannelsSilently(String text) {
        sendMessageToSyncChannelsInternal(text, false);
    }

    public void sendMessageToChat(Chat chat, String text) {
        SendMessage telegramMessage = new SendMessage(chat.getID(), text);
        telegramMessage.disableNotification();
        try {
            bot.executeAsync(telegramMessage, new SentCallback<>() {
                @Override
                public void onResult(BotApiMethod<Message> method, Message response) {

                }

                @Override
                public void onError(BotApiMethod<Message> method, TelegramApiRequestException apiException) {

                }

                @Override
                public void onException(BotApiMethod<Message> method, Exception exception) {

                }
            });
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    public void sendMessageToUserInChat(Sender user, Chat chat, String text) {
        // We can't actually do this... For now, just send a message to the chat
        sendMessageToChat(chat, text);
    }
}
