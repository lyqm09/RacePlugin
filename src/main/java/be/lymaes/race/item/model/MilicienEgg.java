package be.lymaes.race.item.model;

import be.lymaes.race.Race;
import be.lymaes.race.RaceProfile;
import be.lymaes.race.ability.AbilityKey;
import be.lymaes.race.item.ARaceItem;
import be.lymaes.race.item.IStaticItem;
import be.lymaes.race.item.Interactable;
import be.lymaes.race.item.RaceItem;
import be.lymaes.race.manager.RaceManager;
import be.lymaes.race.model.Karyu;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.IronGolem;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class MilicienEgg extends ARaceItem implements IStaticItem, Interactable {

    @Override
    public RaceItem getType() {
        return RaceItem.MILICIEN_EGG;
    }

    @Override
    public void onInteract(PlayerInteractEvent e, Player player, ItemStack item) {
        e.setCancelled(true);

        if(e.getAction() != Action.RIGHT_CLICK_BLOCK) return;

        Block clickedBlock = e.getClickedBlock();
        if (clickedBlock == null) return;

        RaceManager raceManager = Race.getInstance().getRaceManager();
        RaceProfile profile = raceManager.getProfile(player);
        if(profile == null) return;
        if(!profile.hasAbility(AbilityKey.SUMMON_MILICIEN)) return;

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
