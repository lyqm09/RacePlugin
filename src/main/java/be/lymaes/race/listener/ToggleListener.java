package be.lymaes.race.listener;

import be.lymaes.race.Race;
import be.lymaes.race.RaceProfile;
import be.lymaes.race.ability.AbilityType;
import be.lymaes.race.ability.Fly;
import be.lymaes.race.ability.Sneaker;
import be.lymaes.race.manager.RaceManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerToggleFlightEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;

import java.util.Set;

public class ToggleListener implements Listener {

    private final RaceManager raceManager;

    public ToggleListener(Race plugin) {
        this.raceManager = plugin.getRaceManager();
    }

    @EventHandler
    public void onToggleSneak(PlayerToggleSneakEvent e) {
        Player player = e.getPlayer();

        RaceProfile profile = raceManager.getProfile(player);
        if(profile == null) return;

        Set<Sneaker> abilities = profile.getEventAbilities(AbilityType.SNEAKER);
        for(Sneaker sneaker : abilities) {
            sneaker.onToggleSneak(e);
        }
    }

    @EventHandler
    public void onToggleFly(PlayerToggleFlightEvent e) {
        Player player = e.getPlayer();

        RaceProfile profile = raceManager.getProfile(player);
        if(profile == null) return;

        Set<Fly> abilities = profile.getEventAbilities(AbilityType.FLY);
        for(Fly fly : abilities) {
            fly.onToggleFly(e);
        }
    }

}
