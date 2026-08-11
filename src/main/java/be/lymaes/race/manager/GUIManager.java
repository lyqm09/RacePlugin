package be.lymaes.race.manager;

import be.lymaes.race.RaceProfile;
import be.lymaes.race.gui.*;
import org.bukkit.inventory.Inventory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GUIManager {

    private Map<GUITypes, IRaceGUI> register = new HashMap<>();
    private Map<Inventory, IRaceGUI> inventories = new HashMap<>();

    public GUIManager() {
        RaceChoiceGUI raceChoice = new RaceChoiceGUI();
        register.put(GUITypes.RACE, raceChoice);
        inventories.put(raceChoice.getInventory(), raceChoice);

        TamashiChoiceGUI tamashiChoice = new TamashiChoiceGUI();
        register.put(GUITypes.TAMASHI, tamashiChoice);
        inventories.put(tamashiChoice.getInventory(), tamashiChoice);

        KaryuChoiceGUI karyuChoice = new KaryuChoiceGUI();
        register.put(GUITypes.KARYU, karyuChoice);
        inventories.put(karyuChoice.getInventory(), karyuChoice);
    }

    public void terminate() {
        register.clear();
        inventories.clear();
    }

    public IRaceGUI getGUI(GUITypes gui) {
        return register.get(gui);
    }

    public IRaceGUI getGUI(Inventory inventory) {
        return inventories.get(inventory);
    }
}
