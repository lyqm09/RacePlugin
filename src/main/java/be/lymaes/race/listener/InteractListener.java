package be.lymaes.race.listener;

import be.lymaes.race.Race;
import be.lymaes.race.RaceProfile;
import be.lymaes.race.ability.Interactable;
import be.lymaes.race.manager.RaceManager;
import be.lymaes.race.model.IRace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;

public class InteractListener implements Listener {

    private final RaceManager raceManager;

    public InteractListener(Race plugin) {
        this.raceManager = plugin.getRaceManager();
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent e) {
        Player player = e.getPlayer();

        RaceProfile profile = raceManager.getProfile(player);
        if(profile == null) return;

        IRace model = raceManager.getRaceModel(profile.raceData.getRace());
        if(!(model instanceof Interactable interactable)) return;

        interactable.onInteract(e, player, profile.raceData);
    }

}
