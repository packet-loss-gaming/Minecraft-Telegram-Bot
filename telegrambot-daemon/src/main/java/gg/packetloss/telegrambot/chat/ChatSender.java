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

    public void sendMessageToSyncChannels(String text) {
        for (String syncChannel : bot.getConfig().getSyncChats()) {
            try {
                bot.executeAsync(new SendMessage(syncChannel, text), new SentCallback<>() {
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
}
