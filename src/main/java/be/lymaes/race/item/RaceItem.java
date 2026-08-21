package be.lymaes.race.item;

import be.lymaes.race.Race;
import be.lymaes.race.manager.ItemManager;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.components.FoodComponent;
import org.bukkit.inventory.meta.components.consumable.ConsumableComponent;
import org.bukkit.persistence.PersistentDataType;

import java.util.function.Supplier;

public enum RaceItem {

    KAZAN_STONE("kazan_stone", "Pierre de Kazan", Material.CHARCOAL, KazanStone::new),
    TAMASHI_HEART("tamashi_heart", "Coeur de Tamashi", Material.GLOWSTONE_DUST, TamashiHeart::new),
    FLY_CHARGE("fly_charge", "Charge de vol", Material.WIND_CHARGE, FlyCharge::new),
    MILICIEN_EGG("milicien_egg", "Oeuf de Milicien", Material.IRON_GOLEM_SPAWN_EGG, MilicienEgg::new);

    public final String id;
    public final String name;
    public final Material material;
    public final Supplier<IRaceItem> factory;

    RaceItem(String id, String name, Material material, Supplier<IRaceItem> factory) {
        this.id = id;
        this.name = name;
        this.material = material;
        this.factory = factory;
    }

    public ItemStack getItem() {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        if(meta == null)
            return item;

        meta.getPersistentDataContainer().set(ItemManager.KEY_ITEM_ID, PersistentDataType.STRING, id);

        meta.setDisplayName(ChatColor.WHITE + name);

        meta.setEnchantmentGlintOverride(true);

        IRaceItem iitem = Race.getInstance().getItemManager().getItem(id);
        if(iitem instanceof Consumable) {
            ConsumableComponent consumable = meta.getConsumable();
            consumable.setAnimation(ConsumableComponent.Animation.EAT);
            consumable.setConsumeSeconds(1.6f);
            consumable.setSound(Sound.ENTITY_ITEM_BREAK);
            meta.setConsumable(consumable);

            FoodComponent food = meta.getFood();
            food.setNutrition(0);
            food.setSaturation(0);
            food.setCanAlwaysEat(true);
            meta.setFood(food);
        }

        item.setItemMeta(meta);
        return item;
    }

}
