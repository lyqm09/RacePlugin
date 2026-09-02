package be.lymaes.race.model;

import be.lymaes.race.Race;
import be.lymaes.race.RaceProfile;
import be.lymaes.race.ability.Ability;
import be.lymaes.race.ability.AbilityKey;
import be.lymaes.race.data.IRaceData;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlotGroup;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.permissions.PermissionAttachmentInfo;

import java.util.Map;

public interface IRace<T extends IRaceData> {

    Map<AbilityKey, Ability> getAbilities();

    void applyRacePerks(Player player, RaceProfile profile, T data);
    void reapplyPerms(Player player, T data);
    void reapplyEffect(Player player, T data);
    void cleanup(Player player, RaceProfile profile);

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

    static PermissionAttachment getPermission(Player player) {
        for (PermissionAttachmentInfo info : player.getEffectivePermissions()) {
            PermissionAttachment attachment = info.getAttachment();

            if (attachment != null && attachment.getPlugin().equals(Race.getInstance())) {
                return attachment;
            }
        }
        return null;
    }

    static void addPermission(Player player, String perm) {
        PermissionAttachment attachment = getPermission(player);
        if(attachment == null)
            attachment = player.addAttachment(Race.getInstance());

        attachment.setPermission(perm, true);
        player.updateCommands();
    }

    static void removePermission(Player player, String perm) {
        PermissionAttachment attachment = getPermission(player);
        if(attachment == null)
            return;

        attachment.setPermission(perm, false);
        player.updateCommands();
    }
}
