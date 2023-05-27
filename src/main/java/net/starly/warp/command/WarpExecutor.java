package net.starly.warp.command;

import lombok.AllArgsConstructor;
import net.starly.warp.WarpMain;
import net.starly.warp.context.MessageContent;
import net.starly.warp.context.MessageType;
import net.starly.warp.data.WarpData;
import net.starly.warp.manager.WarpManager;
import net.starly.warp.util.WarpUtil;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Collections;
import java.util.List;
import java.util.logging.Level;

@AllArgsConstructor
public class WarpExecutor implements TabExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        JavaPlugin plugin = WarpMain.getInstance();
        WarpManager warpManager = WarpManager.getInstance();
        MessageContent content = MessageContent.getInstance();
        if (args.length == 0) {
            sender.sendMessage(plugin.getName() + " version " + plugin.getDescription().getVersion());
            return true;
        }

        switch (args[0]) {

            case "생성": {
                if (!(sender instanceof Player))
                    content.getMessageAfterPrefix(MessageType.ERROR, "wrongPlatform").ifPresent(sender::sendMessage);
                else if (!sender.hasPermission("starly.warp.create"))
                    content.getMessageAfterPrefix(MessageType.ERROR, "permissionDenied").ifPresent(sender::sendMessage);
                else if (args.length < 2)
                    sender.sendMessage(content.getPrefix() + "§c사용법) /워프 생성 [<워프이름>]");
                else if (warpManager.has(args[1]))
                    content.getMessageAfterPrefix(MessageType.ERROR, "existWarpName").ifPresent(sender::sendMessage);
                else {
                    Player player = (Player) sender;
                    warpManager.addWarp(new WarpData(args[1], player.getLocation()));
                    content.getMessageAfterPrefix(MessageType.NORMAL, "warpCreateComplete").ifPresent(sender::sendMessage);
                }
                break;
            }

            case "제거": {
                if (!sender.hasPermission("starly.warp.delete"))
                    content.getMessageAfterPrefix(MessageType.ERROR, "permissionDenied").ifPresent(sender::sendMessage);
                else if (args.length < 2)
                    sender.sendMessage(content.getPrefix() + "§c사용법) /워프 제거 [<워프이름>]");
                else if (!warpManager.has(args[1]))
                    content.getMessageAfterPrefix(MessageType.ERROR, "noExistWarpDelete").ifPresent(sender::sendMessage);
                else {
                    warpManager.removeWarp(warpManager.getWarp(args[1]));
                    content.getMessageAfterPrefix(MessageType.NORMAL, "warpDeleteComplete").ifPresent(sender::sendMessage);
                }
                break;
            }

            case "이동": {
                if (!sender.hasPermission("starly.warp.teleport") && !sender.hasPermission("starly.warp.forcedteleport"))
                    content.getMessageAfterPrefix(MessageType.ERROR, "permissionDenied").ifPresent(sender::sendMessage);
                else if (args.length < 2) {
                    if (sender.hasPermission("warp.forcedteleport"))
                        sender.sendMessage(content.getPrefix() + "§c사용법) /워프 이동 [<워프이름>] [<플레이어>]");
                    else sender.sendMessage(content.getPrefix() + "§c사용법) /워프 이동 [<워프이름>]");
                } else if (!warpManager.has(args[1]))
                    content.getMessageAfterPrefix(MessageType.ERROR, "noExistWarpMove").ifPresent(sender::sendMessage);
                else {
                    if (args.length > 2) {
                        if (!sender.hasPermission("starly.warp.forcedteleport")) {
                            content.getMessageAfterPrefix(MessageType.ERROR, "permissionDenied").ifPresent(sender::sendMessage);
                            break;
                        }
                        Player player = Bukkit.getPlayer(args[2]);
                        if (player == null) {
                            content.getMessageAfterPrefix(MessageType.ERROR, "noExistPlayerWarp").ifPresent(sender::sendMessage);
                            break;
                        }
                        WarpUtil.forceTeleport(warpManager.getWarp(args[1]), player, sender);
                    } else {
                        if (!sender.hasPermission("starly.warp.teleport")) {
                            content.getMessageAfterPrefix(MessageType.ERROR, "permissionDenied").ifPresent(sender::sendMessage);
                            break;
                        }

                        if (!(sender instanceof Player)) {
                            content.getMessageAfterPrefix(MessageType.ERROR, "wrongPlatform").ifPresent(sender::sendMessage);
                            break;
                        }
                        Player player = (Player) sender;
                        WarpUtil.teleport(warpManager.getWarp(args[1]), player);
                    }
                }
                break;
            }

            case "목록": {
                if (!sender.hasPermission("starly.warp.list")) {
                    content.getMessageAfterPrefix(MessageType.ERROR, "permissionDenied").ifPresent(sender::sendMessage);
                    break;
                } else if (warpManager.getWarps().size() == 0) {
                    content.getMessageAfterPrefix(MessageType.ERROR, "emptyWarpList").ifPresent(sender::sendMessage);
                    break;
                }

                int page = args.length > 1 ? Integer.parseInt(args[1]) - 1 : 0;
                int max = (page * 10) + 10;
                int size = warpManager.getWarps().size();

                List<WarpData> warpDataList = warpManager.getWarps();

                if ((page * 10) > size) page = 0;

                content.getMessage(MessageType.NORMAL, "warpListTitle").ifPresent(message -> plugin.getLogger().log(Level.INFO,message));

                for (int i = page * 10; i < max; i++) {
                    if (i > size - 1) break;
                    WarpData warpData = warpDataList.get(i);
                    content.getMessage(MessageType.NORMAL, "warpListFormat").ifPresent(message -> {
                        String formattedMessage = message.replace("%warpname%", warpData.getName());
                        sender.sendMessage(formattedMessage);
                    });
                }
                content.getMessage(MessageType.NORMAL, "warpListFooter").ifPresent(sender::sendMessage);
                break;
            }

            case "트리거": {
                if (!(sender instanceof Player)) {
                    content.getMessageAfterPrefix(MessageType.ERROR, "wrongPlatform");
                    break;
                }

                if (!sender.hasPermission("starly.warp.triggerCreate") && !sender.hasPermission("starly.warp.triggerDelete")) {
                    content.getMessageAfterPrefix(MessageType.ERROR,"permissionDenied").ifPresent(sender::sendMessage);
                    break;
                }

                Player player = (Player) sender;

            }

        }

        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] strings) {
        return Collections.emptyList();
    }
}
