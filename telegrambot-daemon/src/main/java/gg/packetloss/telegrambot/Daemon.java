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

import gg.packetloss.telegrambot.protocol.MessageProcessor;
import gg.packetloss.telegrambot.protocol.PlatformAdapter;
import gg.packetloss.telegrambot.protocol.event.ProtocolEvent;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Deque;

public class Daemon {
    public static void main(String[] args) throws TelegramApiException {
        TelegramBot bot = new TelegramBot();
        DaemonHandler handler = new DaemonHandler(bot);

        try (
                ServerSocket serverSocket = new ServerSocket(6854);
                Socket clientSocket = serverSocket.accept();
                BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), false);
        ) {
            new MessageProcessor(new PlatformAdapter() {
                @Override
                public boolean isAlive() {
                    return clientSocket.isConnected() && !clientSocket.isClosed();
                }

                @Override
                public boolean isServer() {
                    return true;
                }

                @Override
                public void acceptIncoming(ProtocolEvent event) {
                    handler.accept(event);
                }

                @Override
                public Deque<ProtocolEvent> getOutbound() {
                    return bot.getPendingEvents();
                }
            }).accept(in, out);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
