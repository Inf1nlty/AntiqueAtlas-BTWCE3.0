package hunternif.atlas.marker;

import hunternif.atlas.network.AntiqueAtlasNetwork;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.EntityPlayerMP;
import net.minecraft.src.NBTTagCompound;
import net.minecraft.src.NBTTagList;
import net.minecraft.src.WorldSavedData;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Stores marker data for an atlas.
 * Handles marker creation, removal, and synchronization with players.
 */
public class MarkersData extends WorldSavedData {
    private static final int VERSION = 3;
    private static final String TAG_VERSION = "aaVersion";
    private static final String TAG_DIMENSION_MAP_LIST = "dimMap";
    private static final String TAG_DIMENSION_ID = "dimID";
    private static final String TAG_MARKERS = "markers";
    private static final String TAG_MARKER_ID = "id";
    private static final String TAG_MARKER_TYPE = "markerType";
    private static final String TAG_MARKER_LABEL = "label";
    private static final String TAG_MARKER_X = "x";
    private static final String TAG_MARKER_Y = "y";
    private static final String TAG_MARKER_VISIBLE_AHEAD = "visAh";

    public static final int CHUNK_STEP = 8;

    private final Set<EntityPlayer> playersSentTo = new HashSet<EntityPlayer>();
    private final AtomicInteger largestID = new AtomicInteger(0);
    private final Map<Integer, Marker> idMap = new ConcurrentHashMap<Integer, Marker>(2, 0.75F, 2);
    private final Map<Integer, DimensionMarkersData> dimensionMap = new ConcurrentHashMap<Integer, DimensionMarkersData>(2, 0.75F, 2);

    /**
     * Generates a new unique marker ID.
     *
     * @return A new marker ID
     */
    protected int getNewID() {
        return this.largestID.incrementAndGet();
    }

    public MarkersData(String key) {
        super(key);
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        int version = compound.getInteger(TAG_VERSION);
        if (version < VERSION) {
            this.markDirty();
        }

        NBTTagList dimensionMapList = compound.getTagList(TAG_DIMENSION_MAP_LIST);

        for (int d = 0; d < dimensionMapList.tagCount(); ++d) {
            NBTTagCompound tag = (NBTTagCompound) dimensionMapList.tagAt(d);
            int dimensionID = tag.getInteger(TAG_DIMENSION_ID);
            NBTTagList markersList = tag.getTagList(TAG_MARKERS);

            for (int i = 0; i < markersList.tagCount(); ++i) {
                NBTTagCompound markerTag = (NBTTagCompound) markersList.tagAt(i);

                // Handle version migration
                boolean visibleAhead = version >= 2 ? markerTag.getBoolean(TAG_MARKER_VISIBLE_AHEAD) : true;

                int id;
                if (version < 3) {
                    id = this.getNewID();
                } else {
                    id = markerTag.getInteger(TAG_MARKER_ID);
                    if (this.getMarkerByID(id) != null) {
                        id = this.getNewID();
                    }
                }

                if (this.largestID.get() < id) {
                    this.largestID.set(id);
                }

                Marker marker = new Marker(
                        id,
                        markerTag.getString(TAG_MARKER_TYPE),
                        markerTag.getString(TAG_MARKER_LABEL),
                        dimensionID,
                        markerTag.getInteger(TAG_MARKER_X),
                        markerTag.getInteger(TAG_MARKER_Y),
                        visibleAhead
                );
                this.loadMarker(marker);
            }
        }
    }

    @Override
    public void writeToNBT(NBTTagCompound compound) {
        // Build a dimension -> marker list map from the authoritative idMap
        Map<Integer, List<Marker>> grouped = new HashMap<>();
        for (Marker marker : idMap.values()) {
            grouped.computeIfAbsent(marker.getDimension(), k -> new ArrayList<>()).add(marker);
        }

        compound.setInteger(TAG_VERSION, VERSION);
        NBTTagList dimensionMapList = new NBTTagList();

        for (Map.Entry<Integer, List<Marker>> e : grouped.entrySet()) {
            Integer dimension = e.getKey();
            List<Marker> markers = e.getValue();

            NBTTagCompound tag = new NBTTagCompound();
            tag.setInteger(TAG_DIMENSION_ID, dimension);
            NBTTagList markersList = new NBTTagList();

            for (Marker marker : markers) {
                NBTTagCompound markerTag = new NBTTagCompound();
                markerTag.setInteger(TAG_MARKER_ID, marker.getId());
                markerTag.setString(TAG_MARKER_TYPE, marker.getType());
                markerTag.setString(TAG_MARKER_LABEL, marker.getLabel());
                markerTag.setInteger(TAG_MARKER_X, marker.getX());
                markerTag.setInteger(TAG_MARKER_Y, marker.getZ());
                markerTag.setBoolean(TAG_MARKER_VISIBLE_AHEAD, marker.isVisibleAhead());
                markersList.appendTag(markerTag);
            }

            tag.setTag(TAG_MARKERS, markersList);
            dimensionMapList.appendTag(tag);
        }

        compound.setTag(TAG_DIMENSION_MAP_LIST, dimensionMapList);
    }

