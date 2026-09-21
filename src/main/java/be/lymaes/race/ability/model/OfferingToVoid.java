package be.lymaes.race.ability.model;

import be.lymaes.race.Race;
import be.lymaes.race.RaceProfile;
import be.lymaes.race.ability.ItemDropping;
import be.lymaes.race.manager.RaceManager;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Item;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.persistence.PersistentDataType;

import java.util.UUID;

public class OfferingToVoid implements ItemDropping {

    private static final int EXP_MULTIPLIER = 10;
    private final NamespacedKey key;

    public OfferingToVoid() {
        this.key = NamespacedKey.fromString("intentionally_dropped", Race.getInstance());
    }

    @Override
    public void onDrop(PlayerDropItemEvent e) {
        if(e.isCancelled()) return;

        Item item = e.getItemDrop();
        if(item.getItemStack().getType() != Material.DIAMOND) return;

        item.getPersistentDataContainer().set(key, PersistentDataType.BYTE, (byte) 1);
    }

    public void diamondDamage(EntityDamageEvent e, RaceManager raceManager) {
        if(e.getCause() != EntityDamageEvent.DamageCause.VOID) return;

        if(!(e.getEntity() instanceof Item item)) return;

        if(!item.getPersistentDataContainer().has(key)) return;

        item.remove();

        UUID throwerUuid = item.getThrower();
        if (throwerUuid == null) return;

        RaceProfile profile = raceManager.getProfile(throwerUuid);
        if(profile == null) return;

        profile.addExp(EXP_MULTIPLIER * item.getItemStack().getAmount());
    }

}
