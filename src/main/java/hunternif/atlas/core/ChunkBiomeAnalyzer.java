package hunternif.atlas.core;

import hunternif.atlas.util.ByteUtil;
import net.minecraft.src. BiomeGenBase;
import net.minecraft. src.Block;
import net.minecraft.src. Chunk;

/**
 * Analyzes chunks to determine the dominant biome.
 * Used for atlas tile generation.
 */
public class ChunkBiomeAnalyzer {
    public static final int NOT_FOUND = -1;
    public static final ChunkBiomeAnalyzer instance = new ChunkBiomeAnalyzer();

    private static final int waterPoolBiomeID;
    private static final int waterPoolMultiplier = 2;
    private static final int waterMultiplier = 4;
    private static final int beachMultiplier = 3;

    static {
        waterPoolBiomeID = BiomeGenBase.river.biomeID;
    }

    /**
     * Analyzes a chunk to determine the most prominent biome.
     * Takes into account special cases like water pools and beaches.
     *
     * @param chunk The chunk to analyze
     * @return The biome ID, or NOT_FOUND if unable to determine
     */
    public int getMeanBiomeID(Chunk chunk) {
        BiomeGenBase[] biomes = BiomeGenBase. biomeList;
        int[] chunkBiomes = ByteUtil.unsignedByteToIntArray(chunk.getBiomeArray());
        int[] biomeOccurrences = new int[biomes.length];

        for (int x = 0; x < 16; ++x) {
            for (int z = 0; z < 16; ++z) {
                int biomeID = chunkBiomes[x << 4 | z];
                int y = chunk.getHeightValue(x, z);

                // Get block at top of column
                int blockID = chunk.getBlockID(x, y - 1, z);

                // Check if it's a water pool (water on top but not in swamp)
                if (blockID == Block.waterStill. blockID &&
                        biomeID != BiomeGenBase.swampland.biomeID &&
                        biomeID != BiomeGenBase.swampland.biomeID + 128) {
                    biomeOccurrences[waterPoolBiomeID] += waterPoolMultiplier;
                }

                // Count biome occurrences with multipliers
                if (biomeID >= 0 && biomeID < biomes. length && biomes[biomeID] != null) {
                    BiomeGenBase biome = biomes[biomeID];

                    if (biome. equals(BiomeGenBase.river) || biome.equals(BiomeGenBase.ocean)) {
                        biomeOccurrences[biomeID] += waterMultiplier;
                    } else if (biome.equals(BiomeGenBase.beach)) {
                        biomeOccurrences[biomeID] += beachMultiplier;
                    } else {
                        biomeOccurrences[biomeID]++;
                    }
                }
            }
        }

        // Find biome with highest occurrence count
        int meanBiomeId = NOT_FOUND;
        int meanBiomeOccurrences = 0;

        for (int i = 0; i < biomeOccurrences.length; ++i) {
            if (biomeOccurrences[i] > meanBiomeOccurrences) {
                meanBiomeId = i;
                meanBiomeOccurrences = biomeOccurrences[i];
            }
        }

        return meanBiomeId;
    }
}