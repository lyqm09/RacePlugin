package be.lymaes.race.manager;

import be.lymaes.race.Race;
import be.lymaes.race.structure.Structure;
import be.lymaes.race.structure.StructureType;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.bukkit.Bukkit;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class StructureManager {

    private static final String PATH = "/structs/";
    private final Path dataFolder;

    private final Race plugin;

    private Map<UUID, Structure> register = new HashMap<>();
    private Map<StructureType, List<Structure>> structures = new HashMap<>();

    public StructureManager(Race plugin) {
        this.plugin = plugin;
        this.dataFolder = Paths.get(plugin.getDataFolder() + PATH);

        load();
    }

    public void terminate() {
        unload();

        structures.clear();
        register.clear();
    }

    @SuppressWarnings("unchecked")
    public <T extends Structure> List<T> getStructures(StructureType type) {
        return (List<T>) structures.getOrDefault(type, Collections.emptyList());
    }

    public List<Structure> getStructures() {
        return register.values().stream().toList();
    }

    public void addStructure(Structure structure) {
        register.put(structure.getUuid(), structure);
        structures.computeIfAbsent(structure.getType(), k -> new ArrayList<>()).add(structure);
    }

    public void removeStructure(Structure structure) {
        register.remove(structure.getUuid());
        List<Structure> structs = structures.get(structure.getType());
        if(structs == null || structs.isEmpty()) return;
        structs.remove(structure);
    }

    public void load() {
        if(Race.getInstance().isEnabled()) {
            Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> loadStruct(StructureType.KITSUNE_VILLAGE));
        } else {
            loadStruct(StructureType.KITSUNE_VILLAGE);
        }
    }

    public void loadStruct(StructureType type) {
        Path file = Paths.get(dataFolder + type.name().toLowerCase() + ".json");
        if (!Files.exists(file)) return;

        ObjectMapper mapper = Race.MAPPER;

        try {
            List<Structure> loadedList = type.loader.load(mapper, file.toFile());

            for (Structure structure : loadedList) {
                register.put(structure.getUuid(), structure);
            }
            structures.put(type, loadedList);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void unload() {
        if(Race.getInstance().isEnabled()) {
            Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> unloadStruct(StructureType.KITSUNE_VILLAGE));
        } else {
            unloadStruct(StructureType.KITSUNE_VILLAGE);
        }
    }

    public void unloadStruct(StructureType type) {
        String name = type.name().toLowerCase();
        File file = new File(dataFolder.toFile(), name + ".json");
        if(!file.exists()) file.mkdirs();

        try {
            Race.MAPPER.writerWithDefaultPrettyPrinter().writeValue(file, register);
        } catch (IOException e) {
            plugin.getLogger().severe("Erreur lors de la sauvegarde du fichier : " + name);
            e.printStackTrace();
        }
    }

}
