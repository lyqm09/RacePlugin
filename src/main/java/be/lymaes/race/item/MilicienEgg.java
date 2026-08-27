package be.lymaes.race.item;

import be.lymaes.race.model.Karyu;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.IronGolem;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class MilicienEgg extends ARaceItem implements IStaticItem, Interactable {

    @Override
    public RaceItem getType() {
        return RaceItem.MILICIEN_EGG;
    }

    @Override
    public void onInteract(PlayerInteractEvent e, Player player, ItemStack item) {
        e.setCancelled(true);

        if(e.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }

        Block clickedBlock = e.getClickedBlock();
        if (clickedBlock == null) {
            return;
        }

        if(!player.hasPermission(Karyu.PERM_MILICIEN)) {
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
