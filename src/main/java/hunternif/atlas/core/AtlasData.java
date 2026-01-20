package hunternif.atlas.core;

import hunternif.atlas.network.MapDataPacket;
import hunternif.atlas.util.Log;
import hunternif.atlas.util.ShortVec2;
import net.minecraft.src.*;

import java.util.HashMap;
import java.util.HashSet;
import java.util. Iterator;
import java.util.Map;
import java.util.Set;

/**
 * Stores discovered tile data for an atlas.
 * Handles serialization, synchronization, and per-dimension tile storage.
 */
public class AtlasData extends WorldSavedData {
    private static final int VERSION = 1;
    private static final String TAG_VERSION = "aaVersion";
    private static final String TAG_DIMENSION_MAP_LIST = "qDimensionMap";
    private static final String TAG_DIMENSION_ID = "qDimensionID";
    private static final String TAG_VISITED_CHUNKS = "qVisitedChunks";

    private final Map<Integer, DimensionData> dimensionMap = new HashMap<Integer, DimensionData>();
    private final Set<NetServerHandler> playersSentTo = new HashSet<NetServerHandler>();
    private final NBTTagCompound nbtCache = new NBTTagCompound();
    private byte[] rawData;

    public AtlasData(String key) {
        super(key);
    }

    /**
     * Reads atlas data from a network packet.
     *
     * @param pkt The packet containing compressed NBT data
     */
    public void readFromPacket(MapDataPacket pkt) {
        NBTTagCompound nbt = CompressedStreamTools.decompress(pkt.data);
        this.readFromNBT(nbt);
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        int version = compound.getInteger(TAG_VERSION);
        if (version < VERSION) {
            Log.warn("Outdated atlas data format!  Was {} but current is {}", version, VERSION);
            this.markDirty();
        }

        NBTTagList dimensionMapList = compound.getTagList(TAG_DIMENSION_MAP_LIST);

        for (int d = 0; d < dimensionMapList.tagCount(); ++d) {
            NBTTagCompound tag = (NBTTagCompound) dimensionMapList.tagAt(d);
            int dimensionID = tag.getInteger(TAG_DIMENSION_ID);
            int[] intArray = tag.getIntArray(TAG_VISITED_CHUNKS);

            for (int i = 0; i < intArray.length; i += 3) {
                int x = intArray[i];
                int z = intArray[i + 1];
                int biomeID = intArray[i + 2];
                this.setTile(dimensionID, x, z, new Tile(biomeID));
            }
        }
    }

    @Override
    public void writeToNBT(NBTTagCompound compound) {
        compound.setInteger(TAG_VERSION, VERSION);
        NBTTagList dimensionMapList = new NBTTagList();

        for (Map.Entry<Integer, DimensionData> dimensionEntry : this.dimensionMap.entrySet()) {
            NBTTagCompound tag = new NBTTagCompound();
            tag.setInteger(TAG_DIMENSION_ID, dimensionEntry.getKey());

            Map<ShortVec2, Tile> seenChunks = dimensionEntry.getValue().getSeenChunks();
            int[] intArray = new int[seenChunks.size() * 3];
            int i = 0;

            for (Map.Entry<ShortVec2, Tile> entry : seenChunks.entrySet()) {
                intArray[i++] = entry. getKey().x;
                intArray[i++] = entry. getKey().y;
                intArray[i++] = entry.getValue().biomeID;
            }

            tag.setIntArray(TAG_VISITED_CHUNKS, intArray);
            dimensionMapList.appendTag(tag);
        }

        compound.setTag(TAG_DIMENSION_MAP_LIST, dimensionMapList);
    }

    /**
     * Sets a tile in the atlas at the specified dimension and coordinates.
     *
     * @param dimension The dimension ID
     * @param x The chunk X coordinate
     * @param z The chunk Z coordinate
     * @param tile The tile to set
     */
    public void setTile(int dimension, int x, int z, Tile tile) {
        DimensionData dimData = this. getDimensionData(dimension);
        dimData.setTile(x, z, tile);
        this.markDirty();
        this.rawData = null;
    }

