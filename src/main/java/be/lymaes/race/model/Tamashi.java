package be.lymaes.race.model;

import be.lymaes.race.Race;
import be.lymaes.race.RaceProfile;
import be.lymaes.race.ability.*;
import be.lymaes.race.ability.model.*;
import be.lymaes.race.data.TamashiData;
import be.lymaes.race.gui.GUITypes;
import be.lymaes.race.item.model.FlyChargeBall;
import be.lymaes.race.item.IStaticItem;
import be.lymaes.race.item.RaceItem;
import be.lymaes.race.manager.ItemManager;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Map;

public class Tamashi implements IRace<TamashiData>, ISubRaceable, IRankable {

    public static final String PERM_HOME = "race.tamashi.home";
    public static final String PERM_FLY_CHARGE = "race.tamashi.air.fly_charge";

    public static final double DISTANCE_SQUARED = 200 * 200;

    private static final NamespacedKey STRENGTH = NamespacedKey.fromString("tamashi:strength");
    private static final NamespacedKey SPEED = NamespacedKey.fromString("tamashi:speed");

    public Map<String, Ability> getAbilities() {
        double[] waterStrengthFactor = new double[] {0.10, 0.20, 0.30, 0.40, 0.50, 1.00};
        double[] earthAbsorptionFactor = new double[] {0.20, 0.30, 0.40, 0.60, 0.80, 0.90};
        int[] fireTimes = new int[] {1, 2, 5, 10, 20, 30};
        int[] flyTimes = new int[] {5, 10, 20, 30, 5*60};
        return Map.of(
                AbilityKey.TAMASHI.EXP, new TamashiExp(DISTANCE_SQUARED),
                AbilityKey.MONOPHOBIA, new Monophobia(DISTANCE_SQUARED),

                AbilityKey.AQUATIC_STRENGTH, new AquaticStrength(waterStrengthFactor),

                AbilityKey.TAMASHI.EARTH_ABSORPTION, new Absorption(earthAbsorptionFactor),
                AbilityKey.DIRT_EATER, new DirtEater(),

                AbilityKey.FIREBALL, new Fireball(),
                AbilityKey.FIRE_ASPECT, new FireAspect(fireTimes),

                AbilityKey.FLY_CHARGE, new FlyCharge(flyTimes),
                AbilityKey.FEATHER_FALL, new FeatherFall()
        );
    }

    @Override
    public void addExpAbilities(RaceProfile profile) {
        profile.addAbility(AbilityKey.TAMASHI.EXP);
    }

    public void removeExpAbilities(RaceProfile profile) {
        profile.removeAbility(AbilityKey.TAMASHI.EXP);
    }

    private void applyWaterAbilities(RaceProfile profile) {
        profile.addAbility(AbilityKey.AQUATIC_STRENGTH);
    }

    private void applyEarthAbilities(RaceProfile profile) {
        profile.addAbility(AbilityKey.TAMASHI.EARTH_ABSORPTION);
        profile.addAbility(AbilityKey.DIRT_EATER);
    }

    private void applyFireAbilities(RaceProfile profile) {
        profile.addAbility(AbilityKey.FIREBALL);
        profile.addAbility(AbilityKey.FIRE_ASPECT);
    }

    private void applyAirAbilities(RaceProfile profile) {
        profile.addAbility(AbilityKey.FLY_CHARGE);
        profile.addAbility(AbilityKey.FEATHER_FALL);
    }

    public void applySharedAbilities(RaceProfile profile) {
        profile.addAbility(AbilityKey.MONOPHOBIA);
    }

    private void applyWaterEffect(Player player, TamashiData data) {
        int dolphinGraceLvl = switch(Rank.fromRank(data.getRank())) {
            case EMBRYO, CHILD -> 1;
            case ACCOMPLISHED, HALF_GOD -> 2;
            case KAMI, OKAMI -> 3;
        };

        if(player.hasPotionEffect(PotionEffectType.CONDUIT_POWER)) {
            player.removePotionEffect(PotionEffectType.CONDUIT_POWER);
        }
        player.addPotionEffect(new PotionEffect(PotionEffectType.CONDUIT_POWER, PotionEffect.INFINITE_DURATION, 0, true, false, true));


        if(player.hasPotionEffect(PotionEffectType.DOLPHINS_GRACE)) {
            player.removePotionEffect(PotionEffectType.DOLPHINS_GRACE);
        }
        player.addPotionEffect(new PotionEffect(PotionEffectType.DOLPHINS_GRACE, PotionEffect.INFINITE_DURATION, dolphinGraceLvl-1, true, false, true));
    }

    private void applyWater(Player player, RaceProfile profile, TamashiData data) {
        applyWaterAbilities(profile);
        applyWaterEffect(player, data);
    }

    private void applyEarth(Player player, RaceProfile profile, TamashiData data) {
        applyEarthAbilities(profile);
    }

    private void applyFireAttribute(Player player, TamashiData data) {
        double multiplier = switch(Rank.fromRank(data.getRank())) {
            case EMBRYO, CHILD -> 0.00;
            case ACCOMPLISHED -> 0.20;
            case HALF_GOD -> 0.30;
            case KAMI -> 0.40;
            case OKAMI -> 0.50;
        };

        if(multiplier > 0.0) {
            IRace.replaceAttribute(player, Attribute.ATTACK_DAMAGE, STRENGTH, multiplier, AttributeModifier.Operation.ADD_SCALAR);
        }
    }

