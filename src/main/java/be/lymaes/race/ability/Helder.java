package be.lymaes.race.ability;

import be.lymaes.race.data.IRaceData;
import org.bukkit.inventory.ItemStack;

public interface Helder {

    void onSwitchOn(IRaceData raceData, ItemStack item);
    void onSwitchOff(IRaceData raceData, ItemStack item);

}
