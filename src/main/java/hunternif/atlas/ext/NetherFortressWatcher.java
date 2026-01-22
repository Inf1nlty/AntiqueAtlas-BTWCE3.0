package hunternif.atlas.ext;

import hunternif.atlas.AntiqueAtlasMod;
import hunternif.atlas.api.AtlasAPI;
import hunternif.atlas.util.Log;
import net.minecraft.src.*;

import java.util.HashSet;
import java.util.Set;

public class NetherFortressWatcher {
    private final Set<String> visited = new HashSet<>();

    private static final String ROOFED = "NeSCLT";
    private static final String ROOFED2 = "NeSCR";
    private static final String ROOFED_STAIRS = "NeCCS";
    private static final String ROOFED3 = "NeCTB";
    private static final String ROOFED4 = "NeSC";

    private static final String BRIDGE_GATE = "NeRC";
    private static final String ROOFED_CROSS = "NeSCSC";
    private static final String BRIDGE_CROSS = "NeBCr";
    private static final String START = "NeStart";

    private static final String BRIDGE = "NeBS";
    private static final String BRIDGE_END = "NeBEF";

    private static final String ENTRANCE = "NeCE";
    private static final String WART_STAIRS = "NeCSR";
    private static final String THRONE = "NeMT";
    private static final String TOWER = "NeSR";

    public void onWorldLoad(World world) {
        if (!world.isRemote && world.provider.dimensionId == -1) {
            visitAllUnvisitedFortresses(world);
        }
    }

    public void onPopulateChunk(World world) {
        if (!world.isRemote && world.provider.dimensionId == -1) {
            visitAllUnvisitedFortresses(world);
        }
    }

    public void visitAllUnvisitedFortresses(World world) {
        MapGenStructureData data = (MapGenStructureData) world.loadItemData(MapGenStructureData.class, "Fortress");
        if (data == null) return;

        NBTTagCompound fortressNBTData = data.func_143041_a();

        Set<String> tagSet = new HashSet<>();
        for (Object key : fortressNBTData.getTags()) {
            if (key instanceof NBTBase) {
                tagSet.add(((NBTBase) key).getName());
            }
        }

        for (String coords : tagSet) {
            if (!visited.contains(coords)) {
                NBTBase tag = fortressNBTData.getTag(coords);
                if (tag instanceof NBTTagCompound) {
                    visitFortress(world, (NBTTagCompound) tag);
                    visited.add(coords);
                }
            }
        }
    }

    private void visitFortress(World world, NBTTagCompound tag) {
        int startChunkX = tag.getInteger("ChunkX");
        int startChunkZ = tag.getInteger("ChunkZ");

        Log.info("Visiting Nether Fortress in dimension #%d at chunk (%d, %d) ~ blocks (%d, %d)",
                world.provider.dimensionId, startChunkX, startChunkZ, startChunkX << 4, startChunkZ << 4);

        // 1.6.4 的 getTagList 只需要一个参数
        NBTTagList children = tag.getTagList("Children");
        for (int i = 0; i < children.tagCount(); i++) {
            // 使用 tagAt 替代 getCompoundTagAt
            NBTBase childBase = children.tagAt(i);
            if (!(childBase instanceof NBTTagCompound)) continue;

            NBTTagCompound child = (NBTTagCompound) childBase;
            String childID = child.getString("id");
            int[] bbArray = child.getIntArray("BB");
            if (bbArray.length < 6) continue;

            int minX = bbArray[0], minZ = bbArray[2];
            int maxX = bbArray[3], maxZ = bbArray[5];

            if (BRIDGE.equals(childID)) {
                handleBridge(world, minX, minZ, maxX, maxZ);
            } else if (BRIDGE_END.equals(childID)) {
                handleBridgeEnd(world, minX, minZ, maxX, maxZ);
            } else {
                handleOtherStructures(world, childID, minX, minZ, maxX, maxZ);
            }
        }
    }

    private void handleBridge(World world, int minX, int minZ, int maxX, int maxZ) {
        int xSize = maxX - minX;
        int zSize = maxZ - minZ;

        if (xSize > 16) {
            String tileName = ExtTileIdMap.TILE_NETHER_BRIDGE_X;
            int chunkZ = (minZ + maxZ) / 2 >> 4;
            for (int x = minX; x < maxX; x += 16) {
                int chunkX = x >> 4;
                if (noTileAt(world, chunkX, chunkZ)) {
                    AtlasAPI.getTileAPI().putCustomGlobalTile(world, tileName, chunkX, chunkZ);
                }
            }
        } else {
            String tileName = ExtTileIdMap.TILE_NETHER_BRIDGE_Z;
            int chunkX = (minX + maxX) / 2 >> 4;
            for (int z = minZ; z < maxZ; z += 16) {
                int chunkZ = z >> 4;
                if (noTileAt(world, chunkX, chunkZ)) {
                    AtlasAPI.getTileAPI().putCustomGlobalTile(world, tileName, chunkX, chunkZ);
                }
            }
        }
    }

    private void handleBridgeEnd(World world, int minX, int minZ, int maxX, int maxZ) {
        int xSize = maxX - minX;
        int zSize = maxZ - minZ;
        String tileName;
        int chunkX, chunkZ;

        if (xSize > zSize) {
            tileName = ExtTileIdMap.TILE_NETHER_BRIDGE_END_X;
            chunkX = minX >> 4;
            chunkZ = (minZ + maxZ) / 2 >> 4;
        } else {
            tileName = ExtTileIdMap.TILE_NETHER_BRIDGE_END_Z;
            chunkX = (minX + maxX) / 2 >> 4;
            chunkZ = minZ >> 4;
        }

        if (noTileAt(world, chunkX, chunkZ)) {
            AtlasAPI.getTileAPI().putCustomGlobalTile(world, tileName, chunkX, chunkZ);
        }
    }

    private void handleOtherStructures(World world, String childID, int minX, int minZ, int maxX, int maxZ) {
        int chunkX = (minX + maxX) / 2 >> 4;
        int chunkZ = (minZ + maxZ) / 2 >> 4;
        String tileName = null;

        if (BRIDGE_GATE.equals(childID)) {
            tileName = ExtTileIdMap.TILE_NETHER_BRIDGE_GATE;
        } else if (BRIDGE_CROSS.equals(childID) || START.equals(childID)) {
            tileName = ExtTileIdMap.TILE_NETHER_BRIDGE;
        } else if (TOWER.equals(childID)) {
            tileName = ExtTileIdMap.TILE_NETHER_TOWER;
        } else if (ENTRANCE.equals(childID)) {
            tileName = ExtTileIdMap.TILE_NETHER_HALL;
        } else if (WART_STAIRS.equals(childID)) {
            tileName = ExtTileIdMap.TILE_NETHER_FORT_STAIRS;
        } else if (THRONE.equals(childID)) {
            tileName = ExtTileIdMap.TILE_NETHER_THRONE;
        } else {
            tileName = ExtTileIdMap.TILE_NETHER_WALL;
            if (!noTileAt(world, chunkX, chunkZ)) return;
        }

        AtlasAPI.getTileAPI().putCustomGlobalTile(world, tileName, chunkX, chunkZ);
    }

    private static boolean noTileAt(World world, int chunkX, int chunkZ) {
        return AntiqueAtlasMod.extBiomeData.getData().getBiomeIdAt(world.provider.dimensionId, chunkX, chunkZ) == -1;
    }
}
