package be.lymaes.race;

import be.lymaes.race.ability.Taskable;
import be.lymaes.race.manager.RaceManager;
import be.lymaes.race.model.*;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class MainRunnable extends BukkitRunnable {

    private final RaceManager raceManager;

    public MainRunnable(Race plugin) {
        this.raceManager = plugin.getRaceManager();
        this.runTaskTimer(plugin, 0, 20L);
    }

    public void terminate() {
        if(!this.isCancelled()) {
            this.cancel();
        }
    }

    @Override
    public void run() {

        for(Player player : Bukkit.getOnlinePlayers()) {
            RaceProfile profile = raceManager.getProfile(player);
            if(profile == null) continue;

            IRace model = raceManager.getRaceModel(profile.raceData.getRace());
            if(!(model instanceof Taskable taskable)) continue;

            taskable.onTask(player, profile);
        }

    }

}
