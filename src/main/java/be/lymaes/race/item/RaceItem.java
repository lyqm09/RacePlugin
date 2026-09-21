package be.lymaes.race.item;

import be.lymaes.race.item.model.*;
import org.bukkit.Material;

import java.util.function.Supplier;

public enum RaceItem {

    KAZAN_STONE("kazan_stone", "Pierre de Kazan", Material.CHARCOAL, KazanStone::new),
    TAMASHI_HEART("tamashi_heart", "Coeur de Tamashi", Material.GLOWSTONE_DUST, TamashiHeart::new),
    FLY_CHARGE("fly_charge", "Orbe de lévitation", Material.WIND_CHARGE, FlyChargeBall::new),
    MILICIEN_EGG("milicien_egg", "Oeuf de Milicien", Material.IRON_GOLEM_SPAWN_EGG, MilicienEgg::new),
    PRIMORDIAL_ONI_BLOOD("primordial_oni_blood", "Sang d'Oni primordial", Material.SPLASH_POTION, PrimordialOniBlood::new),
    SANCTUARY_HEART("sanctuary_heart", "Coeur de Sanctuaire", Material.ENCHANTED_GOLDEN_APPLE, SanctuaryHeart::new),
    VILLAGE_HEART("village_heart", "Coeur de village", Material.BELL, VillageHeart::new),
    ENDER_EYE("ender_eye", "Oeil de l'Ender", Material.ENDER_PEARL, EnderEye::new);

    public final String id;
    public final String name;
    public final Material material;
    public final Supplier<IRaceItem> factory;

    RaceItem(String id, String name, Material material, Supplier<IRaceItem> factory) {
        this.id = id;
        this.name = name;
        this.material = material;
        this.factory = factory;
    }

}
