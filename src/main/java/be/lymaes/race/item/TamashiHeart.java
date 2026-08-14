package be.lymaes.race.item;

import be.lymaes.race.Race;
import be.lymaes.race.RaceProfile;
import be.lymaes.race.manager.ItemManager;
import be.lymaes.race.model.IRace;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.entity.WitherSkeleton;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemStack;

import java.util.concurrent.ThreadLocalRandom;

public class TamashiHeart implements IRaceItem, Eatable {

    private static final double TOL = 0.001;
    private static final int BONUS_EXP = 500;

    @Override
    public RaceItem getType() {
        return RaceItem.TAMASHI_HEART;
    }

    @EventHandler
    public void onDrop(EntityDeathEvent e) {
        if(!(e.getEntity() instanceof Monster))
            return;

        double random = ThreadLocalRandom.current().nextDouble();
        if(random > TOL)
            return;

        ItemStack tamashiHeart = RaceItem.TAMASHI_HEART.getItem();
        e.getDrops().add(tamashiHeart);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onConsume(PlayerItemConsumeEvent e) {
        Race plugin = Race.getInstance();
        IRaceItem item = plugin.getItemManager().getItem(e.getItem());
        if(!(item instanceof TamashiHeart)) return;

        if(e.isCancelled()) {
            e.setCancelled(false);
        }

        Player player = e.getPlayer();
        RaceProfile profile = Race.getInstance().getRaceManager().getProfile(player);

        IRace race = plugin.getRaceManager().getRaceModel(profile.raceData.getRace());
        race.addExp(profile, BONUS_EXP);
    }
}
