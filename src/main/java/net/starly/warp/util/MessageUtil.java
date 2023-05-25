package net.starly.warp.util;


import lombok.Getter;
import net.starly.warp.WarpMain;
import net.starly.warp.enums.MessageType;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;

public class MessageUtil {

    @Getter
    private static final String prefix = "[ST-WarpReloaded] ";

    public static String getMessage(MessageType messageType) {
        String message = "";

        FileConfiguration config = WarpMain.getInstance().getConfig();
        switch (messageType) {
            case WARP_COMPLETE:
                message = prefix + ChatColor.translateAlternateColorCodes('&', config.getString("messages.warpComplete"));
                break;
            case WARP_CREATE_COMPLETE:
                message = prefix + ChatColor.translateAlternateColorCodes('&', config.getString("messages.warpCreateComplete"));
                break;
            case WARP_DELETE_COMPLETE:
                message = prefix + ChatColor.translateAlternateColorCodes('&', config.getString("messages.warpDeleteComplete"));
                break;
            case UNREGISTER_SPAWN:
                message = prefix + ChatColor.translateAlternateColorCodes('&', config.getString("messages.spawnUnRegisterComplete"));
                break;
            case REGISTER_SPAWN:
                message = prefix + ChatColor.translateAlternateColorCodes('&', config.getString("messages.spawnRegisterComplete"));
                break;
            case WARP_LIST_TITLE:
                message = prefix + ChatColor.translateAlternateColorCodes('&', config.getString("messages.warpListTitle"));
                break;
            case WARP_LIST_FORMAT:
                message = prefix + ChatColor.translateAlternateColorCodes('&', config.getString("messages.warpListFormat"));
                break;
            case FORCE_TELEPORTED:
                message = prefix + ChatColor.translateAlternateColorCodes('&', config.getString("messages.forceTeleported"));
                break;
            case COMMAND_CREATE_SUCCESS:
                message = prefix + ChatColor.translateAlternateColorCodes('&', config.getString("messages.commandCreateSuccess"));
                break;
            case COMMAND_DELETE_SUCCESS:
                message = prefix + ChatColor.translateAlternateColorCodes('&', config.getString("messages.commandDeleteSuccess"));
                break;
            case PERMISSION_DENIED:
                message = prefix + ChatColor.translateAlternateColorCodes('&', config.getString("errorMessages.permissionDenied"));
                break;
            case NO_EXIST_WARP_DELETE:
                message = prefix + ChatColor.translateAlternateColorCodes('&', config.getString("errorMessages.noExistWarpDelete"));
                break;
            case EXIST_WARP_NAME:
                message = prefix + ChatColor.translateAlternateColorCodes('&', config.getString("errorMessages.existWarpName"));
                break;
            case NO_EXIST_WARP_MOVE:
                message = prefix + ChatColor.translateAlternateColorCodes('&', config.getString("errorMessages.noExistWarpMove"));
                break;
            case WRONG_PLATFORM:
                message = prefix + ChatColor.translateAlternateColorCodes('&', config.getString("errorMessages.wrongPlatform"));
                break;
            case NO_EXIST_SPAWN_MOVE:
                message = prefix + ChatColor.translateAlternateColorCodes('&', config.getString("errorMessages.noExistSpawnMove"));
                break;
            case NO_EXIST_SPAWN_DELETE:
                message = prefix + ChatColor.translateAlternateColorCodes('&', config.getString("errorMessages.noExistSpawnDelete"));
                break;
            case EMPTY_WARP_LIST:
                message = prefix + ChatColor.translateAlternateColorCodes('&', config.getString("errorMessages.emptyWarpList"));
                break;
            case NO_EXIST_PLAYER_WARP:
                message = prefix + ChatColor.translateAlternateColorCodes('&', config.getString("errorMessages.noExistPlayerWarp"));
                break;
            case EXIST_COMMAND:
                message = prefix + ChatColor.translateAlternateColorCodes('&', config.getString("errorMessages.existCommand"));
                break;
            case NO_EXIST_COMMAND:
                message = prefix + ChatColor.translateAlternateColorCodes('&', config.getString("errorMessages.noExistCommand"));
                break;
            case NO_EXIST_WARP_REGISTER_COMMAND:
                message = prefix + ChatColor.translateAlternateColorCodes('&', config.getString("errorMessages.noExistWarpRegisterCommand"));
                break;
        }
        return message;
    }
}
