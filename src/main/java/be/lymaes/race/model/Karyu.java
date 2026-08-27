package be.lymaes.race.model;

import be.lymaes.race.Race;
import be.lymaes.race.RaceProfile;
import be.lymaes.race.ability.Damageable;
import be.lymaes.race.ability.Interactable;
import be.lymaes.race.ability.Killer;
import be.lymaes.race.ability.Merchant;
import be.lymaes.race.data.IRaceData;
import be.lymaes.race.gui.GUITypes;
import be.lymaes.race.item.IStaticItem;
import be.lymaes.race.item.model.MilicienEgg;
import be.lymaes.race.item.RaceItem;
import be.lymaes.race.manager.ItemManager;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.entity.*;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.MerchantInventory;
import org.bukkit.inventory.MerchantRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;

public class Karyu implements IRace, ISubRaceable, IRankable, Damageable, Interactable, Killer, Merchant {

    private static final NamespacedKey STRENGTH = NamespacedKey.fromString("karyu:strength");
    private static final NamespacedKey SPEED = NamespacedKey.fromString("karyu:speed");

    public static final String PERM_FORTUNE = "race.karyu.merchant.fortune";
    public static final String PERM_VILLAGER = "race.karyu.merchant.villager";
    public static final String PERM_MILICIEN = "race.karyu.merchant.milicien";
    public static final String PERM_SHARPNESS = "race.karyu.adorer.sharpness";
    public static final String PERM_BLESS = "race.karyu.adorer.bless";

    private static final List<Material> minerals = List.of(
            Material.AMETHYST_SHARD,
            Material.RESIN_BRICK,

            Material.COPPER_INGOT,
            Material.DIAMOND,
            Material.GOLD_INGOT,
            Material.IRON_INGOT,
            Material.LAPIS_LAZULI,
            Material.CHARCOAL,

            Material.GLOWSTONE,
            Material.NETHERITE_INGOT,
            Material.QUARTZ,
            Material.PRISMARINE_CRYSTALS,
            Material.PRISMARINE_SHARD,
            Material.REDSTONE

    );

    @Override
    public void onTrade(InventoryClickEvent e, MerchantInventory inventory, RaceProfile profile) {
        if(profile.raceData.getSubrace() == SubRace.MERCHANT.id) {
            if(e.getSlotType() != InventoryType.SlotType.RESULT)
                return;

            ItemStack item = e.getCurrentItem();
            if(item == null || item.getType() == Material.AIR)
                return;

            MerchantRecipe recipe = inventory.getSelectedRecipe();
            if(recipe == null)
                return;

            int tradeCount = e.isShiftClick() ? calculateRealTradeCount(inventory, recipe) : 1;

            profile.addExp(10 * tradeCount);
        }
    }

    private int calculateRealTradeCount(MerchantInventory inv, MerchantRecipe recipe) {
        ItemStack firstSlot = inv.getItem(0);
        ItemStack firstIngredient = recipe.getAdjustedIngredient1();
        int firstMaxTrades = (firstSlot != null && firstIngredient != null) ? firstSlot.getAmount() / firstIngredient.getAmount() : 0;

        int secondMaxTrades = Integer.MAX_VALUE;
        if (recipe.getIngredients().size() > 1) {
            ItemStack secondSlot = inv.getItem(1);
            ItemStack secondIngredient = recipe.getIngredients().get(1);
            secondMaxTrades = (secondSlot != null) ? secondSlot.getAmount() / secondIngredient.getAmount() : 0;
        }

        int remainingUses = recipe.getMaxUses() - recipe.getUses();

        return Math.min(Math.min(firstMaxTrades, secondMaxTrades), remainingUses);
    }

    @Override
    public void onKill(EntityDeathEvent e, RaceProfile profile) {
        if(!(e.getEntity() instanceof Monster))
            return;

        if(profile.raceData.getSubrace() == SubRace.ADORER.id) {
            profile.addExp(1);
        }
    }

    @Override
    public void onInteract(PlayerInteractEvent e, Player player, IRaceData raceData) {
        ItemStack item = e.getItem();

        if (raceData.getSubrace() == SubRace.MERCHANT.id
        || (raceData.getSubrace() == SubRace.ADORER.id && raceData.getRank() >= Rank.DRAGON.rank)) {

            if(raceData.getRank() >= Rank.ADVANCE.rank) {
                emeraldExchange(e, player, item);
            }

        }
    }

