package hunternif.atlas.marker;

import hunternif.atlas.network.MarkersPacket;

import net.minecraft.src.EntityPlayer;

public class GlobalMarkersData extends MarkersData {
    public GlobalMarkersData(String key) {
        super(key);
    }

    public Marker createAndSaveMarker(String type, String label, int dimension, int x, int y, boolean visibleAhead) {
        return super.createAndSaveMarker(type, label, dimension, x, y, visibleAhead).setGlobal(true);
    }

    public Marker loadMarker(Marker marker) {
        return super.loadMarker(marker).setGlobal(true);
    }

    protected void syncOnPlayer(EntityPlayer player) {
        this.syncOnPlayer(-1, player);
    }

    protected MarkersPacket newMarkersPacket(int atlasID, int dimension) {
        return new MarkersPacket(dimension, new Marker[0]);
    }
}
