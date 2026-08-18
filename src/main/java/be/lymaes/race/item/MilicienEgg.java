package be.lymaes.race.item;

import be.lymaes.race.Race;
import be.lymaes.race.model.Karyu;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.IronGolem;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class MilicienEgg implements IRaceItem, IStaticItem {

    @Override
    public RaceItem getType() {
        return RaceItem.MILICIEN_EGG;
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent e) {
        Race plugin = Race.getInstance();
        ItemStack item = e.getItem();
        if(!(plugin.getItemManager().getItem(item) instanceof MilicienEgg)) {
            return;
        }

        e.setCancelled(true);

        if(e.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }

        Block clickedBlock = e.getClickedBlock();
        if (clickedBlock == null) {
            return;
        }

        Player player = e.getPlayer();
        if(!player.hasPermission(Karyu.PERM_MILICIEN)) {
            return;
        }

        if(player.hasCooldown(item)) {
            return;
        }

        Location spawnLoc = clickedBlock.getRelative(e.getBlockFace())
                .getLocation()
                .add(0.5, 0, 0.5);

        player.getWorld().spawn(spawnLoc, IronGolem.class, golem -> {
            golem.setCustomName("Milicien");
            golem.setCustomNameVisible(true);
        });

        player.setCooldown(item, 60 * 20);
    }

}
