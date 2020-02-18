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

package gg.packetloss.telegrambot.client;

import gg.packetloss.telegrambot.Bot;
import gg.packetloss.telegrambot.BotConfiguration;
import gg.packetloss.telegrambot.protocol.data.ConfigDetail;
import gg.packetloss.telegrambot.protocol.event.ProtocolEvent;
import gg.packetloss.telegrambot.protocol.event.outbound.OutboundConfigSyncEvent;
import gg.packetloss.telegrambot.protocol.event.outbound.OutboundSilentTextMessageEvent;
import gg.packetloss.telegrambot.protocol.event.outbound.OutboundTextMessageEvent;

import java.util.ArrayDeque;
import java.util.Deque;

public class ClientServerBot implements Bot {
    private Deque<ProtocolEvent> pendingEvents = new ArrayDeque<>();
    private BotConfiguration config;

    public ClientServerBot(BotConfiguration config) {
        this.config = config;
    }

    public Deque<ProtocolEvent> getPendingEvents() {
        return pendingEvents;
    }

    @Override
    public void sendMessageToSyncChannels(String fromUser, String message) {
        pendingEvents.add(new OutboundTextMessageEvent("<" + fromUser + "> " + message));
    }

    @Override
    public void sendMessageToSyncChannels(String message) {
        pendingEvents.add(new OutboundSilentTextMessageEvent(message));
    }

    @Override
    public void updateConfig() {
        pendingEvents.add(new OutboundConfigSyncEvent(new ConfigDetail(
                config.username, config.apiKey, config.syncChats
        )));
    }
}
