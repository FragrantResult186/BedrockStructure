package fragrant;

import fragrant.generator.StructureGen;
import fragrant.generator.StructureType;
import fragrant.utils.Position;

public class BastionOrFortress {
    public static void main(String[] args) {
        long worldSeed = -3836128199501391923L;
        int X = 10;
        int Z = 20;

        boolean isFortress = StructureGen.isStructureChunk(worldSeed, StructureType.NETHER_FORTRESS, new Position.ChunkPos(X, Z), false);
        System.out.println("Nether Fortress: " + (isFortress ? "FOUND" : "NOT FOUND"));

        boolean isBastion = StructureGen.isStructureChunk(worldSeed, StructureType.BASTION_REMNANT, new Position.ChunkPos(X, Z), true);
        System.out.println("Bastion: " + (isBastion ? "FOUND" : "NOT FOUND"));
    }
}