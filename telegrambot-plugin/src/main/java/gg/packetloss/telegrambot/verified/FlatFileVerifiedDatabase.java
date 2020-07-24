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

package gg.packetloss.telegrambot.verified;

import gg.packetloss.telegrambot.protocol.data.abstraction.TGUserID;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class FlatFileVerifiedDatabase implements VerifiedDatabase {
    private Map<Long, UUID> telegramToMinecraft = new HashMap<>();
    private transient Map<UUID, Long> minecraftToTelegram = new HashMap<>();

    public void load() {
        telegramToMinecraft.forEach((telegramID, minecraftID) -> {
            minecraftToTelegram.put(minecraftID, telegramID);
        });
    }

    @Override
    public UUID getMinecraftID(TGUserID telegramID) {
        return telegramToMinecraft.get(telegramID.asLong());
    }

    @Override
    public long getTelegramID(UUID minecraftID) {
        return minecraftToTelegram.get(minecraftID);
    }

    @Override
    public boolean register(TGUserID telegramID, UUID minecraftID) {
        telegramToMinecraft.put(telegramID.asLong(), minecraftID);
        minecraftToTelegram.put(minecraftID, telegramID.asLong());
        return true;
    }
}