    private void applyFireEffect(Player player, TamashiData data) {
        if(player.hasPotionEffect(PotionEffectType.FIRE_RESISTANCE)) {
            player.removePotionEffect(PotionEffectType.FIRE_RESISTANCE);
        }
        player.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, PotionEffect.INFINITE_DURATION, 0, true, false, true));
    }

    private void applyFire(Player player, RaceProfile profile, TamashiData data) {
        applyFireAbilities(profile);
        applyFireEffect(player, data);
        applyFireAttribute(player, data);
    }

    private void applyAirPermission(Player player, TamashiData data) {
        if(data.getRank() >= Rank.OKAMI.rank) {
            if(!player.hasPermission(PERM_FLY_CHARGE)) {
                IRace.addPermission(player, PERM_FLY_CHARGE);
            }
        }
    }

    private void applyAirAttribute(Player player, TamashiData data) {
        double multiplier = switch(Rank.fromRank(data.getRank())) {
            case EMBRYO -> 0.10;
            case CHILD -> 0.20;
            case ACCOMPLISHED -> 0.30;
            case HALF_GOD -> 0.40;
            case KAMI -> 0.80;
            case OKAMI -> 1.20;
        };

        IRace.replaceAttribute(player, Attribute.MOVEMENT_SPEED, SPEED, multiplier, AttributeModifier.Operation.ADD_SCALAR);
    }

    private void giveAirItem(Player player, TamashiData data) {
        ItemManager itemManager = Race.getInstance().getItemManager();

        ItemStack item = player.getInventory().getContents()[8];
        if(!(itemManager.getItem(item) instanceof FlyChargeBall)) {
            FlyChargeBall flyCharge = (FlyChargeBall) itemManager.getItem(RaceItem.FLY_CHARGE.id);
            player.getInventory().setItem(8, flyCharge.getItem());

            if(item != null && !player.getInventory().addItem(item).isEmpty()) {
                player.getWorld().dropItemNaturally(player.getLocation(), item);
            }
        }
    }

    private void applyAir(Player player, RaceProfile profile, TamashiData data) {
        applyAirAbilities(profile);
        applyAirPermission(player, data);
        applyAirAttribute(player, data);
        giveAirItem(player, data);
    }

    @Override
    public void applyRacePerks(Player player, RaceProfile profile, TamashiData data) {
        if(profile.raceData == data) {
            if(getExpRequired(data.getRank() + 1) != -1) {
                addExpAbilities(profile);
            } else {
                removeExpAbilities(profile);
            }
        }

        applySharedAbilities(profile);
        reapplyPerms(player, data);

        switch(SubRace.fromId(data.getSubrace())) {
            case WATER -> applyWater(player, profile, data);
            case EARTH -> applyEarth(player, profile, data);
            case FIRE -> applyFire(player, profile, data);
            case AIR -> applyAir(player, profile, data);
        }
    }

    @Override
    public void reapplyPerms(Player player, TamashiData data) {
        if(!player.hasPermission(PERM_HOME)) {
            IRace.addPermission(player, PERM_HOME);
        }

        if(data.getSubrace() == SubRace.AIR.id) {
            applyAirPermission(player, data);
        }
    }

    @Override
    public void reapplyEffect(Player player, TamashiData data) {
        switch(SubRace.fromId(data.getSubrace())) {
            case WATER -> applyWaterEffect(player, data);
            case FIRE -> applyFireEffect(player, data);
            case AIR -> giveAirItem(player, data);
        }
    }

    @Override
    public void cleanup(Player player, RaceProfile profile) {
        IRace.removePermission(player, PERM_HOME);

        // water
        PotionEffect conduit = player.getPotionEffect(PotionEffectType.CONDUIT_POWER);
        if(conduit != null && conduit.isInfinite()) {
            player.removePotionEffect(PotionEffectType.CONDUIT_POWER);
        }

        PotionEffect dolphinGrace = player.getPotionEffect(PotionEffectType.DOLPHINS_GRACE);
        if(dolphinGrace != null && dolphinGrace.isInfinite()) {
            player.removePotionEffect(PotionEffectType.DOLPHINS_GRACE);
        }

        // fire
        PotionEffect fireResistance = player.getPotionEffect(PotionEffectType.FIRE_RESISTANCE);
        if(fireResistance != null && fireResistance.isInfinite()) {
            player.removePotionEffect(PotionEffectType.FIRE_RESISTANCE);
        }

        IRace.removeAttribute(player, Attribute.ATTACK_DAMAGE, STRENGTH);

        // air
        if(Race.getInstance().getItemManager().getItem(player.getInventory().getContents()[8]) instanceof IStaticItem) {
            player.getInventory().setItem(8, null);
        }

        IRace.removeAttribute(player, Attribute.MOVEMENT_SPEED, SPEED);

        IRace.removePermission(player, PERM_FLY_CHARGE);
        player.setAllowFlight(false);

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
        return GUITypes.TAMASHI;
    }

    @Override
    public String getSubraceName(int subrace) {
        return SubRace.fromId(subrace).name;
    }

    public enum Rank {
        EMBRYO(0, "Embryon", 0),
        CHILD(1, "Enfant", 400),
        ACCOMPLISHED(2, "Accompli", 4000),
        HALF_GOD(3, "Semi Divin", 10_000),
        KAMI(4, "Kami", 20_000),
        OKAMI(5, "Okami", 40_000);

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
        WATER(0, "Eau", Material.BLUE_DYE),
        EARTH(1, "Terre", Material.ORANGE_DYE),
        FIRE(2, "Feu", Material.RED_DYE),
        AIR(3, "Air", Material.GREEN_DYE);

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
