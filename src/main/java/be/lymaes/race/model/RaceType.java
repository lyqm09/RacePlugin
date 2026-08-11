package be.lymaes.race.model;

import net.kyori.adventure.text.format.NamedTextColor;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public enum RaceType {

    HUMAN("Humain", NamedTextColor.GOLD, Material.WOODEN_HOE),
    ONI("Oni", NamedTextColor.RED, Material.ROTTEN_FLESH),
    KITSUNE("Kitsune", NamedTextColor.LIGHT_PURPLE, Material.OAK_SAPLING),
    TAMASHI("Tamashi", NamedTextColor.AQUA, Material.BONE_MEAL),
    KARYU("Karyu", NamedTextColor.GREEN, Material.DRAGON_EGG);

    public final String name;
    public final NamedTextColor color;
    public final Material icon;

    RaceType(String name, NamedTextColor color, Material icon) {
        this.name = name;
        this.color = color;
        this.icon = icon;
    }

    public String getName() {
        return name;
    }

    public NamedTextColor getColor() {
        return color;
    }

    public ItemStack getItem() {
        ItemStack item = new ItemStack(icon);
        ItemMeta meta = item.getItemMeta();

        if(meta == null)
            return item;

        meta.setDisplayName(ChatColor.of(color.asHexString()) + name);

        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);

        item.setItemMeta(meta);
        return item;
    }

    public static RaceType fromName(String name) {

        for(RaceType race : RaceType.values()) {
            if(race.name().equalsIgnoreCase(name)) {
                return race;
            }
        }

        return HUMAN;
    }

}
