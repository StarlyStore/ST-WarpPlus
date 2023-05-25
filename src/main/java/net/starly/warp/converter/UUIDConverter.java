package net.starly.warp.converter;

import java.net.URL;
import java.util.Scanner;

public class UUIDConverter {
    public static String getMinecraftUUID(String minecraftName) {
        String url = "https://api.mojang.com/users/profiles/minecraft/" + minecraftName;
        String uuid = "";
        try {
            URL api = new URL(url);
            Scanner scanner = new Scanner(api.openStream());
            String response = scanner.useDelimiter("\\A").next();
            scanner.close();
            uuid = response.substring(response.indexOf("id") + 7, response.indexOf("id") + 39);
            uuid = uuid.substring(0, 8) + "-" + uuid.substring(8, 12) + "-" + uuid.substring(12, 16) + "-"
                    + uuid.substring(16, 20) + "-" + uuid.substring(20);
        } catch (Exception e) { e.printStackTrace(); }
        return uuid;
    }
}
