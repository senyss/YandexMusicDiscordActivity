package com.senyss.YMDA;

import club.minnced.discord.rpc.DiscordEventHandlers;
import club.minnced.discord.rpc.DiscordRPC;
import club.minnced.discord.rpc.DiscordRichPresence;
import org.json.JSONObject;

import java.io.*;
import java.nio.charset.StandardCharsets;


class App {
    public static void main(String[] args) {

        Long[] timebuf = {0L};
        boolean alive = true;
        StringBuilder detail = new StringBuilder();
        StringBuilder state = new StringBuilder();
        StringBuilder albumName = new StringBuilder();
        final boolean[] trackChange = {false};
        final boolean[] timeSkip = {false};
        final boolean[] onPause = {false};
        final boolean[] curOnPause = {false};

        Thread post = new Thread() {
            public void run() {
                System.err.println("started post");
                DiscordRPC lib = DiscordRPC.INSTANCE;
                DiscordRichPresence presence = new DiscordRichPresence();
                String applicationId = "954700674287939674";
                String steamID = "";
                DiscordEventHandlers handlers = new DiscordEventHandlers();
                lib.Discord_Initialize(applicationId, handlers, true, steamID);
                presence.details = " ";
                presence.state = " ";
                while (alive) {
                    if (trackChange[0] || timeSkip[0] || onPause[0]) {
                        presence.startTimestamp = System.currentTimeMillis() - timebuf[0];
                        if (onPause[0]) {
                            curOnPause[0] = true;
                            onPause[0] = false;
                            presence.startTimestamp = 0L;
                        }
                        if (trackChange[0]) {
                            presence.largeImageKey = "1";
                        }
                        presence.state = state.toString();
                        presence.details = detail.toString();
                        timeSkip[0] = false;
                        trackChange[0] = false;
                        lib.Discord_UpdatePresence(presence);

                        try {
                            Thread.sleep(14000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    else {
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }

            }
        };
        Thread get = new Thread() {
            InputStream std = System.in;
            public void run() {
                long temp = 0;
                System.err.println("started get");
                while (alive) {
                    try {
                        if (std.available() > 0) {
                            onPause[0] = false;
                            System.err.println("recived + \n");
                            int length = 0;
                            for (int i = 0; i < 4; i++) {
                                length += std.read();
                            }
                            byte[] infob = new byte[length];
                            std.read(infob, 0, length);
                            String s = new String(infob, StandardCharsets.UTF_8);
                            JSONObject info = new JSONObject(s);

                            if (info.getString("type").contains("trackChange")) {
                                trackChange[0] = true;
                                curOnPause[0] = false;
                                JSONObject data = info.getJSONObject("data");
                                detail.setLength(0);
                                state.setLength(0);
                                albumName.setLength(0);
                                albumName.append(data.getString("album"));
                                detail.append(data.getString("title"));
                                state.append(data.get("artist").toString());
                                timebuf[0] = 0L;
                            }
                            if (info.getString("type").contains("trackProgress")) {
                                timebuf[0] = (long) info.getDouble("data") * 1000;
                                if (curOnPause[0]) {
                                    timeSkip[0] = true;
                                    curOnPause[0] = false;
                                }
                                if (timebuf[0] - temp < 3000 && timebuf[0] - temp > 0) {
                                    temp = timebuf[0];
                                }
                                else {
                                    temp = timebuf[0];
                                    timeSkip[0] = true;
                                }
                            }
                        }
                        else {
                            Thread.sleep(2000);
                            if (std.available() == 0) {
                                if (!curOnPause[0]) onPause[0] = true;
                            }
                        }
                    } catch (IOException | InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                try {
                    std.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };
        get.start();
        post.start();

    }
}
