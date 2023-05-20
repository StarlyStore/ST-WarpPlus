package net.starly.warp.command;

import net.starly.warp.WarpMain;
import net.starly.warp.enums.MessageType;
import net.starly.warp.manager.WarpManager;
import net.starly.warp.util.MessageUtil;
import net.starly.warp.util.WarpUtil;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class WarpCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        JavaPlugin plugin = WarpMain.getInstance();
        WarpManager warpManager = WarpManager.getInstance();
        if (args.length == 0) {
            sender.sendMessage(plugin.getName() + " version " + plugin.getDescription().getVersion());
            return true;
        }

        switch (args[0]) {
            case "스폰": {
                if (!(sender instanceof Player)) {
                    sender.sendMessage(MessageUtil.getMessage(MessageType.WRONG_PLATFORM));
                    break;
                }
                Player player = (Player) sender;

                if (warpManager.getWarp("spawn") != null) WarpUtil.unRegisterWarp("spawn");
                WarpUtil.registerWarp("spawn",player.getLocation());
                player.sendMessage(MessageUtil.getMessage(MessageType.REGISTER_SPAWN));
                break;
            }

            case "스폰제거": {
                if (warpManager.getWarp("spawn") == null) {
                    sender.sendMessage(MessageUtil.getMessage(MessageType.NO_EXIST_SPAWN_DELETE));
                    break;
                }
                WarpUtil.unRegisterWarp("spawn");
                sender.sendMessage(MessageUtil.getMessage(MessageType.UNREGISTER_SPAWN));
                break;
            }
        }

        return false;
    }
}
