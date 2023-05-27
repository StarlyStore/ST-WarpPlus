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
        if (event.getClickedBlock() == null) return;

        Player player = event.getPlayer();
        Location location = event.getClickedBlock().getLocation();
        if (event.getAction().name().contains("RIGHT")) {
            clickedLocation.put(player.getUniqueId(), location);
            addChatListenTarget(player);
            MessageContent.getInstance().getMessageAfterPrefix(MessageType.NORMAL,"enterWarpTriggerName").ifPresent(player::sendMessage);
            return;
        }

        WarpManager manager = WarpManager.getInstance();

        if (!manager.hasTrigger(location)) {
            try {
                player.playSound(player.getLocation(), Sound.ENTITY_ENDERMEN_TELEPORT,100,2);
            } catch (NoSuchFieldError error) {
                player.playSound(player.getLocation(), Sound.valueOf("ENTITY_ENDERMAN_TELEPORT"),100,2);
            }
            MessageContent.getInstance().getMessageAfterPrefix(MessageType.ERROR, "noExistTrigger").ifPresent(player::sendMessage);
            return;
        }

        manager.removeTrigger(manager.getTrigger(location));
        MessageContent.getInstance().getMessageAfterPrefix(MessageType.NORMAL, "warpTriggerRemoveSuccess").ifPresent(player::sendMessage);
    }

    @Override
    protected void onChat(AsyncPlayerChatEvent event) {

    }

    @Override
    protected void onQuit(PlayerQuitEvent event) {

    }
}
