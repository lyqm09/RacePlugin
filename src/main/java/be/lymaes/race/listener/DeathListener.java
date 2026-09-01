package be.lymaes.race.listener;

import be.lymaes.race.Race;
import be.lymaes.race.RaceProfile;
import be.lymaes.race.ability.AbilityType;
import be.lymaes.race.ability.Helder;
import be.lymaes.race.ability.Killer;
import be.lymaes.race.item.Droppable;
import be.lymaes.race.item.IRaceItem;
import be.lymaes.race.manager.ItemManager;
import be.lymaes.race.manager.RaceManager;
import be.lymaes.race.model.IRace;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

import java.util.Set;

public class DeathListener implements Listener {

    private final RaceManager raceManager;
    private final ItemManager itemManager;

    public DeathListener(Race plugin) {
        this.raceManager = plugin.getRaceManager();
        this.itemManager = plugin.getItemManager();
    }

    @EventHandler
    public void onKill(EntityDeathEvent e) {
        Entity damager = e.getDamageSource().getCausingEntity();
        if(damager instanceof Player player) {
            RaceProfile profile = raceManager.getProfile(player);
            if (profile == null) return;

            Set<Killer> abilities = profile.getAbilities(AbilityType.KILLER);
            for(Killer killer : abilities) {
                killer.onKill(e, profile);
            }
        }

        if(e.getEntity() instanceof Player player) {
            RaceProfile profile = raceManager.getProfile(player);
            if(profile == null) return;

            Set<Helder> abilities = profile.getAbilities(AbilityType.HELDER);
            for(Helder helder : abilities) {
                helder.onDrops(e);
            }
        }

        for(IRaceItem item : itemManager.getRegisterValues()) {
            if(!(item instanceof Droppable droppable)) continue;
            droppable.onDrop(e);
        }
    }

    @EventHandler
    public void onRespawn(PlayerRespawnEvent e) {
        Player player = e.getPlayer();
        RaceProfile profile = raceManager.getProfile(player);
        if(profile == null) return;

        Bukkit.getScheduler().runTaskLater(Race.getInstance(), () -> {
            raceManager.getRaceModel(profile.raceData.getRace()).reapplyEffect(player, profile.raceData);
        }, 1L);
    }

}