    public void emeraldExchange(PlayerInteractEvent e, Player player, ItemStack item) {
        if (item == null || item.getType() != Material.EMERALD) return;

        if (e.getAction() != Action.RIGHT_CLICK_AIR) return;

        Material mineral = minerals.get(ThreadLocalRandom.current().nextInt(minerals.size()));

        int amount = item.getAmount();
        if (amount > 1) {
            item.setAmount(amount - 1);
        } else {
            player.getInventory().setItemInMainHand(null);
        }

        if(!player.getInventory().addItem(new ItemStack(mineral)).isEmpty()) {
            item.setAmount(amount);
            player.sendMessage(Color.RED + "Erreur : Il n'y a pas de place dans ton inventaire.");
        }

        e.setCancelled(true);
    }

    @Override
    public void onDefend(EntityDamageEvent e, Player player, IRaceData raceData) {
        if (raceData.getSubrace() == SubRace.ADORER.id
        || (raceData.getSubrace() == SubRace.MERCHANT.id && raceData.getRank() >= Rank.DRAGON.rank)) {

            double factor = switch (Rank.fromRank(raceData.getRank())) {
                case BEGINNER -> 0.05;
                case NOVICE -> 0.10;
                case INTERMEDIATE -> 0.20;
                case ADVANCE -> 0.30;
                case BIG, DRAGON -> 0.50;
            };

            e.setDamage(e.getFinalDamage() * (1.0 - factor));
        }
    }

    private void applyMerchantPermission(RaceProfile profile) {
        Player player = profile.getPlayer();

        if(profile.raceData.getRank() >= Rank.BEGINNER.rank) {
            if(!player.hasPermission(PERM_FORTUNE)) {
                IRace.addPermission(player, PERM_FORTUNE);
            }
        }

        if(profile.raceData.getRank() >= Rank.NOVICE.rank) {
            if(!player.hasPermission(PERM_VILLAGER)) {
                IRace.addPermission(player, PERM_VILLAGER);
            }
        }

        if(profile.raceData.getRank() >= Rank.BIG.rank) {
            if(!player.hasPermission(PERM_MILICIEN)) {
                IRace.addPermission(player, PERM_MILICIEN);
            }
        }
    }

    private void applyMerchantEffect(RaceProfile profile) {
        Player player = profile.getPlayer();

        if(profile.raceData.getRank() >= Rank.INTERMEDIATE.rank) {
            if(player.hasPotionEffect(PotionEffectType.HERO_OF_THE_VILLAGE)) {
                player.removePotionEffect(PotionEffectType.HERO_OF_THE_VILLAGE);
            }
            player.addPotionEffect(new PotionEffect(PotionEffectType.HERO_OF_THE_VILLAGE, PotionEffect.INFINITE_DURATION, 0, true, false, true));
        }
    }

    private void giveMerchantItem(RaceProfile profile) {
        Player player = profile.getPlayer();

        if(profile.raceData.getRank() >= Rank.BIG.rank) {
            ItemManager itemManager = Race.getInstance().getItemManager();

            ItemStack item = player.getInventory().getContents()[8];
            if(!(itemManager.getItem(item) instanceof MilicienEgg)) {
                MilicienEgg milicienEgg = (MilicienEgg) itemManager.getItem(RaceItem.MILICIEN_EGG.id);
                player.getInventory().setItem(8, milicienEgg.getItem());

                if(item != null && !player.getInventory().addItem(item).isEmpty()) {
                    player.getWorld().dropItemNaturally(player.getLocation(), item);
                }
            }
        }
    }

    public void applyMerchant(RaceProfile profile) {
        applyMerchantPermission(profile);
        applyMerchantEffect(profile);
        giveMerchantItem(profile);

        if(profile.raceData.getRank() >= Rank.DRAGON.rank && profile.raceData.getSubrace() == SubRace.MERCHANT.id) {
            applyAdorer(profile);
        }
    }

    private void applyAdorerPermission(RaceProfile profile) {
        Player player = profile.getPlayer();

        if(profile.raceData.getRank() >= Rank.ADVANCE.rank) {
            if(!player.hasPermission(PERM_SHARPNESS)) {
                IRace.addPermission(player, PERM_SHARPNESS);
            }
        }

        if(profile.raceData.getRank() >= Rank.BIG.rank) {
            if(!player.hasPermission(PERM_BLESS)) {
                IRace.addPermission(player, PERM_BLESS);
            }
        }
    }

    private void applyAdorerAttribute(RaceProfile profile) {
        Player player = profile.getPlayer();

        double multiplier = switch(Rank.fromRank(profile.raceData.getRank())) {
            case BEGINNER -> 0.05;
            case NOVICE -> 0.10;
            case INTERMEDIATE -> 0.20;
            case ADVANCE -> 0.30;
            case BIG, DRAGON -> 0.50;
        };

        IRace.replaceAttribute(player, Attribute.ATTACK_DAMAGE, STRENGTH, multiplier, AttributeModifier.Operation.ADD_SCALAR);
        IRace.replaceAttribute(player, Attribute.MOVEMENT_SPEED, SPEED, multiplier, AttributeModifier.Operation.ADD_SCALAR);

    }

