package be.lymaes.race.model;

import be.lymaes.race.Messager;
import be.lymaes.race.Race;
import be.lymaes.race.RaceProfile;
import be.lymaes.race.manager.RaceManager;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityTargetLivingEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.EquipmentSlotGroup;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.EnumSet;

public class Oni implements IRace {

    private static final NamespacedKey STRENGTH = NamespacedKey.fromString("oni:strength");
    private static final NamespacedKey SPEED = NamespacedKey.fromString("oni:speed");
    private static final NamespacedKey HEALTH = NamespacedKey.fromString("oni:health");

    private static final EnumSet<Material> MEATS = EnumSet.of(
            Material.BEEF, Material.COOKED_BEEF,
            Material.PORKCHOP, Material.COOKED_PORKCHOP,
            Material.CHICKEN, Material.COOKED_CHICKEN,
            Material.MUTTON, Material.COOKED_MUTTON,
            Material.RABBIT, Material.COOKED_RABBIT,
            Material.COD, Material.COOKED_COD,
            Material.SALMON, Material.COOKED_SALMON,
            Material.TROPICAL_FISH, Material.PUFFERFISH,
            Material.ROTTEN_FLESH
    );

    private static final EnumSet<Material> IGNORED_CONSUMABLES = EnumSet.of(
            Material.POTION,
            Material.MILK_BUCKET,
            Material.HONEY_BOTTLE
    );

