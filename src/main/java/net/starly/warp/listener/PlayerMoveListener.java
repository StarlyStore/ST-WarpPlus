package net.starly.warp.listener;

import net.starly.warp.data.WarpData;
import net.starly.warp.manager.WarpStorage;
import net.starly.warp.util.WarpUtil;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

public class PlayerMoveListener implements Listener {

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        WarpStorage warpStorage = WarpStorage.getInstance();
        Player player = event.getPlayer();

        Location location = player.getLocation();
        location.subtract(0,1,0);

        if (!warpStorage.hasTrigger(location)) return;

        WarpData data = warpStorage.getTrigger(location).getTargetWarp();
        WarpUtil.teleport(data, player);
    }
}
