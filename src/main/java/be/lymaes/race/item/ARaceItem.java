package be.lymaes.race.item;

import be.lymaes.race.manager.ItemManager;
import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

public abstract class ARaceItem implements IRaceItem {

    @Override
    public ItemStack getItem() {
        ItemStack item = new ItemStack(getType().material);
        ItemMeta meta = item.getItemMeta();
        if(meta == null)
            return item;

        meta.getPersistentDataContainer().set(ItemManager.KEY_ITEM_ID, PersistentDataType.STRING, getType().id);
        meta.setDisplayName(ChatColor.WHITE + getType().name);

        applyMeta(meta);

        item.setItemMeta(meta);
        return item;
    }

    protected void applyMeta(ItemMeta meta) {

    }

}
