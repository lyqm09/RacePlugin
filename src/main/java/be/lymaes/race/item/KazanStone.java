package be.lymaes.race.item;

import be.lymaes.race.Race;
import be.lymaes.race.RaceProfile;
import be.lymaes.race.manager.ItemManager;
import be.lymaes.race.model.IRace;
import be.lymaes.race.model.Oni;
import org.bukkit.entity.Player;
import org.bukkit.entity.WitherSkeleton;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;

import java.util.concurrent.ThreadLocalRandom;

public class KazanStone implements IRaceItem, Eatable {

    private static final double TOL = 0.05;

    @Override
    public RaceItem getType() {
        return RaceItem.KAZAN_STONE;
    }

    @EventHandler
    public void onDrop(EntityDeathEvent e) {
        if(!(e.getEntity() instanceof WitherSkeleton))
            return;

        double random = ThreadLocalRandom.current().nextDouble();
        if(random > TOL)
            return;

        ItemStack kazanStone = RaceItem.KAZAN_STONE.getItem();
        e.getDrops().add(kazanStone);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onConsume(PlayerItemConsumeEvent e) {
        Race plugin = Race.getInstance();
        IRaceItem item = plugin.getItemManager().getItem(e.getItem());
        if(!(item instanceof KazanStone)) return;

        if(e.isCancelled()) {
            e.setCancelled(false);
        }

        Player player = e.getPlayer();
        RaceProfile profile = Race.getInstance().getRaceManager().getProfile(player);

        IRace race = plugin.getRaceManager().getRaceModel(profile.race);
        if(!(race instanceof Oni oni)) {
            player.addPotionEffect(PotionEffectType.POISON.createEffect(10 * 20, 1));
            return;
        }

        oni.checkRankup(profile, true);
    }

}
