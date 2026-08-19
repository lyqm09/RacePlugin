package be.lymaes.race;

import be.lymaes.race.manager.RaceManager;
import be.lymaes.race.model.Kitsune;
import be.lymaes.race.model.Oni;
import be.lymaes.race.model.RaceType;
import be.lymaes.race.model.Tamashi;
import org.bukkit.scheduler.BukkitRunnable;

public class MainRunnable extends BukkitRunnable {

    private final RaceManager raceManager;

    public MainRunnable(Race plugin) {
        this.raceManager = plugin.getRaceManager();
        this.runTaskTimer(plugin, 0, 20L);
    }

    @Override
    public void run() {

        ((Oni) raceManager.getRaceModel(RaceType.ONI)).task(raceManager);
        ((Kitsune) raceManager.getRaceModel(RaceType.KITSUNE)).task(raceManager);
        ((Tamashi) raceManager.getRaceModel(RaceType.TAMASHI)).task(raceManager);

    }

}
