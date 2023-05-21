package net.starly.warp.command;

import net.starly.warp.WarpMain;
import net.starly.warp.enums.MessageType;
import net.starly.warp.manager.WarpManager;
import net.starly.warp.util.MessageUtil;
import net.starly.warp.util.WarpUtil;
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
            case "스폰설정": {
                if (!(sender instanceof Player)) {
                    sender.sendMessage(MessageUtil.getMessage(MessageType.WRONG_PLATFORM));
                    break;
                }
                Player player = (Player) sender;

                if (warpManager.getWarp("spawn") != null) WarpUtil.unRegisterWarp("spawn");
                WarpUtil.registerWarp("spawn", player.getLocation());
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

            case "생성": {
                if (!(sender instanceof Player))
                    sender.sendMessage(MessageUtil.getMessage(MessageType.WRONG_PLATFORM));
                else if (!sender.hasPermission("warp.create"))
                    sender.sendMessage(MessageUtil.getMessage(MessageType.PERMISSION_DENIED));
                else if (args.length < 2)
                    sender.sendMessage(MessageUtil.getPrefix() + "§c사용법) /워프 생성 [<워프이름>]");
                else if (warpManager.has(args[1]))
                    sender.sendMessage(MessageUtil.getMessage(MessageType.EXIST_WARP_NAME));
                else {
                    Player player = (Player) sender;
                    WarpUtil.registerWarp(args[1], player.getLocation());
                    sender.sendMessage(MessageUtil.getMessage(MessageType.WARP_CREATE_COMPLETE));
                }
            }

            case "제거": {
                if (!sender.hasPermission("warp.delete"))
                    sender.sendMessage(MessageUtil.getMessage(MessageType.PERMISSION_DENIED));
                else if (args.length < 2)
                    sender.sendMessage(MessageUtil.getPrefix() + "§c사용법) /워프 제거 [<워프이름>]");
                else if (!warpManager.has(args[1]))
                    sender.sendMessage(MessageUtil.getMessage(MessageType.NO_EXIST_WARP_DELETE));
                else {
                    WarpUtil.unRegisterWarp(args[1]);
                    sender.sendMessage(MessageUtil.getMessage(MessageType.WARP_DELETE_COMPLETE));
                }
            }

            case "목록": {
                if (!sender.hasPermission("warp.list")) {
                    sender.sendMessage(MessageUtil.getMessage(MessageType.PERMISSION_DENIED));
                    break;
                }


            }

        }

        return false;
    }
}
