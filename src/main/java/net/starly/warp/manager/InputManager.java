package net.starly.warp.manager;

import net.starly.warp.context.MessageContent;
import net.starly.warp.context.MessageType;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class InputManager extends InputListenerBase {

    private final Map<UUID, Location> clickedLocation = new HashMap<>();
    private static InputManager instance = null;

    public static InputManager getInstance() {
        if (instance == null) instance = new InputManager();
        return instance;
    }

    @Override
    protected void onClick(PlayerInteractEvent event) {
        if (event.getClickedBlock() == null) {
            addClickListenTarget(event.getPlayer());
            return;
        }
        event.setCancelled(true);

        Player player = event.getPlayer();
        Location location = event.getClickedBlock().getLocation();
        if (event.getAction().name().contains("RIGHT")) {

            if (!player.hasPermission("starly.warp.triggercreate")) {
                MessageContent.getInstance().getMessageAfterPrefix(MessageType.ERROR,"permissionDenied").ifPresent(player::sendMessage);
                return;
            }

            clickedLocation.put(player.getUniqueId(), location);
            addChatListenTarget(player);
            MessageContent.getInstance().getMessageAfterPrefix(MessageType.NORMAL,"enterWarpTriggerName").ifPresent(player::sendMessage);
            return;
        }

        if (!player.hasPermission("starly.warp.triggerdelete")) {
            MessageContent.getInstance().getMessageAfterPrefix(MessageType.ERROR,"permissionDenied").ifPresent(player::sendMessage);
            return;
        }

        WarpStorage warpStorage = WarpStorage.getInstance();

        if (!warpStorage.hasTrigger(location)) {
            try {
                player.playSound(player.getLocation(), Sound.ENTITY_ENDERMEN_TELEPORT,100,0);
            } catch (NoSuchFieldError error) {
                player.playSound(player.getLocation(), Sound.valueOf("ENTITY_ENDERMAN_TELEPORT"),100,0);
            }
            MessageContent.getInstance().getMessageAfterPrefix(MessageType.ERROR, "noExistTrigger").ifPresent(player::sendMessage);
            return;
        }

        warpStorage.removeTrigger(warpStorage.getTrigger(location));
        MessageContent.getInstance().getMessageAfterPrefix(MessageType.NORMAL, "warpTriggerRemoveSuccess").ifPresent(player::sendMessage);
    }

    @Override
    protected void onChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        String warpName = event.getMessage();
        WarpStorage warpStorage = WarpStorage.getInstance();

        event.setCancelled(true);

        if (!warpStorage.has(warpName)) {
            MessageContent.getInstance().getMessageAfterPrefix(MessageType.ERROR, "noExistWarpRegisterTrigger").ifPresent(player::sendMessage);
            clickedLocation.remove(event.getPlayer().getUniqueId());
            return;
        }
        warpStorage.addTrigger(warpStorage.getWarp(warpName), clickedLocation.get(player.getUniqueId()));
        MessageContent.getInstance().getMessageAfterPrefix(MessageType.NORMAL,"warpTriggerRegisterSuccess").ifPresent(player::sendMessage);
    }

    @Override
    protected void onQuit(PlayerQuitEvent event) {
        clickedLocation.remove(event.getPlayer().getUniqueId());
    }
}
