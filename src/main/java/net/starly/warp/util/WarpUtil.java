package net.starly.warp.util;

import net.starly.warp.data.WarpData;
import net.starly.warp.enums.MessageType;
import net.starly.warp.manager.WarpManager;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class WarpUtil {
    public static void teleport(WarpData warpData, Player player) {
        if (!WarpManager.getInstance().has(warpData.getName())) return;
        player.teleport(warpData.getLocation());
        try {
            player.playSound(player.getLocation(), Sound.ENTITY_ENDERMEN_TELEPORT,100,0);
        } catch (NoSuchFieldError error) {
            player.playSound(player.getLocation(), Sound.valueOf("ENTITY_ENDERMAN_TELEPORT"),100,0);
        }
        player.sendMessage(MessageUtil.getMessage(MessageType.WARP_COMPLETE));
    }

    public static void forceTeleport(WarpData warpData, Player target, CommandSender user) {
        if (!WarpManager.getInstance().has(warpData.getName())) return;
        target.teleport(warpData.getLocation());
        try {
            target.playSound(target.getLocation(), Sound.ENTITY_ENDERMEN_TELEPORT,100,0);
        } catch (NoSuchFieldError error) {
            target.playSound(target.getLocation(), Sound.valueOf("ENTITY_ENDERMAN_TELEPORT"),100,0);
        }
        target.sendMessage(MessageUtil.getMessage(MessageType.FORCE_TELEPORTED).replace("%user%",user.getName()));
        user.sendMessage(MessageUtil.getMessage(MessageType.WARP_COMPLETE));
    }

    public static void registerWarp(String name, Location location) {
        if (WarpManager.getInstance().has(name)) return;
        WarpManager.getInstance().addWarp(new WarpData(name,location));
    }

    public static void unRegisterWarp(String name) {
        if (!WarpManager.getInstance().has(name)) return;
        WarpManager.getInstance().removeWarp(WarpManager.getInstance().getWarp(name));
    }
}
