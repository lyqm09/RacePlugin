package be.lymaes.race.util;

import be.lymaes.race.structure.Structure;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.util.List;

@FunctionalInterface
public interface StructureLoader {
    List<Structure> load(ObjectMapper mapper, File file) throws IOException;
}
