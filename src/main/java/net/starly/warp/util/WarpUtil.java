package net.starly.warp.util;

import net.starly.warp.context.MessageContent;
import net.starly.warp.context.MessageType;
import net.starly.warp.data.WarpData;
import net.starly.warp.manager.WarpManager;
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
        MessageContent.getInstance().getMessageAfterPrefix(MessageType.NORMAL, "warpComplete").ifPresent(player::sendMessage);
    }

    public static void forceTeleport(WarpData warpData, Player target, CommandSender user) {
        if (!WarpManager.getInstance().has(warpData.getName())) return;
        target.teleport(warpData.getLocation());
        try {
            target.playSound(target.getLocation(), Sound.ENTITY_ENDERMEN_TELEPORT,100,0);
        } catch (NoSuchFieldError error) {
            target.playSound(target.getLocation(), Sound.valueOf("ENTITY_ENDERMAN_TELEPORT"),100,0);
        }
        MessageContent.getInstance().getMessageAfterPrefix(MessageType.NORMAL, "forceTeleported").ifPresent(message -> {
            String formattedMessage = message.replace("%user%", user.getName());
            target.sendMessage(formattedMessage);
        });
        MessageContent.getInstance().getMessageAfterPrefix(MessageType.NORMAL, "warpComplete").ifPresent(user::sendMessage);
    }
}
