package net.starly.warp.util;

import net.starly.warp.data.WarpData;
import net.starly.warp.enums.MessageType;
import net.starly.warp.manager.WarpManager;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

public class WarpUtil {
    public static void teleport(WarpData warpData, Player player) {
        player.teleport(warpData.getLocation());
        player.playSound(player.getLocation(), Sound.ENTITY_ENDERMEN_TELEPORT,100,0);
        player.sendMessage(MessageUtil.getMessage(MessageType.WARP_COMPLETE));
    }

    public static void registerWarp(String name, Location location) {
        WarpData warpData = new WarpData(name,location);
        if (WarpManager.getInstance().has(warpData)) return;
        WarpManager.getInstance().addWarp(warpData);
    }

    public static void unRegisterWarp(String name) {
        WarpData warpData = WarpManager.getInstance().getWarp(name);
        if (warpData == null) return;
        WarpManager.getInstance().removeWarp(warpData);
    }
}
