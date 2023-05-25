package net.starly.warp.command;

import lombok.Getter;
import net.starly.warp.WarpMain;
import net.starly.warp.data.WarpData;
import net.starly.warp.enums.MessageType;
import net.starly.warp.manager.WarpManager;
import net.starly.warp.util.MessageUtil;
import net.starly.warp.util.WarpUtil;
import org.bukkit.command.Command;
import org.bukkit.command.CommandMap;
import org.bukkit.command.CommandSender;
import org.bukkit.command.defaults.BukkitCommand;
import org.bukkit.entity.Player;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CustomCommand extends BukkitCommand {

    @Getter
    private final WarpData warpData;

    public CustomCommand(String name, WarpData targetWarp) {
        super(name);
        this.setName(name);
        this.setDescription("ST-WarpReloaded Custom Warp Command");
        this.setPermission("warp.teleport");
        this.setLabel(name);

        warpData = targetWarp;

        try {
            Field field;
            field = WarpMain.getInstance().getServer().getPluginManager().getClass().getDeclaredField("commandMap");
            field.setAccessible(true);
            CommandMap commandMap = (CommandMap) field.get(WarpMain.getInstance().getServer().getPluginManager());
            commandMap.register(name,this);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean execute(CommandSender commandSender, String s, String[] strings) {

        if (!WarpManager.getInstance().getCommandList().contains(this)) {
            commandSender.sendMessage("Unknown command. Type \"/help\" for help.");
            return false;
        }

        if (!(commandSender instanceof Player)) {
            commandSender.sendMessage(MessageUtil.getMessage(MessageType.WRONG_PLATFORM));
            return false;
        }

        WarpUtil.teleport(warpData, (Player) commandSender);
        return false;
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String alias, String[] args) throws IllegalArgumentException {
        return null;
    }

    public CommandMap getCommandMap() {
        CommandMap commandMap = null;

        try {
            Field f = WarpMain.getInstance().getServer().getPluginManager().getClass().getDeclaredField("commandMap");
            f.setAccessible(true);

            commandMap = (CommandMap) f.get(WarpMain.getInstance().getServer().getPluginManager());
        } catch (NoSuchFieldException | IllegalAccessException | IllegalArgumentException | SecurityException e) {
            e.printStackTrace();
        }

        return commandMap;
    }

    public Map<String, String> serialize() {
        Map<String, String> result = new HashMap<>();
        result.put("name",getName());
        result.put("warp", warpData.getName());
        return result;
    }
}
