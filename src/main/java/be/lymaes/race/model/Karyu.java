package be.lymaes.race.model;

import be.lymaes.race.Race;
import be.lymaes.race.RaceProfile;
import be.lymaes.race.ability.*;
import be.lymaes.race.ability.model.Absorption;
import be.lymaes.race.ability.model.KaryuAdorerExp;
import be.lymaes.race.ability.model.KaryuMerchantExp;
import be.lymaes.race.ability.model.LootTransformer;
import be.lymaes.race.data.KaryuData;
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
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Map;
import java.util.Objects;

public class Karyu implements IRace<KaryuData>, ISubRaceable, IRankable {

    private static final NamespacedKey STRENGTH = NamespacedKey.fromString("karyu:strength");
    private static final NamespacedKey SPEED = NamespacedKey.fromString("karyu:speed");

    public static final String PERM_FORTUNE = "race.karyu.merchant.fortune";
    public static final String PERM_VILLAGER = "race.karyu.merchant.villager";
    public static final String PERM_MILICIEN = "race.karyu.merchant.milicien";
    public static final String PERM_SHARPNESS = "race.karyu.adorer.sharpness";
    public static final String PERM_BLESS = "race.karyu.adorer.bless";

    public Map<String, Ability> getAbilities() {
        double[] adorerAbsorptionFactor = new double[] {0.05, 0.10, 0.20, 0.30, 0.50, 0.50};
        Material[] minerals = new Material[] {
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
                Material.REDSTONE};

        return Map.of(
                AbilityKey.KARYU.MERCHANT_EXP, new KaryuMerchantExp(),
                AbilityKey.KARYU.ADORER_EXP, new KaryuAdorerExp(),
                AbilityKey.KARYU.ADORER_ABSORPTION, new Absorption(adorerAbsorptionFactor),
                AbilityKey.EMERALD_TRANSFORMER, new LootTransformer(minerals)
        );
    }

    @Override
    public void addExpAbilities(RaceProfile profile) {
        int sub = profile.raceData.getSubrace();

        if(sub == SubRace.MERCHANT.id) {
            profile.addAbility(AbilityKey.KARYU.MERCHANT_EXP);
        }
        else if(sub == SubRace.ADORER.id) {
            profile.addAbility(AbilityKey.KARYU.ADORER_EXP);
        }
    }

    public void removeExpAbilities(RaceProfile profile) {
        profile.removeAbility(AbilityKey.KARYU.MERCHANT_EXP);
        profile.removeAbility(AbilityKey.KARYU.ADORER_EXP);
    }

    private void applyMerchantAbilities(RaceProfile profile, KaryuData data) {
        int rank = data.getRank();

        if (rank >= Rank.ADVANCE.rank) {
            profile.addAbility(AbilityKey.EMERALD_TRANSFORMER);
        }
    }

    private void applyAdorerAbilities(RaceProfile profile, KaryuData data) {
        profile.addAbility(AbilityKey.KARYU.ADORER_ABSORPTION);
    }

    private void applyMerchantPermission(Player player, KaryuData data) {
        if(data.getRank() >= Rank.BEGINNER.rank) {
            if(!player.hasPermission(PERM_FORTUNE)) {
                IRace.addPermission(player, PERM_FORTUNE);
            }
        }

        if(data.getRank() >= Rank.NOVICE.rank) {
            if(!player.hasPermission(PERM_VILLAGER)) {
                IRace.addPermission(player, PERM_VILLAGER);
            }
        }

        if(data.getRank() >= Rank.BIG.rank) {
            if(!player.hasPermission(PERM_MILICIEN)) {
                IRace.addPermission(player, PERM_MILICIEN);
            }
        }
    }

    private void applyMerchantEffect(Player player, KaryuData data) {
        if(data.getRank() >= Rank.INTERMEDIATE.rank) {
            if(player.hasPotionEffect(PotionEffectType.HERO_OF_THE_VILLAGE)) {
                player.removePotionEffect(PotionEffectType.HERO_OF_THE_VILLAGE);
            }
            player.addPotionEffect(new PotionEffect(PotionEffectType.HERO_OF_THE_VILLAGE, PotionEffect.INFINITE_DURATION, 0, true, false, true));
        }
    }

    private void giveMerchantItem(Player player, KaryuData data) {
        if(data.getRank() >= Rank.BIG.rank) {
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

    public void applyMerchant(Player player, RaceProfile profile, KaryuData data) {
        applyMerchantAbilities(profile, data);
        applyMerchantPermission(player, data);
        applyMerchantEffect(player, data);
        giveMerchantItem(player, data);

        if(data.getRank() >= Rank.DRAGON.rank && data.getSubrace() == SubRace.MERCHANT.id) {
            applyAdorer(player, profile, data);
        }
    }

    private void applyAdorerPermission(Player player, KaryuData data) {
        if(data.getRank() >= Rank.ADVANCE.rank) {
            if(!player.hasPermission(PERM_SHARPNESS)) {
                IRace.addPermission(player, PERM_SHARPNESS);
            }
        }

        if(data.getRank() >= Rank.BIG.rank) {
            if(!player.hasPermission(PERM_BLESS)) {
                IRace.addPermission(player, PERM_BLESS);
            }
        }
    }

    private void applyAdorerAttribute(Player player, KaryuData data) {
        double multiplier = switch(Rank.fromRank(data.getRank())) {
            case BEGINNER -> 0.05;
            case NOVICE -> 0.10;
            case INTERMEDIATE -> 0.20;
            case ADVANCE -> 0.30;
            case BIG, DRAGON -> 0.50;
        };

        IRace.replaceAttribute(player, Attribute.ATTACK_DAMAGE, STRENGTH, multiplier, AttributeModifier.Operation.ADD_SCALAR);
        IRace.replaceAttribute(player, Attribute.MOVEMENT_SPEED, SPEED, multiplier, AttributeModifier.Operation.ADD_SCALAR);

    }

    public void applyAdorer(Player player, RaceProfile profile, KaryuData data) {
        applyAdorerAbilities(profile, data);
        applyAdorerPermission(player, data);
        applyAdorerAttribute(player, data);

        if(data.getRank() >= Rank.DRAGON.rank && data.getSubrace() == SubRace.ADORER.id) {
            applyMerchant(player, profile, data);
        }
    }

    @Override
    public void applyRacePerks(Player player, RaceProfile profile, KaryuData data) {
        if(profile.raceData == data) {
            if(getExpRequired(data.getRank() + 1) != -1) {
                addExpAbilities(profile);
            } else {
                removeExpAbilities(profile);
            }
        }

        switch(SubRace.fromId(data.getSubrace())) {
            case MERCHANT -> applyMerchant(player, profile, data);
            case ADORER -> applyAdorer(player, profile, data);
        }
    }

    @Override
    public void reapplyPerms(Player player, KaryuData data) {
        switch(SubRace.fromId(data.getSubrace())) {
            case MERCHANT -> applyMerchantPermission(player, data);
            case ADORER -> applyAdorerPermission(player, data);
        }
    }

    @Override
    public void reapplyEffect(Player player, KaryuData data) {
        if (Objects.requireNonNull(SubRace.fromId(data.getSubrace())) == SubRace.MERCHANT) {
            applyMerchantEffect(player, data);
            giveMerchantItem(player, data);
        }
    }

    @Override
    public void cleanup(Player player, RaceProfile profile) {
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

        for(String key : getAbilities().keySet()) {
            profile.removeAbility(key);
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
