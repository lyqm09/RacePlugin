package be.lymaes.race.ability.model;

import be.lymaes.race.ability.Ability;
import be.lymaes.race.ability.Interact;
import me.libraryaddict.disguise.DisguiseAPI;
import me.libraryaddict.disguise.disguisetypes.DisguiseType;
import me.libraryaddict.disguise.disguisetypes.MobDisguise;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

public class Disguise implements Interact {

    private final DisguiseType disguiseType;

    public Disguise(DisguiseType disguiseType) {
        this.disguiseType = disguiseType;
    }

    public void onInteract(PlayerInteractEvent e, Player player, int rank) {
        if(!player.isSneaking()) return;
        if(e.getItem() != null || e.getMaterial() != Material.AIR) return;
        if(e.getAction() != Action.LEFT_CLICK_AIR && e.getAction() != Action.LEFT_CLICK_BLOCK) return;

        if (DisguiseAPI.isDisguised(player) && DisguiseAPI.getDisguise(player).getType() == disguiseType) {
            DisguiseAPI.undisguiseToAll(player);
        }
        else {
            MobDisguise disguise = new MobDisguise(disguiseType);
            DisguiseAPI.disguiseToAll(player, disguise);
        }

        e.setCancelled(true);
    }

}
