package net.starly.warp.command;

import lombok.AllArgsConstructor;
import net.starly.warp.WarpMain;
import net.starly.warp.context.MessageContent;
import net.starly.warp.context.MessageType;
import net.starly.warp.data.WarpData;
import net.starly.warp.manager.InputManager;
import net.starly.warp.manager.WarpStorage;
import net.starly.warp.scheduler.ParticleScheduler;
import net.starly.warp.util.WarpUtil;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.StringUtil;

import java.util.*;
import java.util.stream.Collectors;

@AllArgsConstructor
public class WarpExecutor implements TabExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        JavaPlugin plugin = WarpMain.getInstance();
        WarpStorage warpStorage = WarpStorage.getInstance();
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
                else if (args.length != 2)
                    sender.sendMessage(content.getPrefix() + "§c사용법) /워프 생성 [<워프이름>]");
                else if (warpStorage.has(args[1]))
                    content.getMessageAfterPrefix(MessageType.ERROR, "existWarpName").ifPresent(sender::sendMessage);
                else {
                    Player player = (Player) sender;
                    warpStorage.addWarp(new WarpData(args[1], player.getLocation()));
                    content.getMessageAfterPrefix(MessageType.NORMAL, "warpCreateComplete").ifPresent(sender::sendMessage);
                }
                break;
            }

            case "삭제": {
                if (!sender.hasPermission("starly.warp.delete"))
                    content.getMessageAfterPrefix(MessageType.ERROR, "permissionDenied").ifPresent(sender::sendMessage);
                else if (args.length != 2)
                    sender.sendMessage(content.getPrefix() + "§c사용법) /워프 삭제 [<워프이름>]");
                else if (!warpStorage.has(args[1]))
                    content.getMessageAfterPrefix(MessageType.ERROR, "noExistWarpDelete").ifPresent(sender::sendMessage);
                else {
                    warpStorage.removeWarp(warpStorage.getWarp(args[1]));
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
                } else if (!warpStorage.has(args[1]))
                    content.getMessageAfterPrefix(MessageType.ERROR, "noExistWarpMove").ifPresent(sender::sendMessage);
                else {
                    if (args.length == 3) {
                        if (!sender.hasPermission("starly.warp.forcedteleport")) {
                            content.getMessageAfterPrefix(MessageType.ERROR, "permissionDenied").ifPresent(sender::sendMessage);
                            break;
                        }
                        Player player = Bukkit.getPlayer(args[2]);
                        if (player == null) {
                            content.getMessageAfterPrefix(MessageType.ERROR, "noExistPlayerWarp").ifPresent(sender::sendMessage);
                            break;
                        }
                        WarpUtil.forceTeleport(warpStorage.getWarp(args[1]), player, sender);
                    } else if (args.length == 2) {
                        if (!sender.hasPermission("starly.warp.teleport")) {
                            content.getMessageAfterPrefix(MessageType.ERROR, "permissionDenied").ifPresent(sender::sendMessage);
                            break;
                        }

                        if (!(sender instanceof Player)) {
                            content.getMessageAfterPrefix(MessageType.ERROR, "wrongPlatform").ifPresent(sender::sendMessage);
                            break;
                        }
                        Player player = (Player) sender;
                        WarpUtil.teleport(warpStorage.getWarp(args[1]), player);
                        break;
                    } else {
                        if (sender.hasPermission("warp.forcedteleport"))
                            sender.sendMessage(content.getPrefix() + "§c사용법) /워프 이동 [<워프이름>] [<플레이어>]");

                        if (sender.hasPermission("warp.teleport"))
                            sender.sendMessage(content.getPrefix() + "§c사용법) /워프 이동 [<워프이름>] [<플레이어>]");
                        break;
                    }
                }
                break;
            }

            case "목록": {
                if (!sender.hasPermission("starly.warp.list")) {
                    content.getMessageAfterPrefix(MessageType.ERROR, "permissionDenied").ifPresent(sender::sendMessage);
                    break;
                } else if (warpStorage.getWarps().size() == 0) {
                    content.getMessageAfterPrefix(MessageType.ERROR, "emptyWarpList").ifPresent(sender::sendMessage);
                    break;
                }

                int page = args.length > 1 ? Integer.parseInt(args[1]) - 1 : 0;
                int max = (page * 10) + 10;
                int size = warpStorage.getWarps().size();

                List<WarpData> warpDataList = warpStorage.getWarps();

                if ((page * 10) > size) page = 0;

                content.getMessageAfterPrefix(MessageType.NORMAL, "warpListTitle").ifPresent(sender::sendMessage);

                for (int i = page * 10; i < max; i++) {
                    if (i > size - 1) break;
                    WarpData warpData = warpDataList.get(i);
                    content.getMessageAfterPrefix(MessageType.NORMAL, "warpListFormat").ifPresent(message -> {
                        String formattedMessage = message.replace("%warpname%", warpData.getName());
                        sender.sendMessage(formattedMessage);
                    });
                }
                content.getMessageAfterPrefix(MessageType.NORMAL, "warpListFooter").ifPresent(sender::sendMessage);
                break;
            }

            case "트리거": {
                if (!(sender instanceof Player)) {
                    content.getMessageAfterPrefix(MessageType.ERROR, "wrongPlatform");
                    break;
                }

                if (!sender.hasPermission("starly.warp.triggercreate") && !sender.hasPermission("starly.warp.triggerdelete")) {
                    content.getMessageAfterPrefix(MessageType.ERROR, "permissionDenied").ifPresent(sender::sendMessage);
                    break;
                }

                Player player = (Player) sender;
                if (InputManager.getInstance().has(player)) {
                    InputManager.getInstance().removeTarget(player);
                    content.getMessageAfterPrefix(MessageType.NORMAL, "exitTriggerMode").ifPresent(player::sendMessage);
                    break;
                }

                InputManager.getInstance().addClickListenTarget(player);
                MessageContent.getInstance().getMessageAfterPrefix(MessageType.NORMAL, "enterSelectionMode").ifPresent(player::sendMessage);
                break;
            }

            case "리로드": {
                if (!sender.hasPermission("starly.warp.reload")) {
                    content.getMessageAfterPrefix(MessageType.ERROR, "permissionDenied").ifPresent(sender::sendMessage);
                    break;
                }

                FileConfiguration configuration = plugin.getConfig();

                if (configuration.getBoolean("settings.showParticle"))
                    ParticleScheduler.getInstance().stop();

                plugin.saveDefaultConfig();
                plugin.reloadConfig();

                configuration = plugin.getConfig();

                if (configuration.getBoolean("settings.showParticle"))
                    ParticleScheduler.getInstance().runTaskTimer(plugin, 0L, 10L);
                content.getMessageAfterPrefix(MessageType.NORMAL, "reloadComplete").ifPresent(sender::sendMessage);
                break;
            }

            default: {
                content.getMessages(MessageType.NORMAL, "helpMessage").forEach(message -> {
                    String formattedMessage = content.getPrefix() + " " + message;
                    sender.sendMessage(formattedMessage);
                });
                break;
            }

        }

        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        List<String> result = new ArrayList<>();
        if (args.length == 1) {
            if (sender.hasPermission("starly.warp.list")) result.add("목록");
            if (sender.hasPermission("starly.warp.create")) result.add("생성");
            if (sender.hasPermission("starly.warp.delete")) result.add("삭제");
            if (sender.hasPermission("starly.warp.reload")) result.add("리로드");
            if (sender.hasPermission("starly.warp.triggerCreate")) result.add("트리거");
            else if (sender.hasPermission("starly.warp.triggerDelete")) result.add("트리거");

            return StringUtil.copyPartialMatches(args[0], result, new ArrayList<>());
        } else if (args.length == 2) {
            if (!args[0].equalsIgnoreCase("삭제") && !args[0].equalsIgnoreCase("이동"))
                return Collections.emptyList();
            else if (!sender.hasPermission("starly.warp.delete") && !sender.hasPermission("starly.warp.teleport"))
                return Collections.emptyList();
            else return WarpStorage.getInstance().getWarps()
                        .stream()
                        .map(WarpData::getName)
                        .filter(name -> name.startsWith(args[1]))
                        .collect(Collectors.toList());
        } else if (args.length == 3 && args[0].equalsIgnoreCase("이동")) {
            if (!sender.hasPermission("starly.warp.forcedteleport"))
                return Collections.emptyList();
            return null;
        }
        return Collections.emptyList();
    }
}
