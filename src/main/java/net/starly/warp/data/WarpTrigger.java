package net.starly.warp.data;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import net.starly.warp.WarpMain;
import net.starly.warp.manager.WarpStorage;
import org.bukkit.Location;
import org.bukkit.World;

import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
@AllArgsConstructor
public class WarpTrigger {
    private WarpData targetWarp;
    private Location location;

    public Map<String, String> serialize() {
        Map<String, String> result = new HashMap<>();
        result.put("target", targetWarp.getName());
        result.put("world", location.getWorld().getName());
        result.put("x", String.valueOf(location.getX()));
        result.put("y", String.valueOf(location.getY()));
        result.put("z", String.valueOf(location.getZ()));
        result.put("pitch", String.valueOf(location.getPitch()));
        result.put("yaw", String.valueOf(location.getYaw()));
        return result;
    }

    public static WarpTrigger deserialize(Map<String, String> map) {
        String name = map.get("target");
        World world = WarpMain.getInstance().getServer().getWorld(map.get("world"));

        double x = Double.parseDouble(map.get("x"));
        double y = Double.parseDouble(map.get("y"));
        double z = Double.parseDouble(map.get("z"));

        float pitch = Float.parseFloat(map.get("pitch"));
        float yaw = Float.parseFloat(map.get("yaw"));

        if (WarpStorage.getInstance().getWarp(name) == null) return null;

        return new WarpTrigger(WarpStorage.getInstance().getWarp(name), new Location(world,x,y,z,pitch,yaw));
    }
}
