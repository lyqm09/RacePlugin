package be.lymaes.race.ability.model;

import be.lymaes.race.Race;
import be.lymaes.race.RaceProfile;
import be.lymaes.race.ability.Interact;
import be.lymaes.race.ability.InteractOwnInventory;
import com.google.common.collect.Multimap;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.*;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.components.EquippableComponent;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class PowderWalker implements InteractOwnInventory, Interact {

    private static final int BOOTS_SLOT = 36;

    private final NamespacedKey powderKey;
    private final NamespacedKey invisibleModel;
    private final NamespacedKey zeroArmorKey;

    public PowderWalker() {
        Race plugin = Race.getInstance();

        this.powderKey = new NamespacedKey(plugin, "powder_boots");
        this.invisibleModel = new NamespacedKey(plugin, "invisible");
        this.zeroArmorKey = new NamespacedKey(plugin, "zero_armor");
    }

    @Override
    public void onClick(InventoryClickEvent e, PlayerInventory inventory, Player player) {

        if(e.getSlot() == BOOTS_SLOT) {

            switch (e.getAction()) {
                case PICKUP_ALL,
                     PICKUP_SOME,
                     PICKUP_HALF,
                     PICKUP_ONE,
                     SWAP_WITH_CURSOR,
                     HOTBAR_SWAP,
                     HOTBAR_MOVE_AND_READD,
                     MOVE_TO_OTHER_INVENTORY,
                     DROP_ALL_CURSOR,
                     DROP_ONE_SLOT,
                     DROP_ONE_CURSOR-> {
                    e.setCurrentItem(realOf(e.getCurrentItem()));
                    scheduleBootsUpdate(inventory);
                }
                case PLACE_ALL,
                     PLACE_SOME,
                     PLACE_ONE -> scheduleBootsUpdate(inventory);
                default -> {}
            }

        } else if(e.getAction() == InventoryAction.MOVE_TO_OTHER_INVENTORY) {

            if(!isBoots(e.getCurrentItem())) return;

            scheduleBootsUpdate(inventory);
        }
    }

    @Override
    public void onInteract(PlayerInteractEvent e, Player player, RaceProfile profile) {
        if(e.useItemInHand() == Event.Result.DENY) return;

        if(!isBoots(e.getItem())) return;
        if(e.getAction() != Action.RIGHT_CLICK_AIR) return;

        e.setUseItemInHand(Event.Result.DENY);

        PlayerInventory inventory = player.getInventory();

        EquipmentSlot hand = e.getHand();
        if(hand == EquipmentSlot.HAND) {
            player.getInventory().setItemInMainHand(realOf(inventory.getBoots()));
        } else if(hand == EquipmentSlot.OFF_HAND) {
            player.getInventory().setItemInOffHand(realOf(inventory.getBoots()));
        }

        inventory.setBoots(buildPowderBoots(e.getItem()));
    }

    private void scheduleBootsUpdate(PlayerInventory inventory) {
        Bukkit.getScheduler().runTaskLater(Race.getInstance(), () -> {
            inventory.setBoots(buildPowderBoots(inventory.getBoots()));
        }, 1L);
    }

    public void disguise(PlayerInventory inventory) {
        scheduleBootsUpdate(inventory);
    }

    public void restore(PlayerInventory inventory) {
        ItemStack boots = inventory.getBoots();
        if(!isPowderBoots(boots)) return;

        Bukkit.getScheduler().runTaskLater(Race.getInstance(), () -> inventory.setBoots(realOf(boots)), 1L);
    }

    public boolean isBoots(ItemStack item) {
        return item != null && item.getType().name().endsWith("_BOOTS");
    }

    private boolean isPowderBoots(ItemStack boots) {
        if(boots == null || boots.getType() != Material.LEATHER_BOOTS) return false;

        if(!boots.hasItemMeta()) return false;
        ItemMeta meta = boots.getItemMeta();

        return meta.getPersistentDataContainer().has(powderKey, PersistentDataType.BYTE_ARRAY);
    }

    private NamespacedKey vanillaArmorModel(Material m) {
        String n = m.name();
        if (!n.endsWith("_BOOTS")) return null;
        String mat = n.substring(0, n.length() - "_BOOTS".length()).toLowerCase();
        if (mat.equals("golden")) mat = "gold";
        return NamespacedKey.minecraft(mat);
    }

    private static byte[] encode(ItemStack item) {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try (BukkitObjectOutputStream out = new BukkitObjectOutputStream(new GZIPOutputStream(bos))) {
            out.writeObject(item);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
        return bos.toByteArray();
    }

    private static ItemStack decode(byte[] data) throws IOException, ClassNotFoundException {
        try (BukkitObjectInputStream in = new BukkitObjectInputStream(
                new GZIPInputStream(new ByteArrayInputStream(data)))) {
            return (ItemStack) in.readObject();
        }
    }

    private ItemStack buildPowderBoots(ItemStack real) {
        if(isPowderBoots(real)) return real;

        boolean empty = real == null || real.getType().isAir();

        ItemStack boots;
        if(empty) {
            boots = new ItemStack(Material.LEATHER_BOOTS);
        } else {
            boots = new ItemStack(real);
            boots.setType(Material.LEATHER_BOOTS);
        }

        ItemMeta meta = boots.getItemMeta();

        boolean hadEquippable = meta.hasEquippable();
        EquippableComponent eq = meta.getEquippable();
        eq.setSlot(EquipmentSlot.FEET);
        if (empty) {
            eq.setModel(invisibleModel);
        } else if (!hadEquippable) {
            NamespacedKey model = vanillaArmorModel(real.getType());
            eq.setModel(model != null ? model : invisibleModel);
        }
        meta.setEquippable(eq);
        if (!empty && !meta.hasItemModel()) {
            meta.setItemModel(real.getType().getKey());
        }

        if (!meta.hasAttributeModifiers()) {
            Multimap<Attribute, AttributeModifier> defaults = empty ? null : real.getType().getDefaultAttributeModifiers(EquipmentSlot.FEET);
            if (defaults != null && !defaults.isEmpty()) {
                meta.setAttributeModifiers(defaults);
            } else {
                meta.addAttributeModifier(Attribute.ARMOR, new AttributeModifier(zeroArmorKey, 0.0, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlotGroup.FEET));
            }
        }

        boolean breakable = false;
        if (!empty && meta instanceof Damageable d && !meta.isUnbreakable()) {
            int max = d.hasMaxDamage() ? d.getMaxDamage() : real.getType().getMaxDurability();
            if (max > 0) {
                d.setMaxDamage(max);
                breakable = true;
            }
        }
        if (!breakable && !meta.isUnbreakable()) {
            meta.setUnbreakable(true);
            meta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
        }

        meta.getPersistentDataContainer().set(powderKey, PersistentDataType.BYTE_ARRAY, empty ? new byte[0] : encode(real));

        boots.setItemMeta(meta);
        return boots;
    }

    private ItemStack realOf(ItemStack powder) {
        if(!isPowderBoots(powder)) return powder;

        ItemMeta powderMeta = powder.getItemMeta();
        byte[] data = powderMeta.getPersistentDataContainer().get(powderKey, PersistentDataType.BYTE_ARRAY);
        if (data == null || data.length == 0) return null;
        try {
            ItemStack real = decode(data);
            if (powderMeta instanceof Damageable g && real.getItemMeta() instanceof Damageable r) {
                r.setDamage(g.getDamage());
                real.setItemMeta(r);
            }
            return real;
        } catch (Exception ex) {
            System.out.println("...");
            Race.getInstance().getLogger().severe("PowderSnowWalker: impossible de relire les bottes d'origine : " + ex);
            return powder;
        }
    }

}