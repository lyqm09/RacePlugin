package be.lymaes.race.listener;

import be.lymaes.race.Race;
import be.lymaes.race.RaceProfile;
import be.lymaes.race.ability.Damageable;
import be.lymaes.race.ability.Damager;
import be.lymaes.race.manager.RaceManager;
import be.lymaes.race.model.IRace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;

public class DamageListener implements Listener {

    private final RaceManager raceManager;

    public DamageListener(Race plugin) {
        this.raceManager = plugin.getRaceManager();
    }

    @EventHandler
    public void onDamage(EntityDamageEvent e) {
        handleAttack(e);
        handleDefend(e);
    }

    private void handleAttack(EntityDamageEvent e) {
        if(!(e instanceof EntityDamageByEntityEvent attackEvent)) return;
        if(!(attackEvent.getDamager() instanceof Player player)) return;

        RaceProfile profile = raceManager.getProfile(player);
        if(profile == null) return;

        IRace model = raceManager.getRaceModel(profile.raceData.getRace());
        if(!(model instanceof Damager damager)) return;

        damager.onAttack(attackEvent, player, profile.raceData);
    }

    private void handleDefend(EntityDamageEvent e) {
        if(!(e.getEntity() instanceof Player player)) return;

        RaceProfile profile = raceManager.getProfile(player);
        if(profile == null) return;

        IRace model = raceManager.getRaceModel(profile.raceData.getRace());
        if(!(model instanceof Damageable defender)) return;

        defender.onDefend(e, player, profile.raceData);
    }

}