    /**
     * Gets the set of all dimensions visited in this atlas.
     *
     * @return Set of dimension IDs
     */
    public Set<Integer> getVisitedDimensions() {
        return this.dimensionMap.keySet();
    }

    /**
     * Gets or creates dimension data for the specified dimension.
     *
     * @param dimension The dimension ID
     * @return The dimension data
     */
    public DimensionData getDimensionData(int dimension) {
        DimensionData dimData = this.dimensionMap.get(dimension);
        if (dimData == null) {
            dimData = new DimensionData(dimension);
            this.dimensionMap.put(dimension, dimData);
        }
        return dimData;
    }

    /**
     * Gets all seen chunks in the specified dimension.
     *
     * @param dimension The dimension ID
     * @return Map of chunk coordinates to tiles
     */
    public Map<ShortVec2, Tile> getSeenChunksInDimension(int dimension) {
        return this.getDimensionData(dimension).getSeenChunks();
    }

    /**
     * Checks if this atlas data has been synced to the specified player.
     *
     * @param player The player to check
     * @return true if synced, false otherwise
     */
    public boolean isSyncedOnPlayer(EntityPlayer player) {
        if (!(player instanceof EntityPlayerMP)) {
            Log.warn("Client-side player cannot be synced");
            return true;
        }

        EntityPlayerMP playerMP = (EntityPlayerMP) player;
        return this.playersSentTo.contains(playerMP. playerNetServerHandler);
    }

    /**
     * Synchronizes this atlas data to the specified player.
     *
     * @param atlasID The atlas ID
     * @param player The player to sync to
     */
    public void syncOnPlayer(int atlasID, EntityPlayer player) {
        if (!(player instanceof EntityPlayerMP)) {
            Log.warn("Cannot sync atlas data to client-side player");
            return;
        }

        EntityPlayerMP playerMP = (EntityPlayerMP) player;

        // Send data packet
        MapDataPacket packet = new MapDataPacket(atlasID, this.getOrUpdateRawData());
        playerMP.playerNetServerHandler.sendPacketToPlayer(packet);

        // Track sync state
        cleanupClosedConnections();
        this.playersSentTo.add(playerMP.playerNetServerHandler);
    }

    /**
     * Sends a packet to all players who have this atlas synced.
     *
     * @param packet The packet to send
     */
    public void sendPacketToSyncedPlayers(Packet packet) {
        Iterator<NetServerHandler> it = this.playersSentTo. iterator();
        while (it. hasNext()) {
            NetServerHandler handler = it.next();
            if (handler.isConnectionClosed()) {
                it.remove();
            } else {
                handler.netManager.addToSendQueue(packet);
            }
        }
    }

    /**
     * Sends a packet to all synced players.  Typo-tolerant alias.
     *
     * @param packet The packet to send
     */
    public void sendPacketToSyncPlayer(Packet packet) {
        this.sendPacketToSyncedPlayers(packet);
    }

    /**
     * Removes closed connections from the synced players list.
     */
    private void cleanupClosedConnections() {
        Iterator<NetServerHandler> it = this.playersSentTo.iterator();
        while (it.hasNext()) {
            if (it.next().isConnectionClosed()) {
                it. remove();
            }
        }
    }

    /**
     * Gets or generates compressed NBT data for network transmission.
     *
     * @return Compressed NBT byte array
     */
    private byte[] getOrUpdateRawData() {
        if (this. rawData != null) {
            return this.rawData;
        }

        this.writeToNBT(this.nbtCache);
        this.rawData = CompressedStreamTools. compress(this.nbtCache);

        if (this.rawData.length >= 65535) {
            Log.error("Atlas data too large:  {} bytes", this.rawData.length);
        }

        return this.rawData;
    }

    /**
     * Checks if this atlas has no data.
     *
     * @return true if empty, false otherwise
     */
    public boolean isEmpty() {
        return this.dimensionMap.isEmpty();
    }
}