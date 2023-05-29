package net.starly.warp.scheduler;

import net.starly.warp.data.WarpTrigger;
import net.starly.warp.manager.WarpStorage;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.scheduler.BukkitRunnable;

public class ParticleScheduler extends BukkitRunnable {

    private static ParticleScheduler instance;

    public static ParticleScheduler getInstance() {
        if (instance == null) instance = new ParticleScheduler();
        return instance;
    }

    public void stop() {
        if (instance != null) instance.cancel();
        instance = null;
    }

    @Override
    public void run() {
        for (WarpTrigger warpTrigger : WarpStorage.getInstance().getTriggerList()) {
            Location location = warpTrigger.getLocation().clone();
            location = location.add(0.5,1.5,0.5);

            World world = location.getWorld();

            world.spawnParticle(Particle.END_ROD,location,1,0,0,0,0);
        }
    }
}
