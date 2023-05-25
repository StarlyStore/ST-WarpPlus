package net.starly.warp.listener;

import net.starly.warp.WarpMain;
import net.starly.warp.command.CustomCommand;
import net.starly.warp.manager.WarpManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChatTabCompleteEvent;
import org.bukkit.event.server.TabCompleteEvent;

import java.util.Collection;
import java.util.List;

public class TabCompleteListener implements Listener {
    @EventHandler
    public void onTabComplete(PlayerChatTabCompleteEvent event) {
        Collection<String> completions = event.getTabCompletions();
        completions.clear();
        for (CustomCommand command : WarpManager.getInstance().getCommandList()) {
            completions.add(WarpMain.getInstance().getName());
            completions.add(command.getName());
            System.out.println(command.getName());
        }
        System.out.println("tab");
    }
}
