package net.starly.warp.manager;

import net.starly.warp.WarpMain;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.HashMap;
import java.util.UUID;

public abstract class InputListenerBase {

    protected static final HashMap<UUID, Listener> listenerMap = new HashMap<>();

    protected abstract void onClick(PlayerInteractEvent event);
    protected abstract void onChat(AsyncPlayerChatEvent event);
    protected abstract void onQuit(PlayerQuitEvent event);

    public void addChatListenTarget(Player player) {
        if (listenerMap.containsKey(player.getUniqueId())) {
            Listener removedListener = listenerMap.remove(player.getUniqueId());
            if (removedListener != null) HandlerList.unregisterAll(removedListener);
        }
        Listener listener = registerChatEvent(player.getUniqueId(), this);
        listenerMap.put(player.getUniqueId(),listener);
        registerPlayerQuitEvent(player.getUniqueId(),this);
    }

    public void addClickListenTarget(Player player) {
        if (listenerMap.containsKey(player.getUniqueId())) {
            Listener removedListener = listenerMap.remove(player.getUniqueId());
            HandlerList.unregisterAll(removedListener);
        }
        Listener listener = registerClickEvent(player.getUniqueId(), this);
        listenerMap.put(player.getUniqueId(),listener);
        registerPlayerQuitEvent(player.getUniqueId(),this);
    }

    protected Listener registerClickEvent(UUID uuid, InputListenerBase listenerManager) {
        Server server = WarpMain.getInstance().getServer();
        Listener clickEventListener = new Listener() {};

        server.getPluginManager().registerEvent(PlayerInteractEvent.class, clickEventListener, EventPriority.LOWEST, (listeners, event) -> {
            if (event instanceof PlayerInteractEvent) {
                PlayerInteractEvent clickEvent = (PlayerInteractEvent) event;
                if (uuid.equals(clickEvent.getPlayer().getUniqueId())) {
                    HandlerList.unregisterAll(clickEventListener);
                    listenerMap.remove(uuid);
                    listenerManager.onClick(clickEvent);
                }
            }
        }, WarpMain.getInstance());

        return clickEventListener;
    }

    protected Listener registerChatEvent(UUID uuid, InputListenerBase listenerManager) {
        Server server = WarpMain.getInstance().getServer();
        Listener chatEventListener = new Listener() {};

        server.getPluginManager().registerEvent(AsyncPlayerChatEvent.class, chatEventListener, EventPriority.LOWEST, (listeners, event) -> {
            if (event instanceof AsyncPlayerChatEvent) {
                AsyncPlayerChatEvent clickEvent = (AsyncPlayerChatEvent) event;
                if (uuid.equals(clickEvent.getPlayer().getUniqueId())) {
                    HandlerList.unregisterAll(chatEventListener);
                    listenerMap.remove(uuid);
                    listenerManager.onChat(clickEvent);
                }
            }
        }, WarpMain.getInstance());

        return chatEventListener;
    }

    protected void registerPlayerQuitEvent(UUID uuid, InputListenerBase listenerManager) {
        Server server = WarpMain.getInstance().getServer();
        Listener listener = new Listener() {};

        server.getPluginManager().registerEvent(PlayerQuitEvent.class, listener, EventPriority.LOWEST, (listeners, event) -> {
            if (event instanceof PlayerQuitEvent) {
                PlayerQuitEvent quitEvent = (PlayerQuitEvent) event;
                if (uuid.equals(quitEvent.getPlayer().getUniqueId())) {
                    Listener removedListener = listenerMap.remove(uuid);
                    if (removedListener != null) {
                        HandlerList.unregisterAll(removedListener);
                    }
                    HandlerList.unregisterAll(listener);
                    listenerManager.onQuit(quitEvent);
                }
            }
        }, WarpMain.getInstance());
    }

    public boolean has(Player player) {
        return listenerMap.containsKey(player.getUniqueId());
    }

    public void removeTarget(Player player) {
        Listener removedListener = listenerMap.remove(player.getUniqueId());
        if (removedListener != null) {
            HandlerList.unregisterAll(removedListener);
        }
    }

}
