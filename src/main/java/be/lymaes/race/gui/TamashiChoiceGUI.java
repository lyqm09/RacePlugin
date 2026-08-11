package be.lymaes.race.gui;

import be.lymaes.race.Race;
import be.lymaes.race.model.RaceType;
import be.lymaes.race.model.Tamashi;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.jspecify.annotations.NonNull;

public class TamashiChoiceGUI implements IRaceGUI {

    private final InventoryHolder holder;

    public TamashiChoiceGUI() {
        this.holder = new RaceInventoryHolder(1, "Choix de l'élément");

        Inventory inventory = getInventory();

        Tamashi.SubRace[] subrace = Tamashi.SubRace.values();
        for(int i = 0; i < subrace.length; i++) {

            inventory.setItem(i, subrace[i].getItem());

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

        Tamashi.SubRace[] subRace = Tamashi.SubRace.values();
        int slot = e.getSlot();

        if(slot >= 0 && slot < subRace.length) {
            Race plugin = Race.getInstance();
            plugin.getRaceManager().changeRace(player, RaceType.TAMASHI, slot);
            player.closeInventory();
        }

        player.updateInventory();
    }

    @Override
    public @NonNull Inventory getInventory() {
        return holder.getInventory();
    }
}