    public void task(RaceManager manager) {

        for(RaceProfile profile : manager.getRaceProfiles(RaceType.ONI)) {
            if(profile.player.isInWater()) {
                profile.player.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 2 * 20, 1, true, false, true));
                profile.player.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 2 * 20, 1, true, false, true));
            }
        }

    }

    @EventHandler
    public void onKill(EntityDeathEvent e) {
        if(!(e.getEntity() instanceof Mob))
            return;

        Entity damager = e.getDamageSource().getCausingEntity();
        if(!(damager instanceof Player player))
            return;

        RaceProfile profile = Race.getInstance().getRaceManager().getProfile(player);
        if(profile.race != RaceType.ONI)
            return;

        addExp(profile, 1);
    }

    @EventHandler
    public void onConsume(PlayerItemConsumeEvent e) {
        Player player = e.getPlayer();
        RaceProfile profile = Race.getInstance().getRaceManager().getProfile(player);
        if(profile.race != RaceType.ONI)
            return;

        ItemStack item = e.getItem();
        Material consumedItem = item.getType();

        if (IGNORED_CONSUMABLES.contains(consumedItem)) {
            return;
        }

        if (MEATS.contains(consumedItem)) {
            return;
        }

        player.sendMessage("Beurk !");
        e.setCancelled(true);
    }

    @EventHandler
    public void onDamage(EntityDamageEvent e) {
        if(!(e.getEntity() instanceof Player player))
            return;

        RaceProfile profile = Race.getInstance().getRaceManager().getProfile(player);
        if(profile.race != RaceType.ONI)
            return;

        double factor = switch(Rank.fromRank(profile.getRank())) {
            case EVOLVED -> 0.05;
            case LIEUTENANT -> 0.10;
            case CAPTAIN -> 0.15;
            case COMMANDER -> 0.20;
            case LORD -> 0.25;
            case GENERAL -> 0.50;
            default -> 0.0;
        };

        e.setDamage(e.getFinalDamage() * (1.0 - factor));
    }

    @EventHandler
    public void onTarget(EntityTargetLivingEntityEvent e) {
        if(!(e.getEntity() instanceof Monster))
            return;

        if(!(e.getTarget() instanceof Player player))
            return;

        RaceProfile profile = Race.getInstance().getRaceManager().getProfile(player);
        if(profile.race != RaceType.ONI)
            return;

        if(profile.getRank() >= Rank.CAPTAIN.rank) {

            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent e) {
        Player player = e.getPlayer();
        RaceProfile profile = Race.getInstance().getRaceManager().getProfile(player);
        if(profile.race != RaceType.ONI)
            return;

        if(profile.getRank() < Rank.GENERAL.rank)
            return;

        ItemStack item = e.getItem();
        if(item == null || item.getType() != Material.BLAZE_POWDER)
            return;

        if (e.getAction() != Action.RIGHT_CLICK_AIR)
            return;

        int amount = item.getAmount();
        if (amount > 1) {
            item.setAmount(amount - 1);
        } else {
            player.getInventory().setItemInMainHand(null);
        }

        player.launchProjectile(Fireball.class);

        e.setCancelled(true);
    }

    @Override
    public void cleanup(RaceProfile profile) {

        IRace.removeAttribute(profile.player, Attribute.ATTACK_DAMAGE, STRENGTH);
        IRace.removeAttribute(profile.player, Attribute.MOVEMENT_SPEED, SPEED);
        IRace.removeAttribute(profile.player, Attribute.MAX_HEALTH, HEALTH);

        PotionEffect regeneration = profile.player.getPotionEffect(PotionEffectType.REGENERATION);
        if(regeneration != null && regeneration.isInfinite()) {
            profile.player.removePotionEffect(PotionEffectType.REGENERATION);
        }
    }

    private void rankUp(RaceProfile profile) {
        profile.rankUp();

        Rank rank = Rank.fromRank(profile.getRank());

        Messager.sendRankupTitle(profile, rank.name);

        loadRank(profile);
    }

    private void loadAttribute(RaceProfile profile) {
        Rank rank = Rank.fromRank(profile.getRank());
        double multiplier = switch(rank) {
            case EVOLVED -> 0.05;
            case LIEUTENANT -> 0.10;
            case CAPTAIN -> 0.15;
            case COMMANDER -> 0.20;
            case LORD -> 0.25;
            case GENERAL -> 0.50;
            default -> 0.0;
        };

        IRace.replaceAttribute(profile.player, Attribute.ATTACK_DAMAGE, STRENGTH, multiplier, AttributeModifier.Operation.ADD_SCALAR);
        IRace.replaceAttribute(profile.player, Attribute.MOVEMENT_SPEED, SPEED, multiplier, AttributeModifier.Operation.ADD_SCALAR);

        if(profile.getRank() >= Rank.LORD.rank) {
            AttributeInstance health = profile.player.getAttribute(Attribute.MAX_HEALTH);
            if(health != null) {
                boolean isAbsent = true;
                for (AttributeModifier mod : health.getModifiers()) {
                    if (mod.getKey().equals(HEALTH)) {
                        isAbsent = false;
                        break;
                    }
                }

                if(isAbsent) {
                    double ratio = profile.player.getHealth()/health.getValue();
                    health.addModifier(new AttributeModifier(HEALTH, 20, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlotGroup.ANY));
                    profile.player.setHealth(ratio * health.getValue());
                }
            }
        }
    }

    private void loadEffect(RaceProfile profile) {
        if(profile.getRank() >= Rank.COMMANDER.rank) {
            if(profile.player.hasPotionEffect(PotionEffectType.REGENERATION)) {
                profile.player.removePotionEffect(PotionEffectType.REGENERATION);
            }
            profile.player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, PotionEffect.INFINITE_DURATION, 0, true, false, false));
        }
    }

    @Override
    public void loadRank(RaceProfile profile) {
        loadEffect(profile);
        loadAttribute(profile);
    }

    @Override
    public void reloadEffect(RaceProfile profile) {
        loadEffect(profile);
    }

    public void checkRankup(RaceProfile profile, boolean hasKazanStone) {
        if(profile.getRank() < Rank.values().length-1) {

            Rank rank = Rank.fromRank(profile.getRank() + 1);

            if (rank == Rank.GENERAL) {
                if(!hasKazanStone)
                    return;
            }

            int required = rank.expRequired;
            if (profile.getExp() < required)
                return;

            System.out.println(profile.getExp() + " < " + required);

            profile.subExp(required);
            rankUp(profile);
            checkRankup(profile);
        }
    }

    public void checkRankup(RaceProfile profile) {
        checkRankup(profile, false);
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
        return 0;
    }

    public enum Rank {
        BEAST(0, "Bête", 0),
        EVOLVED(1, "Évolué", 20),
        LIEUTENANT(2, "Lieutenant", 100),
        CAPTAIN(3, "Capitaine", 500),
        COMMANDER(4, "Commandant", 2000),
        LORD(5, "Seigneur", 5000),
        GENERAL(6, "Général", 10000);

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
            else if(n >= ranks.length) {
                return ranks[ranks.length-1];
            }
            return ranks[n];
        }
    }
}
