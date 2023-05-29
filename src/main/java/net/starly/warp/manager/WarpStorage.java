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

public class WarpStorage {
    private static WarpStorage instance;

    public static WarpStorage getInstance() {
        if (instance == null) instance = new WarpStorage();
        return instance;
    }

    private final JavaPlugin plugin = WarpMain.getInstance();

    @Getter
    private final List<WarpData> warps = new ArrayList<>();

    @Getter
    private final List<WarpTrigger> triggerList = new ArrayList<>();

    @Getter
    private final File dataFile;

    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    private WarpStorage() {
        dataFile = new File(plugin.getDataFolder(), "data.json");

        if (!dataFile.exists()) {
            try {
                dataFile.createNewFile();
                saveData();
            } catch (IOException e) { e.printStackTrace(); }
        }
    }

    public void loadData() {
        try (Reader reader = Files.newBufferedReader(dataFile.toPath())) {
            JsonObject json = gson.fromJson(reader, JsonObject.class);

            if (json == null) return;

            if (json.has("warpData") && json.has("triggerData")) {
                JsonArray warpArray = json.getAsJsonArray("warpData");
                warpArray.forEach(element -> {
                    JsonObject warpObject = element.getAsJsonObject();
                    Map<String, String> map = new HashMap<>();
                    warpObject.entrySet().forEach(entry -> {
                        map.put(entry.getKey(), entry.getValue().getAsString());
                    });
                    warps.add(WarpData.deserialize(map));
                });

                JsonArray triggerArray = json.getAsJsonArray("triggerData");
                triggerArray.forEach(element -> {
                    JsonObject warpObject = element.getAsJsonObject();
                    Map<String, String> map = new HashMap<>();
                    warpObject.entrySet().forEach(entry -> {
                        map.put(entry.getKey(), entry.getValue().getAsString());
                    });
                    triggerList.add(WarpTrigger.deserialize(map));
                });
            }
        } catch (IOException e) { e.printStackTrace(); }
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
        new ArrayList<>(triggerList).forEach(trigger -> {
            if (trigger.getTargetWarp().equals(warpData)) {
                triggerList.remove(trigger);
            }
        });
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
        try (Writer writer = Files.newBufferedWriter(dataFile.toPath(), StandardCharsets.UTF_8)) {
            JsonObject json = new JsonObject();

            JsonArray warpArray = new JsonArray();
            JsonArray triggerArray = new JsonArray();

            // 워프 목록 저장
            warps.forEach(warpData -> {
                JsonObject warpObject = new JsonObject();
                warpData.serialize().forEach(warpObject::addProperty);
                warpArray.add(warpObject);
            });
            json.add("warpData", warpArray);

            // 트리거 목록 저장
            triggerList.forEach(trigger -> {
                JsonObject triggerObject = new JsonObject();
                trigger.serialize().forEach(triggerObject::addProperty);
                triggerArray.add(triggerObject);
            });
            json.add("triggerData", triggerArray);

            gson.toJson(json, writer);
        } catch (IOException exception) {
            exception.printStackTrace();
        }

    }
}
