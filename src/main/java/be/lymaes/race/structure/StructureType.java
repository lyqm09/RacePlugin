package be.lymaes.race.structure;

import be.lymaes.race.util.StructureLoader;

import java.util.function.Function;

public enum StructureType {

    KITSUNE_VILLAGE(KitsuneVillage::load);

    public final StructureLoader loader;

    StructureType(StructureLoader loader) {
        this.loader = loader;
    }

}
