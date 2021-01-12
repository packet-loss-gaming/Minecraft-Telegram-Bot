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

import gg.packetloss.telegrambot.protocol.data.Attachment;
import gg.packetloss.telegrambot.protocol.data.Sender;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.User;

import java.util.List;

public class AttachmentFactory {
    private AttachmentFactory() { }

    private static Sender getSender(Message message) {
        User sender = message.getFrom();
        if (sender == null) {
            return null;
        }

        return SenderFactory.build(message.getFrom());
    }

    private static final List<String> RECOGNIZED_PHOTO_TYPES = List.of("image/jpeg", "png", "tif", "webp", "gif");

    private static Attachment.AttachmentKind getKind(Message message) {
        if (message.hasPhoto()) {
            return Attachment.AttachmentKind.PHOTO;
        }

        if (message.hasSticker()) {
            return Attachment.AttachmentKind.STICKER;
        }

        if (message.hasDocument()) {
            String mimeName = message.getDocument().getMimeType().toLowerCase();
            if (mimeName.startsWith("image/")) {
                return Attachment.AttachmentKind.PHOTO;
            }

            if (mimeName.startsWith("video/")) {
                return Attachment.AttachmentKind.VIDEO;
            }

            return Attachment.AttachmentKind.FILE;
        }

        if (message.hasVideo() || message.hasVideoNote()) {
            return Attachment.AttachmentKind.VIDEO;
        }

        if (message.hasVoice()) {
            return Attachment.AttachmentKind.VOICE_MESSAGE;
        }

        if (message.hasAudio()) {
            return Attachment.AttachmentKind.AUDIO_MESSAGE;
        }

        if (message.hasPoll()) {
            return Attachment.AttachmentKind.POLL;
        }

        if (message.hasContact()) {
            return Attachment.AttachmentKind.CONTACT;
        }

        if (message.getGame() != null) {
            return Attachment.AttachmentKind.GAME;
        }

        throw new IllegalArgumentException("Message does not have attachment");
    }

    public static Attachment build(Message message) {
        Attachment.AttachmentKind kind = getKind(message);
        return new Attachment(getSender(message), kind);
    }
}
