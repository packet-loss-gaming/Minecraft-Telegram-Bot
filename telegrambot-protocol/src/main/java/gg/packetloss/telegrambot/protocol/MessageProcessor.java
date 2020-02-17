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

package gg.packetloss.telegrambot.protocol;

import com.google.gson.Gson;
import gg.packetloss.telegrambot.protocol.event.EventType;
import gg.packetloss.telegrambot.protocol.event.ProtocolEvent;
import gg.packetloss.telegrambot.protocol.event.generic.GenericNothingEvent;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Deque;

public class MessageProcessor {
    private final Gson gson = new Gson();
    private final PlatformAdapter adapter;

    public MessageProcessor(PlatformAdapter adapter) {
        this.adapter = adapter;
    }

    private void doInbound(BufferedReader in) throws IOException {
        try {
            EventType eventType = gson.fromJson(in.readLine(), EventType.class);
            ProtocolEvent event = gson.fromJson(in.readLine(), eventType.getProtocolClass());

            adapter.acceptIncoming(event);
        } catch (NullPointerException ignored) { }
    }

    private void doOutbound(PrintWriter out) {
        Deque<ProtocolEvent> eventDeque = adapter.getOutbound();

        if (eventDeque.isEmpty()) {
            eventDeque.add(new GenericNothingEvent());
        }

        while (eventDeque.peek() != null) {
            ProtocolEvent event = eventDeque.poll();
            out.println(gson.toJson(event.getType()));
            out.println(gson.toJson(event));
            out.flush();
        }
    }

    public void accept(BufferedReader in, PrintWriter out) {
        while (adapter.isAlive()) {
            try {
                if (adapter.isServer()) {
                    doInbound(in);
                    doOutbound(out);
                } else {
                    doOutbound(out);
                    doInbound(in);
                }
            } catch (Throwable t) {
                t.printStackTrace();
            }
        }
    }
}
