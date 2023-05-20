package net.starly.warp.manager;

import com.google.gson.Gson;
import net.starly.warp.WarpMain;
import net.starly.warp.data.WarpData;
import org.bukkit.Location;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class WarpManager {
    private static WarpManager instance;
    public static WarpManager getInstance() {
        if (instance == null) instance = new WarpManager();
        return instance;
    }

    private final JavaPlugin plugin = WarpMain.getInstance();

    private List<WarpData> warpDataList = new ArrayList<>();

    public void loadData() {
        try {
            if (!plugin.getDataFolder().exists()) plugin.getDataFolder().mkdir();

            File file = new File(plugin.getDataFolder().getPath() + "\\data.json");
            if (!file.exists()) file.createNewFile();

            Reader fileReader = new FileReader(plugin.getDataFolder().getPath() + "\\data.json");
            Gson gson = new Gson();
            warpDataList = Arrays.asList(gson.fromJson(fileReader, WarpData[].class));
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    public WarpData getWarp(String name) {
        for (WarpData warpData : warpDataList) {
            if (warpData.getName().equalsIgnoreCase(name)) return warpData;
        }
        return null;
    }

    public WarpData getWarp(Location location) {
        for (WarpData warpData : warpDataList) {
            if (warpData.getLocation().equals(location)) return warpData;
        }
        return null;
    }

    public void addWarp(WarpData warpData) {
        if (warpDataList.contains(warpData)) return;
        warpDataList.add(warpData);
    }

    public void removeWarp(WarpData warpData) {
        if (!warpDataList.contains(warpData)) return;
        warpDataList.remove(warpData);
    }

    public boolean has(WarpData warpData) {
        return warpDataList.contains(warpData);
    }

    public void saveData() {
        try {
            if (!plugin.getDataFolder().exists()) plugin.getDataFolder().mkdir();

            File file = new File(plugin.getDataFolder().getPath() + "\\data.json");
            if (!file.exists()) file.createNewFile();

            Writer fileWriter = new FileWriter(plugin.getDataFolder().getPath() + "\\data.json");
            Gson gson = new Gson();
            gson.toJson(warpDataList.toArray(),fileWriter);
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }
}
