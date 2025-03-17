package fragrant.generator;

import fragrant.generator.random.MersenneTwister;
import fragrant.utils.Position;
import nl.jellejurre.biomesampler.BiomeSampler;
import nl.jellejurre.biomesampler.minecraft.Biome;
import nl.kallestruik.noisesampler.minecraft.Dimension;
import java.util.*;
import java.util.function.BiPredicate;

public class StructureGen {
    public record Config(int salt, int spacing, int sep, int n, BiPredicate<int[], StructureType> cond, List<Biome> biomes) {
        public Config(int salt, int spacing, int sep, int n) {
            this(salt, spacing, sep, n, (_, _) -> true, null);
        }
    }

    public static boolean isStructureChunk(long seed, StructureType type, Position.ChunkPos cp, boolean checkBio) {
        return isChunkValid(seed, type, cp) && (!checkBio || biomeValid(seed, type, cp));
    }

    private static boolean isChunkValid(long seed, StructureType t, Position.ChunkPos cp) {
        if (t == StructureType.STRONGHOLD) {
            return isStrongholdChunk(seed, cp);
        }

        Config c = configOf(t);
        int sp = c.spacing(), se = c.sep();

        if (t == StructureType.MINESHAFT)
            return isMineshaft(seed, cp);
        if (t == StructureType.END_CITY && cp.x()*cp.x() + cp.z()*cp.z() <= 3969)
            return false;

        int xm = Math.floorMod(cp.x(), sp), zm = Math.floorMod(cp.z(), sp);
        int[] nums = MersenneTwister.genNums(c.salt() + (int)seed - 245998635 * Math.floorDiv(cp.z(), sp) - 1724254968 * Math.floorDiv(cp.x(), sp), c.n());

        if (t == StructureType.BASTION_REMNANT || t == StructureType.NETHER_FORTRESS)
            return MersenneTwister.mod(nums[0], se) == xm && MersenneTwister.mod(nums[1], se) == zm && (t == StructureType.BASTION_REMNANT) == (MersenneTwister.mod(nums[2], 6) >= 2);
        if (t == StructureType.TRIAL_CHAMBERS || t == StructureType.TRAIL_RUINS)
            return isJavaStyle(seed, cp, c);

        return c.n() == 2 ?
                MersenneTwister.mod(nums[0], se) == xm && MersenneTwister.mod(nums[1], se) == zm :
               (MersenneTwister.mod(nums[0], se) + MersenneTwister.mod(nums[1], se))/2 == xm &&
               (MersenneTwister.mod(nums[2], se) + MersenneTwister.mod(nums[3], se))/2 == zm;
    }

    private static boolean biomeValid(long seed, StructureType t, Position.ChunkPos cp) {
        List<Biome> bios = Biomes.forStructure(t);
        if (bios == null) return true;

        Position.Block3DPos center = Position.centerOf(cp, t).withY(Biomes.getY(t));
        Dimension dim = switch(t) {
            case BASTION_REMNANT, NETHER_FORTRESS, RUINED_PORTAL_N -> Dimension.NETHER;
            case END_CITY                                          -> Dimension.THEEND;
            default                                                -> Dimension.OVERWORLD;
        };
        return bios.contains(new BiomeSampler(seed, dim).getBiomeFromBlockPos(center.x(), center.y(), center.z()));
    }

    private static boolean isJavaStyle(long seed, Position.ChunkPos cp, Config c) {
        int rx = Math.floorDiv(cp.x(), c.spacing()), rz = Math.floorDiv(cp.z(), c.spacing());
        long s = seed + rx * 341873128712L + rz * 132897987541L + c.salt();
        s = (s ^ 0x5DEECE66DL) & 0xFFFFFFFFFFFFL;
        s = (s * 0x5DEECE66DL + 0xB) & 0xFFFFFFFFFFFFL;

        int xo = (int)(s >>> 17) % c.sep();
        s = (s * 0x5DEECE66DL + 0xB) & 0xFFFFFFFFFFFFL;
        int zo = (int)(s >>> 17) % c.sep();

        return Math.floorMod(cp.x(), c.spacing()) == xo && Math.floorMod(cp.z(), c.spacing()) == zo;
    }

