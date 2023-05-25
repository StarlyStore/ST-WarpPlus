package net.starly.warp;

import net.starly.core.bstats.Metrics;
import net.starly.warp.command.WarpCommand;
import net.starly.warp.command.tabCompleter.WarpTabCompleter;
import net.starly.warp.data.WarpData;
import net.starly.warp.listener.TabCompleteListener;
import net.starly.warp.manager.WarpManager;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

public class WarpMain extends JavaPlugin {

    private static WarpMain instance;
    public static WarpMain getInstance() {
        return instance;
    }

    @Override
    public void onLoad() {
        instance = this;
    }

    @Override
    public void onEnable() {
        /* DEPENDENCY
         ──────────────────────────────────────────────────────────────────────────────────────────────────────────────── */
        if (!isPluginEnabled("ST-Core")) {
            getServer().getLogger().warning("[" + getName() + "] ST-Core 플러그인이 적용되지 않았습니다! 플러그인을 비활성화합니다.");
            getServer().getLogger().warning("[" + getName() + "] 다운로드 링크 : §fhttp://starly.kr/");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        /* SETUP
         ──────────────────────────────────────────────────────────────────────────────────────────────────────────────── */
        //new Metrics(this, 12345); // TODO: 수정

        /* CONFIG
         ──────────────────────────────────────────────────────────────────────────────────────────────────────────────── */
        saveDefaultConfig();
        reloadConfig();

        /* DATA
        ──────────────────────────────────────────────────────────────────────────────────────────────────────────────── */
        WarpManager.getInstance().loadData();

        /* COMMAND
         ──────────────────────────────────────────────────────────────────────────────────────────────────────────────── */
        getCommand("워프").setExecutor(new WarpCommand());
        getCommand("워프").setTabCompleter(new WarpTabCompleter());

        /* LISTENER
         ──────────────────────────────────────────────────────────────────────────────────────────────────────────────── */
        getServer().getPluginManager().registerEvents(new TabCompleteListener(), this);
    }

    private boolean isPluginEnabled(String name) {
        Plugin plugin = getServer().getPluginManager().getPlugin(name);
        return plugin != null && plugin.isEnabled();
    }

    @Override
    public void onDisable() {
        WarpManager.getInstance().saveData(); // 데이터 저장
    }
}
