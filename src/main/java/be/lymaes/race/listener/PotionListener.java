package be.lymaes.race.listener;

import be.lymaes.race.Race;
import be.lymaes.race.data.IRaceData;
import be.lymaes.race.item.IRaceItem;
import be.lymaes.race.item.Splashable;
import be.lymaes.race.manager.ItemManager;
import be.lymaes.race.manager.RaceManager;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PotionSplashEvent;

import java.util.ArrayList;
import java.util.List;

public class PotionListener implements Listener {

    private final RaceManager raceManager;
    private final ItemManager itemManager;

    public PotionListener(Race plugin) {
        this.raceManager = plugin.getRaceManager();
        this.itemManager = plugin.getItemManager();
    }

    @EventHandler
    public void onSplash(PotionSplashEvent e) {
        IRaceItem item = itemManager.getItem(e.getPotion().getItem());
        if(item instanceof Splashable splashable) {
            List<Player> players = new ArrayList<>();
            List<IRaceData> data = new ArrayList<>();
            for(LivingEntity entity : e.getAffectedEntities()) {
                if(!(entity instanceof Player player)) continue;

                players.add(player);
                data.add(raceManager.getProfile(player).raceData);
            }

            splashable.onSplash(players, data);
        }
    }

}