    public static boolean isMineshaft(long seed, Position.ChunkPos cp) {
        var mt = new MersenneTwister();
        mt.seed(seed, 2);
        mt.seed(((long) mt.nextUnbound() * cp.x() ^ (long) mt.nextUnbound() * cp.z() ^ seed), 3);
        return mt.nextFloat() < 0.004 && mt.nextInt(80) < Math.max(Math.abs(cp.x()), Math.abs(cp.z()));
    }

    private static boolean isStrongholdChunk(long seed, Position.ChunkPos cp) {
        int[] mt = MersenneTwister.genNums((int) seed, 2);
        double angle = 6.2831855 * MersenneTwister.int2Float(mt[0]);
        int chunkDist = MersenneTwister.mod(mt[1], 16) + 40;

        for (int i = 0; i < 3; i++) {
            int cx = (int) Math.floor(Math.cos(angle) * chunkDist);
            int cz = (int) Math.floor(Math.sin(angle) * chunkDist);

            for (int x = cx - 8; x < cx + 8; x++) {
                for (int z = cz - 8; z < cz + 8; z++) {
                    Position.ChunkPos candidatePos = new Position.ChunkPos(x, z);

                    if (isVillageChunk(seed, candidatePos) && candidatePos.equals(cp)) {
                        return true;
                    }
                }
            }

            angle += 1.8849558;
            chunkDist += 8;
        }

        return false;
    }

    private static boolean isVillageChunk(long seed, Position.ChunkPos cp) {
        Config c = configOf(StructureType.VILLAGE);
        int sp = c.spacing(), se = c.sep();
        int xm = Math.floorMod(cp.x(), sp), zm = Math.floorMod(cp.z(), sp);
        int[] nums = MersenneTwister.genNums(c.salt() + (int)seed - 245998635 * Math.floorDiv(cp.z(), sp) - 1724254968 * Math.floorDiv(cp.x(), sp), c.n());

        return (MersenneTwister.mod(nums[0], se) + MersenneTwister.mod(nums[1], se))/2 == xm &&
               (MersenneTwister.mod(nums[2], se) + MersenneTwister.mod(nums[3], se))/2 == zm;
    }

    private static Config configOf(StructureType type) {
        return switch(type) {
            case ANCIENT_CITY                     -> new Config( 20083232,24,16,4);
            case BASTION_REMNANT, NETHER_FORTRESS -> new Config( 30084232,30,26,4);
            case BURIED_TREASURE                  -> new Config( 16842397, 4, 2,4);
            case DESERT_TEMPLE                    -> new Config( 14357617,32,24,2);
            case END_CITY                         -> new Config( 10387313,20, 9,4);
            case MINESHAFT                        -> new Config(        0, 1, 1,0);
            case OCEAN_RUIN                       -> new Config( 14357621,20,12,2);
            case OCEAN_MONUMENT                   -> new Config( 10387313,32,27,4);
            case PILLAGER_OUTPOST                 -> new Config(165745296,80,56,4);
            case RUINED_PORTAL_O                  -> new Config( 40552231,40,25,2);
            case RUINED_PORTAL_N                  -> new Config( 40552231,25,15,2);
            case SHIPWRECK                        -> new Config(165745295,24,20,2);
            case STRONGHOLD                       -> new Config( 10387312, 0, 0,0);
            case TRAIL_RUINS                      -> new Config( 83469867,34,26,0);
            case TRIAL_CHAMBERS                   -> new Config( 94251327,34,22,0);
            case VILLAGE                          -> new Config( 10387312,34,26,4);
            case WOODLAND_MANSION                 -> new Config( 10387319,80,60,4);
            default                               -> new Config( 14357617,32,24,2); // その他の寺院タイプ
        };
    }
}
