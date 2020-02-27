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

import com.sk89q.commandbook.CommandBook;
import gg.packetloss.telegrambot.event.CommandReceivedEvent;
import gg.packetloss.telegrambot.protocol.event.ProtocolEvent;
import gg.packetloss.telegrambot.protocol.event.inbout.InboundCommandEvent;
import gg.packetloss.telegrambot.protocol.event.inbout.InboundTextMessageEvent;
import gg.packetloss.telegrambot.event.TextMessageReceivedEvent;
import gg.packetloss.telegrambot.event.TextMessageUpdatedEvent;

import static gg.packetloss.telegrambot.protocol.event.generic.GenericNothingEvent.SLEEP_DURATION;

class EventDispatch {
    public void accept(ProtocolEvent event) {
        switch (event.getType()) {
            case INBOUND_COMMAND:
                CommandBook.callEvent(new CommandReceivedEvent(((InboundCommandEvent) event).getCommand()));
                break;
            case INBOUND_NEW_MESSAGE:
                CommandBook.callEvent(new TextMessageReceivedEvent(((InboundTextMessageEvent) event).getMessage()));
                break;
            case INBOUND_UPDATED_MESSAGE:
                CommandBook.callEvent(new TextMessageUpdatedEvent(((InboundTextMessageEvent) event).getMessage()));
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
