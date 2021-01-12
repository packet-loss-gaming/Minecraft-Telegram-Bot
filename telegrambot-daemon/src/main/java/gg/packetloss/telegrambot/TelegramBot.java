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

package gg.packetloss.telegrambot;

import gg.packetloss.telegrambot.factory.AttachmentFactory;
import gg.packetloss.telegrambot.factory.CommandFactory;
import gg.packetloss.telegrambot.protocol.data.ConfigDetail;
import gg.packetloss.telegrambot.protocol.data.TextMessage;
import gg.packetloss.telegrambot.protocol.event.ProtocolEvent;
import gg.packetloss.telegrambot.protocol.event.inbout.InboundAttachmentEvent;
import gg.packetloss.telegrambot.protocol.event.inbout.InboundCommandEvent;
import gg.packetloss.telegrambot.protocol.event.inbout.InboundNewMessageEvent;
import gg.packetloss.telegrambot.protocol.event.inbout.InboundUpdatedTextMessageEvent;
import gg.packetloss.telegrambot.factory.TextMessageFactory;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.PhotoSize;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;

public class TelegramBot extends TelegramLongPollingBot {
    private Deque<ProtocolEvent> pendingEvents = new ArrayDeque<>();
    private ConfigDetail config;

    public Deque<ProtocolEvent> getPendingEvents() {
        return pendingEvents;
    }

    public void setConfig(ConfigDetail config) {
        this.config = config;
    }

    public ConfigDetail getConfig() {
        return config;
    }

    private boolean isValidTextMessage(Message message) {
        if (message.getText() == null && message.getCaption() == null) {
            return false;
        }

        String chatId = String.valueOf(message.getChatId());
        if (!config.getSyncChats().contains(chatId)) {
            return false;
        }

        return true;
    }

    private boolean hasAttachments(Message message) {
        String chatId = String.valueOf(message.getChatId());
        if (!config.getSyncChats().contains(chatId)) {
            return false;
        }

        if (message.hasPhoto()) {
            return true;
        }

        if (message.hasSticker()) {
            return true;
        }

        if (message.hasDocument()) {
            return true;
        }

        if (message.hasVideo() || message.hasVideoNote()) {
            return true;
        }

        if (message.hasVoice()) {
            return true;
        }

        if (message.hasAudio()) {
            return true;
        }

        if (message.hasPoll()) {
            return true;
        }

        if (message.hasContact()) {
            return true;
        }

        if (message.getGame() != null) {
            return true;
        }

        return false;
    }

    private boolean isValidCommand(Message message) {
        if (message.getText() == null) {
            return false;
        }

        if (!message.getText().startsWith("!")) {
            return false;
        }

        if (message.getFrom() == null) {
            return false;
        }

        // return message.getChat().isUserChat();
        return true;
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage()) {
            Message message = update.getMessage();

            if (isValidCommand(message)) {
                pendingEvents.add(new InboundCommandEvent(CommandFactory.build(message)));
                return;
            }

            if (hasAttachments(message)) {
                pendingEvents.add(new InboundAttachmentEvent(AttachmentFactory.build(message)));
            }

            if (isValidTextMessage(message)) {
                pendingEvents.add(new InboundNewMessageEvent(TextMessageFactory.build(message)));
                return;
            }

            return;
        }

        if (update.hasEditedMessage()) {
            Message message = update.getEditedMessage();
            if (isValidTextMessage(message)) {
                pendingEvents.add(new InboundUpdatedTextMessageEvent(TextMessageFactory.build(message)));
            }
            return;
        }
    }

    @Override
    public String getBotUsername() {
        return config.getName();
    }

    @Override
    public String getBotToken() {
        return config.getAPIKey();
    }
}
