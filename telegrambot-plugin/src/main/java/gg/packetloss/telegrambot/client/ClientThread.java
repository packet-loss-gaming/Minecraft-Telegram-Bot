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
import gg.packetloss.telegrambot.protocol.MessageProcessor;
import gg.packetloss.telegrambot.protocol.PlatformAdapter;
import gg.packetloss.telegrambot.protocol.event.ProtocolEvent;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ConnectException;
import java.net.Socket;
import java.util.Deque;

public class ClientThread extends Thread {
    private final ClientServerBot daemonBot;
    private final Runnable restartDaemon;
    private final EventDispatch dispatch = new EventDispatch();
    private boolean isAlive = true;
    private int failures = 0;

    public ClientThread(ClientServerBot daemonBot, Runnable restartDaemon) {
        this.daemonBot = daemonBot;
        this.restartDaemon = restartDaemon;
    }

    public void destroy() {
        this.isAlive = false;
    }

    private void sleepForFailure() throws InterruptedException {
        ++failures;
        sleep(failures * failures * 1000);
    }

    private void maybeRestartDaemon() {
        if (failures > 0 && failures % 5 == 0) {
            restartDaemon.run();
        }
    }

    private void handleFailure() throws InterruptedException {
        maybeRestartDaemon();
        sleepForFailure();
    }

    private void runConnection() throws IOException, InterruptedException {
        try (
                Socket socket = new Socket("localhost", 6854);
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                PrintWriter out = new PrintWriter(socket.getOutputStream(), false);
        ) {
            CommandBook.logger().info("Connected to Telegram Daemon");
            new MessageProcessor(new PlatformAdapter() {
                @Override
                public boolean isAlive() {
                    return isAlive && socket.isConnected() && !socket.isClosed();
                }

                @Override
                public boolean isServer() {
                    return false;
                }

                @Override
                public void acceptIncoming(ProtocolEvent event) {
                    dispatch.accept(event);
                }

                @Override
                public Deque<ProtocolEvent> getOutbound() {
                    return daemonBot.getPendingEvents();
                }
            }).accept(in, out);

            failures = 0;
        } catch (ConnectException ex) {
            handleFailure();
        } catch (Throwable t) {
            handleFailure();
            throw t;
        }
    }

    @Override
    public void run() {
        while (isAlive) {
            try {
                runConnection();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }
}
