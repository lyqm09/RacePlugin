package be.lymaes.race.item.model;

import be.lymaes.race.Race;
import be.lymaes.race.RaceProfile;
import be.lymaes.race.data.IRaceData;
import be.lymaes.race.item.*;
import be.lymaes.race.manager.RaceManager;
import be.lymaes.race.model.IRace;
import be.lymaes.race.model.Karyu;
import org.bukkit.Sound;
import org.bukkit.entity.Enderman;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.components.FoodComponent;
import org.bukkit.inventory.meta.components.UseCooldownComponent;
import org.bukkit.inventory.meta.components.UseEffectsComponent;
import org.bukkit.inventory.meta.components.consumable.ConsumableComponent;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

public class EnderEye extends ARaceItem implements Droppable, Interactable {

    public static final double TOL = 0.0005;

    private final List<UUID> itemConsumedByPlayers = new ArrayList<>();

    @Override
    public RaceItem getType() {
        return RaceItem.ENDER_EYE;
    }

    @Override
    protected void applyMeta(ItemMeta meta) {
        meta.setEnchantmentGlintOverride(true);
    }

    @Override
    public void onDrop(EntityDeathEvent e) {
        if(!(e.getEntity() instanceof Enderman)) return;

        double random = ThreadLocalRandom.current().nextDouble();
        if(random > TOL) return;

        ItemStack enderEye = this.getItem();
        e.getDrops().add(enderEye);
    }

    @Override
    public void onInteract(PlayerInteractEvent e, Player player, ItemStack item) {
        e.setCancelled(true);

        player.setCooldown(item, 0);
        e.setUseItemInHand(Event.Result.DENY);
        e.setUseInteractedBlock(Event.Result.DENY);

        Race plugin = Race.getInstance();
        RaceManager manager = plugin.getRaceManager();
        RaceProfile profile = manager.getProfile(e.getPlayer());
        if(profile == null) {
            player.updateInventory();
            return;
        }

        IRace model = manager.getRaceModel(profile.getRaceData().getRace());
        if(model instanceof Karyu karyu) {
            IRaceData raceData = profile.getRaceData();

            int nextRank = raceData.getRank() + 1;
            int needToRankUp = 0;
            if(nextRank == Karyu.Rank.BIG.rank) { // 1
                needToRankUp = 1;
            } else if(nextRank > Karyu.Rank.BIG.rank) { // 2
                needToRankUp = 2;
            }

            if(needToRankUp < 1 || item.getAmount() < needToRankUp) {
                player.updateInventory();
                return;
            }

            int expRequired = karyu.getExpRequired(nextRank);
            if(expRequired < 0) {
                player.updateInventory();
                return;
            }
            if (raceData.getExp() < expRequired) {
                player.updateInventory();
                return;
            }

            int amount = item.getAmount();
            if (amount > needToRankUp) {
                item.setAmount(amount - needToRankUp);
            } else {
                player.getInventory().remove(item);
            }

            raceData.subExp(expRequired);
            profile.rankUp();

            profile.updateTabInfo();
        }

        player.updateInventory();
    }
}
