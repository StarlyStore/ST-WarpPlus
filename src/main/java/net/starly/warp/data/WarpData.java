package net.starly.warp.data;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import net.starly.warp.WarpMain;
import org.bukkit.Location;
import org.bukkit.World;

import java.util.HashMap;
import java.util.Map;

@AllArgsConstructor
@Getter
public class WarpData {
    private String name;
    private Location location;

    public Map<String, String> serialize() {
        Map<String, String> result = new HashMap<>();
        result.put("name",name);
        result.put("world", location.getWorld().getName());
        result.put("x", String.valueOf(location.getX()));
        result.put("y", String.valueOf(location.getY()));
        result.put("z", String.valueOf(location.getZ()));
        result.put("pitch", String.valueOf(location.getPitch()));
        result.put("yaw", String.valueOf(location.getYaw()));
        return result;
    }

    public static WarpData deserialize(Map<String, String> map) {
        String name = map.get("name");
        World world = WarpMain.getInstance().getServer().getWorld(map.get("world"));

        double x = Double.parseDouble(map.get("x"));
        double y = Double.parseDouble(map.get("y"));
        double z = Double.parseDouble(map.get("z"));

        float pitch = Float.parseFloat(map.get("pitch"));
        float yaw = Float.parseFloat(map.get("yaw"));


        return new WarpData(name, new Location(world,x,y,z,pitch,yaw));
    }
}
