package be.lymaes.race.listener;

import be.lymaes.race.Race;
import be.lymaes.race.RaceProfile;
import be.lymaes.race.ability.*;
import be.lymaes.race.ability.model.OfferingToVoid;
import be.lymaes.race.manager.AbilityManager;
import be.lymaes.race.manager.RaceManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;

import java.util.Set;

public class DamageListener implements Listener {

    private final RaceManager raceManager;
    private final AbilityManager abilityManager;

    public DamageListener(Race plugin) {
        this.raceManager = plugin.getRaceManager();
        this.abilityManager = plugin.getAbilityManager();
    }

    @EventHandler
    public void onDamage(EntityDamageEvent e) {
        handleAttack(e);
        handleDefend(e);
    }

    private void handleAttack(EntityDamageEvent e) {

        // special case
        Ability ability = abilityManager.getAbility(AbilityKey.OFFERING_TO_VOID);
        if(ability instanceof OfferingToVoid offering) {
            offering.diamondDamage(e, raceManager);
        }

        // entity damage by entity
        if(!(e instanceof EntityDamageByEntityEvent attackEvent)) return;
        if(!(attackEvent.getDamager() instanceof Player player)) return;

        RaceProfile profile = raceManager.getProfile(player);
        if(profile == null) return;

        Set<Damager> abilities = profile.getEventAbilities(AbilityType.DAMAGER);
        for(Damager damager : abilities) {
            damager.onDamage(attackEvent, player, profile);
        }
    }

    private void handleDefend(EntityDamageEvent e) {
        if(!(e.getEntity() instanceof Player player)) return;

        RaceProfile profile = raceManager.getProfile(player);
        if(profile == null) return;

        Set<Defender> abilities = profile.getEventAbilities(AbilityType.DEFENDER);
        for(Defender defender : abilities) {
            defender.onDefend(e, profile);
        }
    }

}
