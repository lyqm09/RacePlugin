package be.lymaes.race.gui;

import be.lymaes.race.Race;
import be.lymaes.race.manager.GUIManager;
import be.lymaes.race.model.IRace;
import be.lymaes.race.model.ISubRaceable;
import be.lymaes.race.model.RaceType;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.jspecify.annotations.NonNull;

public class RaceChoiceGUI implements IRaceGUI {

    private final InventoryHolder holder;

    public RaceChoiceGUI() {
        this.holder = new RaceInventoryHolder(1, "Choix de race");

        Inventory inventory = getInventory();

        RaceType[] raceTypes = RaceType.values();
        for(int i = 0; i < raceTypes.length; i++) {

            inventory.setItem(i, raceTypes[i].getItem());

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

        RaceType[] raceTypes = RaceType.values();
        int slot = e.getSlot();

        if(slot >= 0 && slot < raceTypes.length) {
            Race plugin = Race.getInstance();
            IRace race = plugin.getRaceManager().getRaceModel(raceTypes[slot]);

            if(race instanceof ISubRaceable subRaceable) {
                GUIManager manager = Race.getInstance().getGuiManager();
                manager.getGUI(subRaceable.getSubRaceGUI()).open(player);
            }
            else {
                plugin.getRaceManager().changeRace(player, raceTypes[slot]);
                player.closeInventory();
            }
        }

        player.updateInventory();
    }

    @Override
    public @NonNull Inventory getInventory() {
        return holder.getInventory();
    }

}
