package be.lymaes.race.gui;

import be.lymaes.race.Race;
import be.lymaes.race.model.IRace;
import be.lymaes.race.model.Karyu;
import be.lymaes.race.model.RaceType;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.jspecify.annotations.NonNull;

public class KaryuChoiceGUI implements IRaceGUI {

    private final InventoryHolder holder;

    public KaryuChoiceGUI() {
        this.holder = new RaceInventoryHolder(1, "Choix du type");

        Inventory inventory = getInventory();

        Karyu.SubRace[] subRace = Karyu.SubRace.values();
        for(int i = 0; i < subRace.length; i++) {

            inventory.setItem(i, subRace[i].getItem());

        }
    }

    @Override
    public void open(Player player) {
        player.openInventory(getInventory());
    }

    @Override
    public void onClick(InventoryClickEvent e) {
        e.setCancelled(true);

        if(!(e.getWhoClicked() instanceof Player player)) {
            return;
        }

        Karyu.SubRace[] subRace = Karyu.SubRace.values();
        int slot = e.getSlot();

        if(slot >= 0 && slot < subRace.length) {
            Race plugin = Race.getInstance();
            plugin.getRaceManager().changeRace(player, RaceType.KARYU, slot);
            player.closeInventory();
        }

        player.updateInventory();
    }

    @Override
    public @NonNull Inventory getInventory() {
        return holder.getInventory();
    }
}
