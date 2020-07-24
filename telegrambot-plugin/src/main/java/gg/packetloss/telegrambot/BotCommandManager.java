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

import com.google.common.collect.ImmutableList;
import com.sk89q.worldedit.command.argument.Arguments;
import com.sk89q.worldedit.internal.command.CommandArgParser;
import com.sk89q.worldedit.internal.command.CommandRegistrationHandler;
import com.sk89q.worldedit.internal.util.Substring;
import com.sk89q.worldedit.util.formatting.text.TextComponent;
import com.sk89q.worldedit.util.formatting.text.format.TextColor;
import gg.packetloss.telegrambot.command.daemon.LinkCommands;
import gg.packetloss.telegrambot.command.daemon.LinkCommandsRegistration;
import gg.packetloss.telegrambot.command.daemon.PingCommands;
import gg.packetloss.telegrambot.command.daemon.PingCommandsRegistration;
import gg.packetloss.telegrambot.protocol.data.Chat;
import gg.packetloss.telegrambot.protocol.data.Sender;
import gg.packetloss.telegrambot.protocol.data.abstraction.TGMessageID;
import org.enginehub.piston.Command;
import org.enginehub.piston.CommandManager;
import org.enginehub.piston.exception.ConditionFailedException;
import org.enginehub.piston.exception.UsageException;
import org.enginehub.piston.impl.CommandManagerServiceImpl;
import org.enginehub.piston.inject.*;
import org.enginehub.piston.util.HelpGenerator;
import org.enginehub.piston.util.ValueProvider;

import java.util.stream.Stream;

import static gg.packetloss.telegrambot.BotComponent.getBot;

public class BotCommandManager {
    private final CommandManagerServiceImpl commandManagerService;
    private final CommandManager commandManager;
    private final InjectedValueStore globalInjectedValues;
    private final CommandRegistrationHandler registration;

    protected BotCommandManager() {
        this.commandManagerService = new CommandManagerServiceImpl();
        this.commandManager = commandManagerService.newCommandManager();
        this.globalInjectedValues = MapBackedValueStore.create();
        this.registration = new CommandRegistrationHandler(ImmutableList.of());
    }

    protected void registerCoreCommands() {
        registration.register(commandManager, PingCommandsRegistration.builder(), new PingCommands());
        registration.register(commandManager, LinkCommandsRegistration.builder(), new LinkCommands());
    }

    private Stream<Substring> parseArgs(String input) {
        return CommandArgParser.forArgString(input).parseArgs();
    }

    private MemoizingValueAccess initializeInjectedValues(Sender sender, Chat chat, TGMessageID messageID, Arguments arguments) {
        InjectedValueStore store = MapBackedValueStore.create();

        store.injectValue(Key.of(Sender.class), ValueProvider.constant(sender));
        store.injectValue(Key.of(Chat.class), ValueProvider.constant(chat));
        store.injectValue(Key.of(TGMessageID.class), ValueProvider.constant(messageID));

        store.injectValue(Key.of(Arguments.class), ValueProvider.constant(arguments));

        return MemoizingValueAccess.wrap(MergedValueAccess.of(store, globalInjectedValues));
    }

    public void handleCommand(Sender sender, Chat chat, TGMessageID messageID, String arguments) {
        String[] split = parseArgs(arguments).map(Substring::getSubstring).toArray(String[]::new);

        // No command found!
        if (!commandManager.containsCommand(split[0])) {
            getBot().sendMessageToUserInChat(sender, chat, "unknown command.");
            return;
        }

        MemoizingValueAccess context = initializeInjectedValues(sender, chat, messageID, () -> arguments);

        try {
            commandManager.execute(context, ImmutableList.copyOf(split));
        } catch (ConditionFailedException e) {
            getBot().sendMessageToUserInChat(sender, chat, e.getMessage());
        } catch (UsageException e) {
            getBot().sendMessageToUserInChat(sender, chat, e.getMessage());
            ImmutableList<Command> cmd = e.getCommands();
            if (!cmd.isEmpty()) {
                getBot().sendMessageToUserInChat(
                        sender,
                        chat,
                        TextComponent.builder("Usage: ")
                            .color(TextColor.RED)
                            .append(HelpGenerator.create(e.getCommandParseResult()).getUsage())
                            .build()
                            .content()
                );
            }
        }
    }

}
