package be.lymaes.race.listener;

import be.lymaes.race.Race;
import be.lymaes.race.RaceProfile;
import be.lymaes.race.ability.Killer;
import be.lymaes.race.manager.RaceManager;
import be.lymaes.race.model.IRace;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

public class DeathListener implements Listener {

    private final RaceManager raceManager;

    public DeathListener(Race plugin) {
        this.raceManager = plugin.getRaceManager();
    }

    @EventHandler
    public void onKill(EntityDeathEvent e) {
        Entity damager = e.getDamageSource().getCausingEntity();
        if(!(damager instanceof Player player)) return;

        RaceProfile profile = raceManager.getProfile(player);
        if(profile == null) return;

        IRace model = raceManager.getRaceModel(profile.raceData.getRace());
        if(!(model instanceof Killer killer)) return;

        killer.onKill(e, profile);
    }

    @EventHandler
    public void onRespawn(PlayerRespawnEvent e) {
        RaceProfile profile = raceManager.getProfile(e.getPlayer());
        if(profile == null) return;

        Bukkit.getScheduler().runTaskLater(Race.getInstance(), () -> {
            raceManager.getRaceModel(profile.raceData.getRace()).reapplyEffect(profile);
        }, 1L);
    }

}
