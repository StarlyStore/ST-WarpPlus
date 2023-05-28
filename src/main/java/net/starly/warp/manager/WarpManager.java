package net.starly.warp.manager;

import com.google.common.reflect.TypeToken;
import com.google.gson.*;
import lombok.Getter;
import net.starly.warp.WarpMain;
import net.starly.warp.data.WarpData;
import net.starly.warp.data.WarpTrigger;
import org.bukkit.Location;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.*;

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
    private final List<WarpTrigger> triggerList = new ArrayList<>();

    public void loadData() {
        try {
            if (!plugin.getDataFolder().exists()) plugin.getDataFolder().mkdir();

            File file = new File(plugin.getDataFolder(), "data.json");
            if (!file.exists()) System.out.println(file.createNewFile());

            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            Reader fileReader = Files.newBufferedReader(file.toPath());

            JsonObject json = gson.fromJson(fileReader, JsonObject.class);

            if (json == null) return;

            if (!json.has("warpData")) return;

            JsonArray dataArray = json.getAsJsonArray("warpData");
            for (JsonElement element : dataArray) {
                Map<String, String> map = gson.fromJson(element.getAsString(), new TypeToken<Map<String, String>>() {
                }.getType());

                WarpData warpData = WarpData.deserialize(map);

                warps.add(warpData);
            }

            if (!json.has("triggerData")) return;

            dataArray = json.getAsJsonArray("triggerData");
            for (JsonElement element : dataArray) {
                Map<String, String> map = gson.fromJson(element.getAsString(), new TypeToken<Map<String, String>>() {
                }.getType());

                triggerList.add(WarpTrigger.deserialize(map));
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
        for (WarpTrigger trigger : triggerList) {
            if (trigger.getTargetWarp().equals(warpData)) {
                triggerList.remove(trigger);
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

    public void addTrigger(WarpData target, Location location) {
        if (hasTrigger(location)) return;
        triggerList.add(new WarpTrigger(target, location));
    }

    public void removeTrigger(WarpTrigger trigger) {
        if (!triggerList.contains(trigger)) return;
        triggerList.remove(trigger);
    }

    public WarpTrigger getTrigger(Location location) {
        for (WarpTrigger trigger : triggerList) {
            if (trigger.getLocation().getBlock().equals(location.getBlock())) {
                return trigger;
            }
        }
        return null;
    }

    public boolean hasTrigger(Location location) {
        for (WarpTrigger trigger : triggerList) {
            if (trigger.getLocation().getBlock().equals(location.getBlock())) {
                return true;
            }
        }
        return false;
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

            JsonArray triggerArray = new JsonArray();
            for (WarpTrigger trigger : triggerList) {
                triggerArray.add(gson.toJson(trigger.serialize(), new TypeToken<Map<String, String>>() {}.getType()));
            }

            json.add("warpData", warpArray);
            json.add("triggerData", triggerArray);

            gson.toJson(json, writer);

            writer.flush();
            writer.close();

        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }
}
