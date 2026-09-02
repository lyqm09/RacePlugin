package be.lymaes.race.listener;

import be.lymaes.race.Race;
import be.lymaes.race.RaceProfile;
import be.lymaes.race.ability.AbilityType;
import be.lymaes.race.ability.Damager;
import be.lymaes.race.ability.Defender;
import be.lymaes.race.manager.RaceManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;

import java.util.Set;

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

        Set<Damager> abilities = profile.getEventAbilities(AbilityType.DAMAGER);
        for(Damager damager : abilities) {
            damager.onDamage(attackEvent, player, profile.raceData.getRank());
        }
    }

    private void handleDefend(EntityDamageEvent e) {
        if(!(e.getEntity() instanceof Player player)) return;

        RaceProfile profile = raceManager.getProfile(player);
        if(profile == null) return;

        Set<Defender> abilities = profile.getEventAbilities(AbilityType.DEFENDER);
        for(Defender defender : abilities) {
            defender.onDefend(e, profile.raceData.getRank());
        }
    }

}
