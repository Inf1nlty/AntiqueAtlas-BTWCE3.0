package hunternif.atlas.marker;

import hunternif.atlas.util.ShortVec2;
import net.minecraft.src.StatCollector;

public class Marker {
    private final int id;
    private final String type;
    private final String label;
    private final int dim;
    private final int x;
    private final int z;
    private final boolean visibleAhead;
    private boolean isGlobal;

    public Marker(int id, String type, String label, int dimension, int x, int z, boolean visibleAhead) {
        this.id = id;
        this.type = type;
        this.label = label == null ? "" : label;
        this.dim = dimension;
        this.x = x;
        this.z = z;
        this.visibleAhead = visibleAhead;
    }

    public int getId() {
        return this.id;
    }

    public String getType() {
        return this.type;
    }

    public String getLabel() {
        return this.label;
    }

    public String getLocalizedLabel() {
        return StatCollector.translateToLocal(this.label);
    }

    public int getDimension() {
        return this.dim;
    }

    public int getX() {
        return this.x;
    }

    public int getZ() {
        return this.z;
    }

    public int getChunkX() {
        return this.x >> 4;
    }

    public int getChunkZ() {
        return this.z >> 4;
    }

    public boolean isVisibleAhead() {
        return this.visibleAhead;
    }

    public boolean isGlobal() {
        return this.isGlobal;
    }

    protected Marker setGlobal(boolean value) {
        this.isGlobal = value;
        return this;
    }

    public boolean equals(Object obj) {
        if (obj instanceof Marker marker) {
            return this.id == marker.id;
        } else {
            return false;
        }
    }

    public ShortVec2 getChunkCoords() {
        return new ShortVec2(this.x >> 4, this.z >> 4);
    }

    public String toString() {
        return "#" + this.id + "\"" + this.label + "\"@(" + this.x + ", " + this.z + ")";
    }
}
