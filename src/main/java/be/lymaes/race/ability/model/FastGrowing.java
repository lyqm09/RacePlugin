package be.lymaes.race.ability.model;

import be.lymaes.race.ability.Interact;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.Ageable;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

public class FastGrowing implements Interact {

    @Override
    public void onInteract(PlayerInteractEvent e, Player player, int rank) {
        if(!player.isSneaking()) return;
        if(e.getItem() != null || e.getMaterial() != Material.AIR) return;
        if(e.getAction() != Action.LEFT_CLICK_BLOCK) return;

        Block block = e.getClickedBlock();
        if(block == null) return;
        if(!(block.getBlockData() instanceof Ageable ageable)) return;

        ageable.setAge(ageable.getMaximumAge());
        block.setBlockData(ageable);
    }

}
