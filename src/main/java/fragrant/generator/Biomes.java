package fragrant.generator;

import nl.jellejurre.biomesampler.minecraft.Biome;
import java.util.*;
import static java.util.stream.Stream.*;

public class Biomes {
    private static final List<Biome>
            BASE_VILLAGE = List.of(Biome.PLAINS, Biome.MEADOW, Biome.SUNFLOWER_PLAINS, Biome.DESERT, Biome.SAVANNA, Biome.TAIGA, Biome.SNOWY_TAIGA, Biome.SNOWY_PLAINS),
            OCEANS = List.of(Biome.OCEAN, Biome.COLD_OCEAN, Biome.FROZEN_OCEAN, Biome.LUKEWARM_OCEAN, Biome.WARM_OCEAN),
            DEEP_OCEANS = List.of(Biome.DEEP_OCEAN, Biome.DEEP_COLD_OCEAN, Biome.DEEP_FROZEN_OCEAN, Biome.DEEP_LUKEWARM_OCEAN),
            BEACHES = List.of(Biome.BEACH, Biome.SNOWY_BEACH, Biome.STONY_SHORE);

    private static final Map<StructureType, List<Biome>> STRUCTURES = initMap();

    private static Map<StructureType, List<Biome>> initMap() {
        var map = new HashMap<StructureType, List<Biome>>();
        map.put(StructureType.ANCIENT_CITY,      List.of(Biome.DEEP_DARK));
        map.put(StructureType.WOODLAND_MANSION,  List.of(Biome.DARK_FOREST));
        map.put(StructureType.DESERT_TEMPLE,     List.of(Biome.DESERT));
        map.put(StructureType.JUNGLE_TEMPLE,     List.of(Biome.JUNGLE));
        map.put(StructureType.WITCH_HUT,         List.of(Biome.SWAMP));
        map.put(StructureType.IGLOO,             List.of(Biome.SNOWY_PLAINS, Biome.SNOWY_TAIGA, Biome.SNOWY_SLOPES));
        map.put(StructureType.BASTION_REMNANT,   List.of(Biome.CRIMSON_FOREST, Biome.NETHER_WASTES, Biome.SOUL_SAND_VALLEY, Biome.WARPED_FOREST));
        map.put(StructureType.TRAIL_RUINS,       merge(Biome.TAIGA, Biome.SNOWY_TAIGA, Biome.OLD_GROWTH_PINE_TAIGA, Biome.OLD_GROWTH_SPRUCE_TAIGA, Biome.OLD_GROWTH_BIRCH_FOREST, Biome.JUNGLE));
        map.put(StructureType.BURIED_TREASURE,   combine(BEACHES, List.of(Biome.STONY_SHORE)));
        map.put(StructureType.PILLAGER_OUTPOST,  combine(BASE_VILLAGE, List.of(Biome.FROZEN_PEAKS, Biome.SNOWY_SLOPES, Biome.CHERRY_GROVE)));
        map.put(StructureType.SHIPWRECK,         combine(BEACHES, OCEANS, DEEP_OCEANS));
        map.put(StructureType.OCEAN_RUIN,        combine(OCEANS, DEEP_OCEANS));
        map.put(StructureType.VILLAGE,           BASE_VILLAGE);
        map.put(StructureType.STRONGHOLD,        BASE_VILLAGE);
        map.put(StructureType.OCEAN_MONUMENT,    DEEP_OCEANS);
        return map;
    }

    @SafeVarargs
    private static List<Biome> combine(List<Biome>... lists) {
        return of(lists).flatMap(List::stream).distinct().toList();
    }

    private static List<Biome> merge(Biome... biomes) {
        return List.of(biomes);
    }

    public static List<Biome> forStructure(StructureType t) {
        return STRUCTURES.get(t);
    }

    public static int getY(StructureType t) {
        return t == StructureType.ANCIENT_CITY ? -51 : 64;
    }
}
