package be.lymaes.race.model;

import be.lymaes.race.data.*;
import com.fasterxml.jackson.databind.JsonNode;
import net.kyori.adventure.text.format.NamedTextColor;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.function.BiFunction;

public enum RaceType {

    HUMAN("Humain", NamedTextColor.GOLD, Material.WOODEN_HOE, HumanData::loadProfileData),
    ONI("Oni", NamedTextColor.RED, Material.ROTTEN_FLESH, OniData::loadProfileData),
    KITSUNE("Kitsune", NamedTextColor.LIGHT_PURPLE, Material.OAK_SAPLING, KitsuneData::loadProfileData),
    TAMASHI("Tamashi", NamedTextColor.AQUA, Material.BONE_MEAL, TamashiData::loadProfileData),
    KARYU("Karyu", NamedTextColor.GREEN, Material.DRAGON_EGG, KaryuData::loadProfileData);

    public record PrimaryData(int subrace, int rank, int exp) {}

    public final String name;
    public final NamedTextColor color;
    public final Material icon;
    public final BiFunction<JsonNode, PrimaryData, IRaceData> loadData;

    RaceType(String name, NamedTextColor color, Material icon, BiFunction<JsonNode, PrimaryData, IRaceData> loadData) {
        this.name = name;
        this.color = color;
        this.icon = icon;
        this.loadData = loadData;
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
