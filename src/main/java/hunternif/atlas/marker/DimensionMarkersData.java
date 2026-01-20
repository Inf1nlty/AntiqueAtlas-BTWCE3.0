package hunternif.atlas.marker;

import hunternif.atlas.util.ListMapValueIterator;
import hunternif.atlas.util.ShortVec2;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Stores markers for a specific dimension.
 * Organizes markers by chunk for efficient spatial queries.
 */
public class DimensionMarkersData {
    public final MarkersData parent;
    public final int dimension;
    private int size = 0;
    private final Map<ShortVec2, List<Marker>> chunkMap = new ConcurrentHashMap<ShortVec2, List<Marker>>(2, 0.75F, 2);
    private final Values values = new Values();
    private final Map<Thread, ShortVec2> thread2KeyMap = new ConcurrentHashMap<Thread, ShortVec2>(2, 0.75F, 2);

    /**
     * Gets a reusable key object for the current thread.
     *
     * @return Thread-local key object
     */
    private ShortVec2 getKey() {
        ShortVec2 key = this.thread2KeyMap.get(Thread.currentThread());
        if (key == null) {
            key = new ShortVec2(0, 0);
            this.thread2KeyMap.put(Thread.currentThread(), key);
        }
        return key;
    }

    public DimensionMarkersData(MarkersData parent, int dimension) {
        this.parent = parent;
        this.dimension = dimension;
    }

    public int getDimension() {
        return this.dimension;
    }

    /**
     * Gets all markers at the specified chunk coordinates.
     *
     * @param x Chunk X coordinate
     * @param z Chunk Z coordinate
     * @return List of markers, or null if no markers exist at this chunk
     */
    public List<Marker> getMarkersAtChunk(int x, int z) {
        return this.chunkMap.get(this.getKey().set(x, z));
    }

    /**
     * Inserts a marker into this dimension's data.
     * Markers are sorted by Z coordinate within each chunk.
     *
     * @param marker The marker to insert
     */
    public void insertMarker(Marker marker) {
        ShortVec2 key = this.getKey().set(marker.getChunkX() / 8, marker.getChunkZ() / 8);
        List<Marker> list = this.chunkMap.get(key);

        if (list == null) {
            list = new CopyOnWriteArrayList<Marker>();
            this.chunkMap.put(key. clone(), list);
        }

        // Insert marker in Z-sorted order
        boolean inserted = false;
        for (int i = 0; i < list.size(); ++i) {
            if (list.get(i).getZ() > marker.getZ()) {
                list.add(i, marker);
                inserted = true;
                break;
            }
        }

        if (!inserted) {
            list.add(marker);
        }

        ++this.size;
        this.parent.markDirty();
    }

    /**
     * Removes a marker from this dimension's data.
     *
     * @param marker The marker to remove
     * @return true if removed, false if not found
     */
    public boolean removeMarker(Marker marker) {
        List<Marker> list = this. getMarkersAtChunk(marker. getChunkX() / 8, marker.getChunkZ() / 8);
        if (list != null && list.remove(marker)) {
            --this.size;
            return true;
        }
        return false;
    }

    /**
     * Gets all markers in this dimension as a list.
     * This is a replacement for the Stream API version.
     *
     * @return List of all markers
     */
    public List<Marker> getAllMarkersList() {
        List<Marker> result = new ArrayList<Marker>(this.size);
        for (List<Marker> chunkMarkers : this.chunkMap. values()) {
            result.addAll(chunkMarkers);
        }
        return result;
    }

    /**
     * Gets all markers in this dimension as a collection.
     *
     * @return Collection view of all markers
     */
    public Collection<Marker> getAllMarkers() {
        return this.values;
    }

    /**
     * Collection view of all markers in this dimension.
     */
    protected class Values extends AbstractCollection<Marker> {
        @Override
        public Iterator<Marker> iterator() {
            return (new ListMapValueIterator<Marker>(DimensionMarkersData.this.chunkMap)).setImmutable(true);
        }

        @Override
        public int size() {
            return DimensionMarkersData.this.size;
        }
    }
}