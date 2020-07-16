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

import com.sk89q.commandbook.CommandBook;
import com.zachsthings.libcomponents.ComponentInformation;
import com.zachsthings.libcomponents.bukkit.BukkitComponent;
import gg.packetloss.telegrambot.chat.ChatBridgeListener;
import gg.packetloss.telegrambot.client.ClientThread;
import gg.packetloss.telegrambot.client.ClientServerBot;
import gg.packetloss.telegrambot.command.RemoteCommandHandler;

import java.io.IOException;
import java.nio.file.Paths;

@ComponentInformation(friendlyName = "Telegram Bot", desc = "Telegram bot integration.")
public class BotComponent extends BukkitComponent {
    private static ClientServerBot bot;
    private BotConfiguration config;
    private BotCommandManager commandManager = new BotCommandManager();
    private Process daemon;
    private ClientThread clientThread;

    private String getDaemonPath() {
        return Paths.get(
                CommandBook.inst().getDataFolder().getPath(),
                "telegram-bot/daemon.jar"
        ).toAbsolutePath().toString();
    }

    private void destroyDaemonIfRunning() {
        if (daemon != null) {
            daemon.destroy();
            daemon = null;
        }
    }

    private void createNewDaemon() {
        try {
            daemon = new ProcessBuilder().inheritIO().command("java", "-jar", getDaemonPath()).start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void launchDaemon() {
        destroyDaemonIfRunning();
        createNewDaemon();
    }

    private void destroyClientThreadIfRunning() {
        if (clientThread != null) {
            clientThread.destroy();
            clientThread = null;
        }
    }

    @Override
    public void enable() {
        this.config = configure(new BotConfiguration());

        commandManager.registerCoreCommands();

        launchDaemon();

        bot = new ClientServerBot(config);
        bot.updateConfig();

        clientThread = new ClientThread(bot, this::launchDaemon);
        clientThread.start();

        CommandBook.registerEvents(new ChatBridgeListener());
        CommandBook.registerEvents(new RemoteCommandHandler(commandManager));

        notifyServerOn();
    }

    @Override
    public void disable() {
        notifyServerOff();
        destroyDaemonIfRunning();
        destroyClientThreadIfRunning();
    }

    @Override
    public void reload() {
        super.reload();
        configure(config);

        bot.updateConfig();
    }

    private void notifyServerOn() {
        bot.sendMessageToSyncChannels("Server back online");
    }

    private void notifyServerOff() {
        bot.sendMessageToSyncChannels("Server shutting down");
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static Bot getBot() {
        return bot;
    }
}
