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

package gg.packetloss.telegrambot.protocol.event.inbout;

import gg.packetloss.telegrambot.protocol.data.Command;
import gg.packetloss.telegrambot.protocol.data.Sender;
import gg.packetloss.telegrambot.protocol.data.TextMessage;
import gg.packetloss.telegrambot.protocol.event.EventType;
import gg.packetloss.telegrambot.protocol.event.ProtocolEvent;

import java.util.Optional;

public class InboundCommandEvent extends ProtocolEvent {
    private Command command;

    public InboundCommandEvent(Command command) {
        this.command = command;
    }

    public Command getCommand() {
        return command;
    }

    @Override
    public EventType getType() {
        return EventType.INBOUND_COMMAND;
    }
}
