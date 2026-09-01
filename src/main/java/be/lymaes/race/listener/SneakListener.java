package be.lymaes.race.listener;

import be.lymaes.race.Race;
import be.lymaes.race.RaceProfile;
import be.lymaes.race.ability.AbilityType;
import be.lymaes.race.ability.Sneaker;
import be.lymaes.race.manager.RaceManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerToggleSneakEvent;

import java.util.Set;

public class SneakListener implements Listener {

    private final RaceManager raceManager;

    public SneakListener(Race plugin) {
        this.raceManager = plugin.getRaceManager();
    }

    @EventHandler
    public void onToggleSneak(PlayerToggleSneakEvent e) {
        Player player = e.getPlayer();

        RaceProfile profile = raceManager.getProfile(player);
        if(profile == null) return;

        Set<Sneaker> abilities = profile.getAbilities(AbilityType.SNEAKER);
        for(Sneaker sneaker : abilities) {
            sneaker.onToggleSneak(e);
        }
    }

}
