package be.lymaes.race.manager;

import be.lymaes.race.RaceProfile;
import be.lymaes.race.item.*;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ItemManager {

    public static final NamespacedKey KEY_ITEM_ID = NamespacedKey.fromString("race:item_id");

    private Map<String, IRaceItem> register = new HashMap<>();

    public ItemManager() {
        for(RaceItem item : RaceItem.values()) {
            IRaceItem model = item.factory.get();

            if(model instanceof Craftable craftable) {
                craftable.craft(this);
            }

            register.put(item.id, model);
        }
    }

    public void terminate() {
        register.clear();
    }

    public static boolean isRaceItem(ItemStack item) {
        if(item == null)
            return false;

        ItemMeta meta = item.getItemMeta();
        if(meta == null)
            return false;

        PersistentDataContainer dataContainer = meta.getPersistentDataContainer();
        if(!dataContainer.has(ItemManager.KEY_ITEM_ID))
            return false;

        return true;
    }

    public IRaceItem getItem(ItemStack item) {
        if(!isRaceItem(item))
            return null;

        ItemMeta meta = item.getItemMeta();

        PersistentDataContainer dataContainer = meta.getPersistentDataContainer();
        return register.get(dataContainer.get(ItemManager.KEY_ITEM_ID, PersistentDataType.STRING));
    }

    public IRaceItem getItem(String id) {
        return register.get(id);
    }

    public Collection<IRaceItem> getRegisterValues() {
        return register.values();
    }
}
