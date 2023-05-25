package net.starly.warp.command;

import net.starly.warp.WarpMain;
import net.starly.warp.data.WarpData;
import net.starly.warp.enums.MessageType;
import net.starly.warp.manager.WarpManager;
import net.starly.warp.util.MessageUtil;
import net.starly.warp.util.WarpUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.net.InetAddress;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;

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
            /*case "스폰설정": {
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
            }*/

            case "생성": {
                if (!(sender instanceof Player)) sender.sendMessage(MessageUtil.getMessage(MessageType.WRONG_PLATFORM));
                else if (!sender.hasPermission("warp.create"))
                    sender.sendMessage(MessageUtil.getMessage(MessageType.PERMISSION_DENIED));
                else if (args.length < 2) sender.sendMessage(MessageUtil.getPrefix() + "§c사용법) /워프 생성 [<워프이름>]");
                else if (warpManager.has(args[1]))
                    sender.sendMessage(MessageUtil.getMessage(MessageType.EXIST_WARP_NAME));
                else {
                    Player player = (Player) sender;
                    WarpUtil.registerWarp(args[1], player.getLocation());
                    sender.sendMessage(MessageUtil.getMessage(MessageType.WARP_CREATE_COMPLETE));
                }
                break;
            }

            case "제거": {
                if (!sender.hasPermission("warp.delete"))
                    sender.sendMessage(MessageUtil.getMessage(MessageType.PERMISSION_DENIED));
                else if (args.length < 2) sender.sendMessage(MessageUtil.getPrefix() + "§c사용법) /워프 제거 [<워프이름>]");
                else if (!warpManager.has(args[1]))
                    sender.sendMessage(MessageUtil.getMessage(MessageType.NO_EXIST_WARP_DELETE));
                else {
                    WarpUtil.unRegisterWarp(args[1]);
                    sender.sendMessage(MessageUtil.getMessage(MessageType.WARP_DELETE_COMPLETE));
                }
                break;
            }

            case "이동": {
                if (!sender.hasPermission("warp.teleport") && !sender.hasPermission("warp.forcedteleport"))
                    sender.sendMessage(MessageUtil.getMessage(MessageType.PERMISSION_DENIED));
                else if (args.length < 2) {
                    if (sender.hasPermission("warp.forcedteleport"))
                        sender.sendMessage(MessageUtil.getPrefix() + "§c사용법) /워프 이동 [<워프이름>] [<플레이어>]");
                    else sender.sendMessage(MessageUtil.getPrefix() + "§c사용법) /워프 이동 [<플레이어>]");
                } else if (!warpManager.has(args[1]))
                    sender.sendMessage(MessageUtil.getMessage(MessageType.NO_EXIST_WARP_MOVE));
                else {
                    if (args.length > 2) {
                        if (!sender.hasPermission("warp.forcedteleport")) {
                            sender.sendMessage(MessageUtil.getMessage(MessageType.PERMISSION_DENIED));
                            break;
                        }
                        Player player = Bukkit.getPlayer(args[2]);
                        if (player == null) {
                            sender.sendMessage(MessageUtil.getMessage(MessageType.NO_EXIST_PLAYER_WARP));
                            break;
                        }
                        WarpUtil.forceTeleport(warpManager.getWarp(args[1]), player, sender);
                    } else {
                        if (!sender.hasPermission("warp.teleport")) {
                            sender.sendMessage(MessageUtil.getMessage(MessageType.PERMISSION_DENIED));
                            break;
                        }

                        if (!(sender instanceof Player)) {
                            sender.sendMessage(MessageUtil.getMessage(MessageType.WRONG_PLATFORM));
                            break;
                        }
                        Player player = (Player) sender;
                        WarpUtil.teleport(warpManager.getWarp(args[1]), player);
                    }
                }
                break;
            }

            case "목록": {
                if (!sender.hasPermission("warp.list")) {
                    sender.sendMessage(MessageUtil.getMessage(MessageType.PERMISSION_DENIED));
                    break;
                } else if (warpManager.getWarps().size() == 0) {
                    sender.sendMessage(MessageUtil.getMessage(MessageType.EMPTY_WARP_LIST));
                    break;
                }

                int page = args.length > 1 ? Integer.parseInt(args[1]) - 1 : 0;
                int max = (page * 10) + 10;
                int size = warpManager.getWarps().size();

                List<WarpData> warpDataList = warpManager.getWarps();

                if ((page * 10) > size) page = 0;

                plugin.getLogger().log(Level.INFO, MessageUtil.getMessage(MessageType.WARP_LIST_TITLE));

                for (int i = page * 10; i < max; i++) {
                    if (i > size - 1) break;
                    WarpData warpData = warpDataList.get(i);
                    String message = MessageUtil.getMessage(MessageType.WARP_LIST_FORMAT).replace("%warpname%", warpData.getName());
                    plugin.getLogger().log(Level.INFO, message);
                }
                sender.sendMessage("/대기열 목록 [<페이지>]로 더 많은 페이지를 확인해보세요!");
                break;
            }

            case "명령생성": {
                if (!sender.hasPermission("warp.commandCreate")) {
                    sender.sendMessage(MessageUtil.getMessage(MessageType.PERMISSION_DENIED));
                    break;
                } else if (args.length < 3) {
                    sender.sendMessage("§c사용법 ) /워프 명령생성 [<명령어>] [<워프>]");
                    break;
                } else if (!warpManager.has(args[2])) {
                    sender.sendMessage(MessageUtil.getMessage(MessageType.NO_EXIST_WARP_REGISTER_COMMAND));
                    break;
                } else if (warpManager.hasCommand(args[1])) {
                    sender.sendMessage(MessageUtil.getMessage(MessageType.EXIST_COMMAND));
                    break;
                }

                warpManager.registerCustomCommand(args[1],warpManager.getWarp(args[2]));
                sender.sendMessage(MessageUtil.getMessage(MessageType.COMMAND_CREATE_SUCCESS));
                break;

            }

            case "명령삭제": {
                if (!sender.hasPermission("warp.commandDelete")) {
                    sender.sendMessage(MessageUtil.getMessage(MessageType.PERMISSION_DENIED));
                    break;
                } else if (args.length < 2) {
                    sender.sendMessage("§c사용법 ) /워프 명령삭제 [<명령어>]");
                    break;
                } else if (!warpManager.hasCommand(args[1])) {
                    sender.sendMessage(MessageUtil.getMessage(MessageType.NO_EXIST_COMMAND));
                    break;
                }

                warpManager.removeCustomCommand(args[1]);
                sender.sendMessage(MessageUtil.getMessage(MessageType.COMMAND_DELETE_SUCCESS));
                break;
            }

        }

        return false;
    }
}
