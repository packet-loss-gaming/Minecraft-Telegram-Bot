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
import java.util.Optional;

public class InMemoryPendingVerificationDatabase implements PendingVerificationDatabase {
    private final Map<String, Long> challengeTokenToTelegramID = new HashMap<>();
    private final Map<Long, VerificationChallenge> challengeMap = new HashMap<>();

    private String generateChallengeToken() {
        String challengeToken;
        do {
            challengeToken = ChallengeTokenGenerator.generate();
        } while (challengeTokenToTelegramID.containsKey(challengeToken));
        return challengeToken;
    }

    private void removeActiveChallenges(long telegramID) {
        if (challengeMap.containsKey(telegramID)) {
            challengeTokenToTelegramID.remove(challengeMap.remove(telegramID).getChallengeToken());
        }
    }

    @Override
    public String createChallenge(TGUserID telegramID, String minecraftName) {
        removeActiveChallenges(telegramID.asLong());

        // Create and register the new challenge.
        String challengeToken = generateChallengeToken();

        challengeTokenToTelegramID.put(challengeToken, telegramID.asLong());
        challengeMap.put(telegramID.asLong(), new VerificationChallenge(minecraftName, challengeToken));

        return challengeToken;
    }

    @Override
    public Optional<TGUserID> matches(String minecraftName, String challengeToken) {
        long telegramID = challengeTokenToTelegramID.get(challengeToken);
        VerificationChallenge challenge = challengeMap.get(telegramID);
        if (challenge.getMinecraftName().equalsIgnoreCase(minecraftName)) {
            removeActiveChallenges(telegramID);
            return Optional.of(new TGUserID(telegramID));
        }

        return Optional.empty();
    }

    private static class VerificationChallenge {
        private final String minecraftName;
        private final String challengeToken;

        private VerificationChallenge(String minecraftName, String challengeToken) {
            this.minecraftName = minecraftName;
            this.challengeToken = challengeToken;
        }

        public String getMinecraftName() {
            return minecraftName;
        }

        public String getChallengeToken() {
            return challengeToken;
        }
    }
}
