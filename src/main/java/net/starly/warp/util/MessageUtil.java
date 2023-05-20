package net.starly.warp.util;

import net.starly.warp.WarpMain;
import net.starly.warp.enums.MessageType;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;

public class MessageUtil {
    public static String getMessage(MessageType messageType) {
        final String prefix = "[ST-WarpReloaded] ";
        String message = "";

        FileConfiguration config = WarpMain.getInstance().getConfig();
        switch (messageType) {
            case WARP_COMPLETE:
                message = prefix + ChatColor.translateAlternateColorCodes('&', config.getString("messages.warpComplete"));
            case WARP_CREATE_COMPLETE:
                message = prefix + ChatColor.translateAlternateColorCodes('&', config.getString("messages.warpCreateComplete"));
            case WARP_DELETE_COMPLETE:
                message = prefix + ChatColor.translateAlternateColorCodes('&', config.getString("messages.warpDeleteComplete"));
            case UNREGISTER_SPAWN:
                message = prefix + ChatColor.translateAlternateColorCodes('&', config.getString("messages.spawnUnRegisterComplete"));
            case REGISTER_SPAWN:
                message = prefix + ChatColor.translateAlternateColorCodes('&', config.getString("messages.spawnRegisterComplete"));
            case PERMISSION_DENIED:
                message = prefix + ChatColor.translateAlternateColorCodes('&', config.getString("errorMessages.permissionDenied"));
            case NO_EXIST_WARP_DELETE:
                message = prefix + ChatColor.translateAlternateColorCodes('&', config.getString("errorMessages.noExistWarpDelete"));
            case EXIST_WARP_NAME:
                message = prefix + ChatColor.translateAlternateColorCodes('&', config.getString("errorMessages.existWarpName"));
            case NO_EXIST_WARP_MOVE:
                message = prefix + ChatColor.translateAlternateColorCodes('&', config.getString("errorMessages.noExistWarpMove"));
            case WRONG_PLATFORM:
                message = prefix + ChatColor.translateAlternateColorCodes('&', config.getString("errorMessages.wrongPlatform"));
            case NO_EXIST_SPAWN_MOVE:
                message = prefix + ChatColor.translateAlternateColorCodes('&', config.getString("errorMessages.noExistSpawnMove"));
            case NO_EXIST_SPAWN_DELETE:
                message = prefix + ChatColor.translateAlternateColorCodes('&', config.getString("errorMessages.noExistSpawnDelete"));
        }
        return message;
    }
}
