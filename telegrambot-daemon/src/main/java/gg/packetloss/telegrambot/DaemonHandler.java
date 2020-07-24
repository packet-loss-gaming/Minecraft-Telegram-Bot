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

import gg.packetloss.telegrambot.chat.ChatSender;
import gg.packetloss.telegrambot.protocol.event.ProtocolEvent;
import gg.packetloss.telegrambot.protocol.event.outbound.*;
import org.telegram.telegrambots.meta.TelegramBotsApi;

import static gg.packetloss.telegrambot.protocol.event.generic.GenericNothingEvent.SLEEP_DURATION;

public class DaemonHandler {
    private final TelegramBotsApi api = new TelegramBotsApi();

    private final TelegramBot bot;
    private final ChatSender chatSender;

    private boolean botRegistered = false;

    public DaemonHandler(TelegramBot bot) {
        this.bot = bot;
        this.chatSender = new ChatSender(bot);
    }

    private void handleConfigSync(OutboundConfigSyncEvent event) {
        bot.setConfig(event.getConfig());
        if (!botRegistered) {
            try {
                api.registerBot(bot);
                botRegistered = true;
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    private void handleTextMessage(OutboundTextMessageEvent event) {
        chatSender.sendMessageToSyncChannels(event.getText());
    }

    private void handleSilentTextMessage(OutboundSilentTextMessageEvent event) {
        chatSender.sendMessageToSyncChannelsSilently(event.getText());
    }

    private void handleTextMessageToChat(OutboundTextMessageToChatEvent event) {
        chatSender.sendMessageToChat(event.getChat(), event.getText());
    }

    private void handleTextMessageToUserInChat(OutboundTextMessageToUserInChatEvent event) {
        chatSender.sendMessageToUserInChat(event.getUser(), event.getChat(), event.getText());
    }

    private void handleModTextMessage(OutboundModTextMessageEvent event) {
        chatSender.sendMessageToModChannels(event.getText());
    }

    private void handleModSilentTextMessage(OutboundModSilentTextMessageEvent event) {
        chatSender.sendMessageToModChannelsSilently(event.getText());
    }

    private void handleDeleteMessage(OutboundDeleteMessageEvent event) {
        chatSender.deleteMessageID(event.getMessageID());
    }

    public void accept(ProtocolEvent event) {
        switch (event.getType()) {
            case OUTBOUND_CONFIG_SYNC:
                handleConfigSync((OutboundConfigSyncEvent) event);
                break;
            case OUTBOUND_TEXT_MESSAGE:
                handleTextMessage((OutboundTextMessageEvent) event);
                break;
            case OUTBOUND_SILENT_TEXT_MESSAGE:
                handleSilentTextMessage((OutboundSilentTextMessageEvent) event);
                break;
            case OUTBOUND_TEXT_MESSAGE_TO_CHAT:
                handleTextMessageToChat((OutboundTextMessageToChatEvent) event);
                break;
            case OUTBOUND_TEXT_MESSAGE_TO_USER_IN_CHAT:
                handleTextMessageToUserInChat((OutboundTextMessageToUserInChatEvent) event);
                break;
            case OUTBOUND_MOD_TEXT_MESSAGE:
                handleModTextMessage((OutboundModTextMessageEvent) event);
                break;
            case OUTBOUND_MOD_SILENT_TEXT_MESSAGE:
                handleModSilentTextMessage((OutboundModSilentTextMessageEvent) event);
                break;
            case OUTBOUND_DELETE_MESSAGE:
                handleDeleteMessage((OutboundDeleteMessageEvent) event);
                break;
            case GENERIC_NOTHING:
                try {
                    Thread.sleep(SLEEP_DURATION);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                break;
            default:
                throw new IllegalStateException();
        }
    }
}
