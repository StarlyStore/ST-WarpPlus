package net.starly.warp.manager;

import com.google.common.reflect.TypeToken;
import com.google.gson.*;
import lombok.Getter;
import net.starly.warp.WarpMain;
import net.starly.warp.command.CustomCommand;
import net.starly.warp.data.WarpData;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.*;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.*;
import java.util.logging.Level;

public class WarpManager {
    private static WarpManager instance;

    public static WarpManager getInstance() {
        if (instance == null) instance = new WarpManager();
        return instance;
    }

    private final JavaPlugin plugin = WarpMain.getInstance();

    @Getter
    private final List<WarpData> warps = new ArrayList<>();

    @Getter
    private final List<CustomCommand> commandList = new ArrayList<>();

    public void loadData() {
        try {
            if (!plugin.getDataFolder().exists()) plugin.getDataFolder().mkdir();

            File file = new File(plugin.getDataFolder(), "data.json");
            if (!file.exists()) file.createNewFile();

            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            Reader fileReader = Files.newBufferedReader(file.toPath());

            JsonObject json = gson.fromJson(fileReader, JsonObject.class);

            if (!json.has("warpData")) return;

            JsonArray dataArray = json.getAsJsonArray("warpData");
            for (JsonElement element : dataArray) {
                Map<String, String> map = gson.fromJson(element.getAsString(), new TypeToken<Map<String, String>>() {
                }.getType());

                WarpData warpData = WarpData.deserialize(map);

                warps.add(warpData);
            }

            if (!json.has("commandData")) return;

            dataArray = json.getAsJsonArray("commandData");
            for (JsonElement element : dataArray) {
                Map<String, String> map = gson.fromJson(element.getAsString(), new TypeToken<Map<String, String>>() {
                }.getType());

                registerCustomCommand(map.get("name"), getWarp(map.get("warp")));
            }

        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    public WarpData getWarp(String name) {
        for (WarpData warpData : warps) {
            if (warpData.getName().equalsIgnoreCase(name)) return warpData;
        }
        return null;
    }

    public void addWarp(WarpData warpData) {
        if (warps.contains(warpData)) return;
        warps.add(warpData);
    }

    public void removeWarp(WarpData warpData) {
        if (!warps.contains(warpData)) return;
        for (CustomCommand command : commandList) {
            if (command.getWarpData().equals(warpData)) {
                command.unregister(command.getCommandMap());
                break;
            }
        }
        warps.remove(warpData);
    }

    public boolean has(String name) {
        for (WarpData data : warps) {
            if (data.getName().equalsIgnoreCase(name)) {
                return true;
            }
        }

        return false;
    }

    public boolean hasCommand(String name) {
        for (CustomCommand command : commandList) {
            if (command.getName().equalsIgnoreCase(name)) {
                return true;
            }
        }
        return false;
    }

    public void registerCustomCommand(String name, WarpData warpData) {
        if (hasCommand(name)) return;
        commandList.add(new CustomCommand(name, warpData));
    }

    public void removeCustomCommand(String name) {
        for (CustomCommand command : commandList) {
            if (command.getName().equalsIgnoreCase(name)) {
                commandList.remove(command);
                return;
            }
        }
    }

    public void saveData() {
        try {
            if (!plugin.getDataFolder().exists()) plugin.getDataFolder().mkdir();

            File file = new File(plugin.getDataFolder(), "data.json");
            if (!file.exists()) file.createNewFile();

            Writer writer = Files.newBufferedWriter(file.toPath(), StandardCharsets.UTF_8);

            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            JsonObject json = new JsonObject();
            JsonArray warpArray = new JsonArray();

            for (WarpData warpData : warps) {
                warpArray.add(gson.toJson(warpData.serialize(), new TypeToken<Map<String, String>>() {}.getType()));
            }

            JsonArray commandArray = new JsonArray();
            for (CustomCommand command : commandList) {
                commandArray.add(gson.toJson(command.serialize(), new TypeToken<Map<String, String>>() {}.getType()));
            }

            json.add("warpData", warpArray);
            json.add("commandData", commandArray);

            gson.toJson(json, writer);

            writer.flush();
            writer.close();

        } catch (Exception exception) {
            exception.printStackTrace();
        }

        for (CustomCommand command : commandList) {
            command.unregister(command.getCommandMap());
        }
    }
}
