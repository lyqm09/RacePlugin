package be.lymaes.race.item;

import be.lymaes.race.Race;
import be.lymaes.race.manager.ItemManager;
import be.lymaes.race.model.Tamashi;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.inventory.ItemStack;

public class FlyCharge implements IRaceItem, IStaticItem {

    @Override
    public RaceItem getType() {
        return RaceItem.FLY_CHARGE;
    }

    @EventHandler
    public void onItemSwitch(PlayerItemHeldEvent e) {
        Player player = e.getPlayer();

        if(player.getGameMode() == GameMode.CREATIVE || player.getGameMode() == GameMode.SPECTATOR) {
            return;
        }

        if(!player.hasPermission(Tamashi.PERM_FLY_CHARGE)) {
            return;
        }

        ItemStack oldItem = player.getInventory().getItem(e.getPreviousSlot());
        ItemStack newItem = player.getInventory().getItem(e.getNewSlot());

        ItemManager manager = Race.getInstance().getItemManager();

        if(newItem != null && manager.getItem(newItem) instanceof FlyCharge) {
            player.setAllowFlight(true);
        }
        else if(oldItem != null && manager.getItem(oldItem) instanceof FlyCharge) {
            player.setAllowFlight(false);
        }
    }

}
