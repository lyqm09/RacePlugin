package be.lymaes.race.model;

import be.lymaes.race.Messager;
import be.lymaes.race.Race;
import be.lymaes.race.RaceProfile;
import be.lymaes.race.gui.GUITypes;
import be.lymaes.race.item.IStaticItem;
import be.lymaes.race.item.MilicienEgg;
import be.lymaes.race.item.RaceItem;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.block.Block;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
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
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.permissions.PermissionAttachmentInfo;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;

public class Karyu implements IRace, ISubRaceable {

    private static final NamespacedKey STRENGTH = NamespacedKey.fromString("karyu:strength");
    private static final NamespacedKey SPEED = NamespacedKey.fromString("karyu:speed");

    public static final String PERM_FORTUNE = "race.karyu.merchant.fortune";
    public static final String PERM_VILLAGER = "race.karyu.merchant.villager";
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

    @EventHandler
    public void onMerchant(InventoryClickEvent e) {
        if(!(e.getClickedInventory() instanceof MerchantInventory inventory))
            return;

        if(!(e.getWhoClicked() instanceof Player player))
            return;

        RaceProfile profile = Race.getInstance().getRaceManager().getProfile(player);
        if(profile.raceData.getRace() != RaceType.KARYU)
            return;

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

            addExp(profile, 10 * tradeCount);
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

    @EventHandler
    public void onKill(EntityDeathEvent e) {
        if(!(e.getEntity() instanceof Monster))
            return;

        Entity damager = e.getDamageSource().getCausingEntity();
        if(!(damager instanceof Player player))
            return;

        RaceProfile profile = Race.getInstance().getRaceManager().getProfile(player);
        if(profile.raceData.getRace() != RaceType.KARYU)
            return;

        if(profile.raceData.getSubrace() == SubRace.ADORER.id) {
            addExp(profile, 1);
        }
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent e) {
        Player player = e.getPlayer();
        RaceProfile profile = Race.getInstance().getRaceManager().getProfile(player);
        if(profile.raceData.getRace() != RaceType.KARYU)
            return;

        ItemStack item = e.getItem();

        if(profile.raceData.getSubrace() == SubRace.MERCHANT.id
        || (profile.raceData.getSubrace() == SubRace.ADORER.id && profile.raceData.getRank() >= Rank.DRAGON.rank)) {

            emeraldExchange: if(profile.raceData.getRank() >= Rank.ADVANCE.rank) {

                if (item == null || item.getType() != Material.EMERALD)
                    break emeraldExchange;

                if (e.getAction() != Action.RIGHT_CLICK_AIR)
                    break emeraldExchange;

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

            summonMilicien: if(profile.raceData.getRank() >= Rank.BIG.rank) {
                if(!(Race.getInstance().getItemManager().getItem(item) instanceof MilicienEgg))
                    break summonMilicien;

                Block clickedBlock = e.getClickedBlock();
                if (clickedBlock == null) {
                    break summonMilicien;
                }

                e.setCancelled(true);

                if(player.hasCooldown(item)) {
                    break summonMilicien;
                }

                Location spawnLoc = clickedBlock.getRelative(e.getBlockFace())
                        .getLocation()
                        .add(0.5, 0, 0.5);

                player.getWorld().spawn(spawnLoc, IronGolem.class, golem -> {
                    golem.setCustomName("Milicien");
                    golem.setCustomNameVisible(true);
                });

                player.setCooldown(item, 60 * 20);
            }
        }
    }

    @EventHandler
    public void onDamage(EntityDamageEvent e) {
        if(!(e.getEntity() instanceof Player player))
            return;

        RaceProfile profile = Race.getInstance().getRaceManager().getProfile(player);
        if(profile.raceData.getRace() != RaceType.KARYU)
            return;

        if(profile.raceData.getSubrace() == SubRace.ADORER.id
        || (profile.raceData.getSubrace() == SubRace.MERCHANT.id && profile.raceData.getRank() >= Rank.DRAGON.rank)) {

            double factor = switch(Rank.fromRank(profile.raceData.getRank())) {
                case BEGINNER -> 0.05;
                case NOVICE -> 0.10;
                case INTERMEDIATE -> 0.20;
                case ADVANCE -> 0.30;
                case BIG, DRAGON -> 0.50;
            };

            e.setDamage(e.getFinalDamage() * (1.0 - factor));
        }
    }

    public static PermissionAttachment getPermission(Player player) {
        for (PermissionAttachmentInfo info : player.getEffectivePermissions()) {
            PermissionAttachment attachment = info.getAttachment();

            if (attachment != null && attachment.getPlugin().equals(Race.getInstance())) {
                return attachment;
            }
        }
        return null;
    }

    public static void addPermission(Player player, String perm) {
        PermissionAttachment attachment = getPermission(player);
        if(attachment == null)
            attachment = player.addAttachment(Race.getInstance());

        attachment.setPermission(perm, true);
    }

    public static void removePermission(Player player, String perm) {
        PermissionAttachment attachment = getPermission(player);
        if(attachment == null)
            return;

        attachment.setPermission(perm, false);
    }

    private void loadMerchantPermission(RaceProfile profile) {
        Player player = profile.getPlayer();

        if(profile.raceData.getRank() >= Rank.BEGINNER.rank) {
            if(!player.hasPermission(PERM_FORTUNE)) {
                addPermission(player, PERM_FORTUNE);
            }
        }

        if(profile.raceData.getRank() >= Rank.NOVICE.rank) {
            if(!player.hasPermission(PERM_VILLAGER)) {
                addPermission(player, PERM_VILLAGER);
            }
        }
    }

    private void loadMerchantEffect(RaceProfile profile) {
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
            ItemStack item = player.getInventory().getContents()[8];
            if(!(Race.getInstance().getItemManager().getItem(item) instanceof MilicienEgg)) {
                player.getInventory().setItem(8, RaceItem.MILICIEN_EGG.getItem());

                if(item != null && !player.getInventory().addItem(item).isEmpty()) {
                    player.getWorld().dropItemNaturally(player.getLocation(), item);
                }
            }
        }
    }

    public void loadMerchant(RaceProfile profile) {
        loadMerchantPermission(profile);
        loadMerchantEffect(profile);
        giveMerchantItem(profile);

        if(profile.raceData.getRank() >= Rank.DRAGON.rank && profile.raceData.getSubrace() == SubRace.MERCHANT.id) {
            loadAdorer(profile);
        }
    }

    private void loadAdorerPermission(RaceProfile profile) {
        Player player = profile.getPlayer();

        if(profile.raceData.getRank() >= Rank.ADVANCE.rank) {
            if(!player.hasPermission(PERM_SHARPNESS)) {
                addPermission(player, PERM_SHARPNESS);
            }
        }

        if(profile.raceData.getRank() >= Rank.BIG.rank) {
            if(!player.hasPermission(PERM_BLESS)) {
                addPermission(player, PERM_BLESS);
            }
        }
    }

    private void loadAdorerAttribute(RaceProfile profile) {
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

    public void loadAdorer(RaceProfile profile) {
        loadAdorerPermission(profile);
        loadAdorerAttribute(profile);

        if(profile.raceData.getRank() >= Rank.DRAGON.rank && profile.raceData.getSubrace() == SubRace.ADORER.id) {
            loadMerchant(profile);
        }
    }

    @Override
    public void reloadEffect(RaceProfile profile) {
        if (Objects.requireNonNull(SubRace.fromId(profile.raceData.getSubrace())) == SubRace.MERCHANT) {
            loadMerchantEffect(profile);
            giveMerchantItem(profile);
        }
    }

    @Override
    public void cleanup(RaceProfile profile) {
        Player player = profile.getPlayer();

        // Adorer
        IRace.removeAttribute(player, Attribute.ATTACK_DAMAGE, STRENGTH);
        IRace.removeAttribute(player, Attribute.MOVEMENT_SPEED, SPEED);
        removePermission(player, PERM_SHARPNESS);
        removePermission(player, PERM_BLESS);

        // Merchant
        removePermission(player, PERM_FORTUNE);
        removePermission(player, PERM_VILLAGER);

        PotionEffect hotv = player.getPotionEffect(PotionEffectType.HERO_OF_THE_VILLAGE);
        if(hotv != null && hotv.isInfinite()) {
            player.removePotionEffect(PotionEffectType.HERO_OF_THE_VILLAGE);
        }

        if(Race.getInstance().getItemManager().getItem(player.getInventory().getContents()[8]) instanceof IStaticItem) {
            player.getInventory().setItem(8, null);
        }
    }

    public void rankUp(RaceProfile profile) {
        profile.rankUp();

        Messager.sendRankupTitle(profile, Rank.fromRank(profile.raceData.getRank()).name);

        loadRank(profile);
    }

    @Override
    public void loadRank(RaceProfile profile) {
        switch(SubRace.fromId(profile.raceData.getSubrace())) {
            case MERCHANT -> loadMerchant(profile);
            case ADORER -> loadAdorer(profile);
        }
    }

    public void checkRankup(RaceProfile profile) {
        if(profile.raceData.getRank() < Rank.values().length-1) {

            Rank rank = Rank.fromRank(profile.raceData.getRank() + 1);

            int required = rank.expRequired;
            if (profile.raceData.getExp() < required)
                return;

            profile.raceData.subExp(required);
            rankUp(profile);
            checkRankup(profile);
        }
    }

    @Override
    public void addExp(RaceProfile profile, int n) {
        profile.addExp(n);
        checkRankup(profile);
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
            if(n < ranks.length) {
                return ranks[n];
            }
            return ranks[0];
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
