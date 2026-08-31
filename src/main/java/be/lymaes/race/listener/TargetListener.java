package be.lymaes.race.listener;

import be.lymaes.race.Race;
import be.lymaes.race.RaceProfile;
import be.lymaes.race.manager.RaceManager;
import be.lymaes.race.model.IRace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityTargetLivingEntityEvent;

public class TargetListener implements Listener {

    private final RaceManager raceManager;

    public TargetListener(Race plugin) {
        this.raceManager = plugin.getRaceManager();
    }

    @EventHandler
    public void onTarget(EntityTargetLivingEntityEvent e) {
        if(!(e.getTarget() instanceof Player player))
            return;

        RaceProfile profile = raceManager.getProfile(player);
        if(profile == null) return;

        IRace model = raceManager.getRaceModel(profile.raceData.getRace());
        if(!(model instanceof Targetable targetable)) return;

        targetable.onTarget(e, player, profile.raceData);
    }

}
