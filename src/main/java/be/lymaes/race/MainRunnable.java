package be.lymaes.race;

import be.lymaes.race.ability.AbilityType;
import be.lymaes.race.ability.Taskable;
import be.lymaes.race.manager.RaceManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Set;

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
        long currentTime = System.currentTimeMillis();

        for(Player player : Bukkit.getOnlinePlayers()) {
            RaceProfile profile = raceManager.getProfile(player);
            if(profile == null) continue;

            Set<Taskable> abilities = profile.getEventAbilities(AbilityType.TASKABLE);
            for(Taskable taskable : abilities) {
                taskable.run(player, profile, profile.raceData, currentTime);
            }
        }

    }

}
