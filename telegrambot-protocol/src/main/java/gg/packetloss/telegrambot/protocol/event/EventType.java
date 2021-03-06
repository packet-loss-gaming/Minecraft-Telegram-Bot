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

package gg.packetloss.telegrambot.protocol.event;

import gg.packetloss.telegrambot.protocol.event.generic.GenericNothingEvent;
import gg.packetloss.telegrambot.protocol.event.inbout.InboundAttachmentEvent;
import gg.packetloss.telegrambot.protocol.event.inbout.InboundCommandEvent;
import gg.packetloss.telegrambot.protocol.event.inbout.InboundNewMessageEvent;
import gg.packetloss.telegrambot.protocol.event.inbout.InboundUpdatedTextMessageEvent;
import gg.packetloss.telegrambot.protocol.event.outbound.*;

public enum EventType {
    GENERIC_NOTHING(GenericNothingEvent.class),

    INBOUND_ATTACHMENT(InboundAttachmentEvent.class),
    INBOUND_COMMAND(InboundCommandEvent.class),
    INBOUND_NEW_MESSAGE(InboundNewMessageEvent.class),
    INBOUND_UPDATED_MESSAGE(InboundUpdatedTextMessageEvent.class),

    OUTBOUND_CONFIG_SYNC(OutboundConfigSyncEvent.class),
    OUTBOUND_TEXT_MESSAGE(OutboundTextMessageEvent.class),
    OUTBOUND_SILENT_TEXT_MESSAGE(OutboundSilentTextMessageEvent.class),
    OUTBOUND_TEXT_MESSAGE_TO_CHAT(OutboundTextMessageToChatEvent.class),
    OUTBOUND_TEXT_MESSAGE_TO_USER_IN_CHAT(OutboundTextMessageToUserInChatEvent.class),
    OUTBOUND_MOD_TEXT_MESSAGE(OutboundModTextMessageEvent.class),
    OUTBOUND_MOD_SILENT_TEXT_MESSAGE(OutboundModSilentTextMessageEvent.class),
    OUTBOUND_DELETE_MESSAGE(OutboundDeleteMessageEvent.class);

    private final Class<? extends ProtocolEvent> clazz;

    private EventType(Class<? extends ProtocolEvent> clazz) {
        this.clazz = clazz;
    }

    public Class<? extends ProtocolEvent> getProtocolClass() {
        return clazz;
    }
}
