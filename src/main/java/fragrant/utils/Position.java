package fragrant.utils;
import fragrant.generator.StructureType;

public class Position {
    private static final String
            CHUNK_FMT = "ChunkPos{x=%d, z=%d}",
            BLOCK_FMT = "BlockPos{x=%d, z=%d}",
            BLOCK3D_FMT = "Block3DPos{x=%d, y=%d, z=%d}";

    public record ChunkPos(int x, int z) {
        public BlockPos toBlock() {
            return new BlockPos(x * 16 + 8, z * 16 + 8);
        }
        public double dist(ChunkPos o) {
            return Math.hypot(x - o.x, z - o.z);
        }
        public boolean inRadius(ChunkPos c, int r) {
            return Math.abs(x - c.x) <= r && Math.abs(z - c.z) <= r;
        }
        public ChunkPos add(int dx, int dz) {
            return new ChunkPos(x + dx, z + dz);
        }
        @Override public String toString() {
            return CHUNK_FMT.formatted(x, z);
        }
    }

    public record BlockPos(int x, int z) {
        public ChunkPos toChunk() {
            return new ChunkPos(x >> 4, z >> 4);
        }
        public Block3DPos withY(int y) {
            return new Block3DPos(x, y, z);
        }
        @Override public String toString() {
            return BLOCK_FMT.formatted(x, z);
        }
    }

    public record Block3DPos(int x, int y, int z) {
        public ChunkPos toChunk() {
            return new ChunkPos(x >> 4, z >> 4);
        }
        public BlockPos to2D() {
            return new BlockPos(x, z);
        }
        @Override public String toString() {
            return BLOCK3D_FMT.formatted(x, y, z);
        }
    }

    public static BlockPos centerOf(ChunkPos cp, StructureType t) {
        int o = switch(t) {
            case TRIAL_CHAMBERS, TRAIL_RUINS, BASTION_REMNANT -> 0;
            case NETHER_FORTRESS -> 11;
            case STRONGHOLD -> 4;
            default -> 8;
        };
        return new BlockPos(cp.x() * 16 + o, cp.z() * 16 + o);
    }
}
