package be.lymaes.race.listener;

import be.lymaes.race.Race;
import be.lymaes.race.RaceProfile;
import be.lymaes.race.ability.Consumer;
import be.lymaes.race.ability.Interactable;
import be.lymaes.race.data.IRaceData;
import be.lymaes.race.item.Consumable;
import be.lymaes.race.item.IRaceItem;
import be.lymaes.race.manager.ItemManager;
import be.lymaes.race.manager.RaceManager;
import be.lymaes.race.model.IRace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemConsumeEvent;

public class ConsumeListener implements Listener {

    private final RaceManager raceManager;
    private final ItemManager itemManager;

    public ConsumeListener(Race plugin) {
        this.raceManager = plugin.getRaceManager();
        this.itemManager = plugin.getItemManager();
    }

    @EventHandler
    public void onConsume(PlayerItemConsumeEvent e) {
        Player player = e.getPlayer();

        RaceProfile profile = raceManager.getProfile(player);
        if(profile == null) return;

        IRace model = raceManager.getRaceModel(profile.raceData.getRace());
        if(model instanceof Consumer consumer) {
            consumer.onConsume(e, player, profile.raceData);
        }

        IRaceItem item = itemManager.getItem(e.getItem());
        if(item instanceof Consumable consumable) {
            consumable.onConsume(player, profile, model);
        }
    }

}
