package be.lymaes.race.ability;

import be.lymaes.race.RaceProfile;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.MerchantInventory;

public interface Merchant extends Ability {

    void onTrade(InventoryClickEvent e, MerchantInventory inventory, RaceProfile profile);

}