    /**
     * Gets all dimensions that have markers.
     *
     * @return Set of dimension IDs
     */
    public Set<Integer> getVisitedDimensions() {
        return this.dimensionMap.keySet();
    }

    /**
     * Gets all markers in the specified dimension.
     *
     * @param dimension The dimension ID
     * @return List of markers
     */
    public List<Marker> getMarkersInDimension(int dimension) {
        return this.getMarkersDataInDimension(dimension).getAllMarkersList();
    }

    /**
     * Gets or creates dimension markers data for the specified dimension.
     *
     * @param dimension The dimension ID
     * @return The dimension markers data
     */
    public DimensionMarkersData getMarkersDataInDimension(int dimension) {
        DimensionMarkersData data = this.dimensionMap.get(dimension);
        if (data == null) {
            data = new DimensionMarkersData(this, dimension);
            this.dimensionMap.put(dimension, data);
        }
        return data;
    }

    /**
     * Gets all markers at the specified chunk coordinates.
     *
     * @param dimension The dimension ID
     * @param x The chunk X coordinate
     * @param z The chunk Z coordinate
     * @return List of markers at the chunk
     */
    public List<Marker> getMarkersAtChunk(int dimension, int x, int z) {
        return this.getMarkersDataInDimension(dimension).getMarkersAtChunk(x, z);
    }

    /**
     * Gets a marker by its ID.
     *
     * @param id The marker ID
     * @return The marker, or null if not found
     */
    public Marker getMarkerByID(int id) {
        return this.idMap.get(id);
    }

    /**
     * Removes a marker by its ID.
     *
     * @param id The marker ID to remove
     * @return The removed marker, or null if not found
     */
    public Marker removeMarker(int id) {
        Marker marker = this.getMarkerByID(id);
        if (marker == null) {
            return null;
        }

        // Remove from authoritative idMap first
        Marker removed = this.idMap.remove(id);
        if (removed != null) {
            this.getMarkersDataInDimension(marker.getDimension()).removeMarker(marker);
            this.markDirty();
        }

        return removed;
    }

    /**
     * Creates a new marker and saves it.
     *
     * @param type The marker type
     * @param label The marker label
     * @param dimension The dimension ID
     * @param x The X coordinate
     * @param z The Z coordinate
     * @param visibleAhead Whether the marker is visible when zoomed out
     * @return The created marker
     */
    public Marker createAndSaveMarker(String type, String label, int dimension, int x, int z, boolean visibleAhead) {
        Marker marker = new Marker(this.getNewID(), type, label, dimension, x, z, visibleAhead);

        this.idMap.put(marker.getId(), marker);
        this.getMarkersDataInDimension(marker.getDimension()).insertMarker(marker);
        this.markDirty();

        return marker;
    }

    /**
     * Loads an existing marker into the data structure.
     *
     * @param marker The marker to load
     * @return The loaded marker
     */
    public Marker loadMarker(Marker marker) {
        if (!this.idMap.containsKey(marker.getId())) {
            this.idMap.put(marker.getId(), marker);
            this.getMarkersDataInDimension(marker.getDimension()).insertMarker(marker);
        }
        return marker;
    }

    /**
     * Checks if this markers data has been synced to the specified player.
     *
     * @param player The player to check
     * @return true if synced, false otherwise
     */
    public boolean isSyncedOnPlayer(EntityPlayer player) {
        return this.playersSentTo.contains(player);
    }

    /**
     * Synchronizes this markers data to the specified player.
     *
     * @param atlasID The atlas ID
     * @param player The player to sync to
     */
    public void syncOnPlayer(int atlasID, EntityPlayer player) {
        if (!(player instanceof EntityPlayerMP)) {
            return;
        }

        EntityPlayerMP playerMP = (EntityPlayerMP) player;

        for (Integer dimension : this.dimensionMap.keySet()) {
            DimensionMarkersData data = this.getMarkersDataInDimension(dimension);
            List<Marker> markers = data.getAllMarkersList();

            if (!markers.isEmpty()) {
                Marker[] markerArray = markers.toArray(new Marker[0]);
                AntiqueAtlasNetwork.sendMarkersToPlayer(playerMP, atlasID, markerArray);
            }
        }

        this.playersSentTo.add(player);
    }

    /**
     * Checks if this markers data is empty.
     *
     * @return true if empty, false otherwise
     */
    public boolean isEmpty() {
        return this.idMap.isEmpty();
    }
}