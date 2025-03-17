package fragrant;

import fragrant.generator.StructureGen;
import fragrant.generator.StructureType;
import fragrant.utils.Position;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Structures {
    public static void main(String[] args) {
        long worldSeed = 12345L;
        int centerX = 0;
        int centerZ = 0;
        int radius = 64;

        StructureType[] targetTypes = {
                StructureType.VILLAGE,
                StructureType.STRONGHOLD
        };

        Map<StructureType, List<Position.ChunkPos>> structureLocations = new HashMap<>();

        for (StructureType type : targetTypes) {
            structureLocations.put(type, new ArrayList<>());
        }

        for (int x = centerX - radius; x <= centerX + radius; x++) {
            for (int z = centerZ - radius; z <= centerZ + radius; z++) {
                Position.ChunkPos pos = new Position.ChunkPos(x, z);

                for (StructureType type : targetTypes) {
                    if (StructureGen.isStructureChunk(worldSeed, type, pos, false)) {
                        structureLocations.get(type).add(pos);
                    }
                }
            }
        }

        for (StructureType type : targetTypes) {
            List<Position.ChunkPos> locations = structureLocations.get(type);
            if (!locations.isEmpty()) {
                System.out.println(type);
                for (Position.ChunkPos pos : locations) {
                    Position.BlockPos blockPos = Position.centerOf(pos, type);
                    System.out.println(pos + " " + blockPos);
                }
                System.out.println();
            }
        }
    }
}