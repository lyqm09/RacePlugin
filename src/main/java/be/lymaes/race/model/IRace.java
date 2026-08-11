package be.lymaes.race.model;

import be.lymaes.race.RaceProfile;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.EquipmentSlotGroup;

public interface IRace extends Listener {

    void loadRank(RaceProfile profile);
    void reloadEffect(RaceProfile profile);
    void cleanup(RaceProfile profile);
    void addExp(RaceProfile profile, int n);

    String getRankName(int rank);
    int getExpRequired(int rank);

    static void removeAttribute(Player player, Attribute attribute, NamespacedKey key) {
        AttributeInstance attributeInstance = player.getAttribute(attribute);
        if(attributeInstance != null) {
            for (AttributeModifier mod : attributeInstance.getModifiers()) {
                if (mod.getKey().equals(key)) {
                    attributeInstance.removeModifier(mod);
                    break;
                }
            }
        }
    }

    static void replaceAttribute(Player player, Attribute attribute, NamespacedKey key, double amount, AttributeModifier.Operation operation) {
        AttributeInstance attributeInstance = player.getAttribute(attribute);
        if(attributeInstance != null) {
            for (AttributeModifier mod : attributeInstance.getModifiers()) {
                if (mod.getKey().equals(key)) {
                    attributeInstance.removeModifier(mod);
                    break;
                }
            }
            attributeInstance.addModifier(new AttributeModifier(key, amount, operation, EquipmentSlotGroup.ANY));
        }
    }
}