    public void applyAdorer(RaceProfile profile) {
        applyAdorerPermission(profile);
        applyAdorerAttribute(profile);

        if(profile.raceData.getRank() >= Rank.DRAGON.rank && profile.raceData.getSubrace() == SubRace.ADORER.id) {
            applyMerchant(profile);
        }
    }

    @Override
    public void applyRacePerks(RaceProfile profile) {
        switch(SubRace.fromId(profile.raceData.getSubrace())) {
            case MERCHANT -> applyMerchant(profile);
            case ADORER -> applyAdorer(profile);
        }
    }

    @Override
    public void reapplyPerms(RaceProfile profile) {
        switch(SubRace.fromId(profile.raceData.getSubrace())) {
            case MERCHANT -> applyMerchantPermission(profile);
            case ADORER -> applyAdorerPermission(profile);
        }
    }

    @Override
    public void reapplyEffect(RaceProfile profile) {
        if (Objects.requireNonNull(SubRace.fromId(profile.raceData.getSubrace())) == SubRace.MERCHANT) {
            applyMerchantEffect(profile);
            giveMerchantItem(profile);
        }
    }

    @Override
    public void cleanup(RaceProfile profile) {
        Player player = profile.getPlayer();

        // Adorer
        IRace.removeAttribute(player, Attribute.ATTACK_DAMAGE, STRENGTH);
        IRace.removeAttribute(player, Attribute.MOVEMENT_SPEED, SPEED);
        IRace.removePermission(player, PERM_SHARPNESS);
        IRace.removePermission(player, PERM_BLESS);

        // Merchant
        IRace.removePermission(player, PERM_FORTUNE);
        IRace.removePermission(player, PERM_VILLAGER);
        IRace.removePermission(player, PERM_MILICIEN);

        PotionEffect hotv = player.getPotionEffect(PotionEffectType.HERO_OF_THE_VILLAGE);
        if(hotv != null && hotv.isInfinite()) {
            player.removePotionEffect(PotionEffectType.HERO_OF_THE_VILLAGE);
        }

        if(Race.getInstance().getItemManager().getItem(player.getInventory().getContents()[8]) instanceof IStaticItem) {
            player.getInventory().setItem(8, null);
        }
    }

    @Override
    public String getRankName(int rank) {
        return Rank.fromRank(rank).name;
    }

    @Override
    public int getExpRequired(int rank) {
        if(rank < Rank.values().length) {
            return Rank.fromRank(rank).expRequired;
        }
        return -1;
    }

    @Override
    public GUITypes getSubRaceGUI() {
        return GUITypes.KARYU;
    }

    @Override
    public String getSubraceName(int subrace) {
        return SubRace.fromId(subrace).name;
    }

    public enum Rank {

        BEGINNER(0, "Débutant", 0),
        NOVICE(1, "Novice", 200),
        INTERMEDIATE(2, "Intermédiaire", 1000),
        ADVANCE(3, "Avancé", 4000),
        BIG(4, "Grand", 10_000),
        DRAGON(5, "Dragon", 20_000);

        public final int rank;
        public final String name;
        public final int expRequired;

        Rank(int rank, String name, int expRequired) {
            this.rank = rank;
            this.name = name;
            this.expRequired = expRequired;
        }

        public static Rank fromRank(int n) {
            Rank[] ranks = Rank.values();
            if(n < 0) {
                return ranks[0];
            }
            else if(n < ranks.length) {
                return ranks[n];
            }
            return ranks[ranks.length-1];
        }
    }

    public enum SubRace {
        MERCHANT(0,"Marchant", Material.EMERALD),
        ADORER(1, "Adorateur", Material.IRON_SWORD);

        public final int id;
        public final String name;
        public final Material icon;

        SubRace(int id, String name, Material icon) {
            this.id = id;
            this.name = name;
            this.icon = icon;
        }

        public ItemStack getItem() {
            ItemStack item = new ItemStack(icon);
            ItemMeta meta = item.getItemMeta();

            if(meta == null)
                return item;

            meta.setDisplayName(ChatColor.WHITE + name);

            item.setItemMeta(meta);
            return item;
        }

        public static SubRace fromId(int id) {
            SubRace[] sub = SubRace.values();
            if(id < sub.length) {
                return sub[id];
            }
            return sub[0];
        }

    }
}
