package hunternif.atlas.network;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;
import hunternif.atlas.api.AtlasNetHandler;
import hunternif.atlas.marker.Marker;
import net.minecraft. src.NetHandler;
import net.minecraft.src.Packet;

import java.io.DataInput;
import java.io.DataOutput;
import java.io. IOException;
import java.util. Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class MarkersPacket extends Packet {
    private static final int GLOBAL = -1;
    public int atlasID;
    public int dimension;
    public final ListMultimap<String, Marker> markersByType;

    public MarkersPacket() {
        this.markersByType = ArrayListMultimap.create();
    }

    public MarkersPacket(int atlasID, int dimension, Marker... markers) {
        this.markersByType = ArrayListMultimap.create();
        this.atlasID = atlasID;
        this.dimension = dimension;

        for (Marker marker : markers) {
            this.markersByType.put(marker.getType(), marker);
        }
    }

    public MarkersPacket(int dimension, Marker... markers) {
        this(-1, dimension, markers);
    }

    public MarkersPacket putMarker(Marker marker) {
        this.markersByType.put(marker.getType(), marker);
        return this;
    }

    public boolean isEmpty() {
        return this. markersByType.isEmpty();
    }

    public boolean isGlobal() {
        return this.atlasID == -1;
    }

    public void readPacketData(DataInput in) throws IOException {
        this.atlasID = in.readShort();
        this.dimension = in.readShort();
        int typesLength = in.readShort();

        for (int i = 0; i < typesLength; ++i) {
            String type = in.readUTF();
            int markersLength = in.readShort();

            for (int j = 0; j < markersLength; ++j) {
                Marker marker = new Marker(
                        in.readInt(),
                        type,
                        in.readUTF(),
                        this.dimension,
                        in.readInt(),
                        in.readInt(),
                        in.readBoolean()
                );
                this.markersByType.put(type, marker);
            }
        }
    }

    public void writePacketData(DataOutput out) throws IOException {
        out.writeShort(this.atlasID);
        out.writeShort(this.dimension);
        Set<String> types = this.markersByType.keySet();
        out.writeShort(types.size());

        for (String type :  types) {
            out.writeUTF(type);
            List<Marker> markers = this. markersByType.get(type);
            out.writeShort(markers.size());

            for (Marker marker : markers) {
                out.writeInt(marker.getId());
                out.writeUTF(marker.getLabel());
                out.writeInt(marker.getX());
                out.writeInt(marker.getZ());
                out.writeBoolean(marker.isVisibleAhead());
            }
        }
    }

    public void processPacket(NetHandler handler) {
        ((AtlasNetHandler)handler).handleMapData(this);
    }

    public int getPacketSize() {
        int ret = 6;

        for (Map.Entry<String, Collection<Marker>> entry : this. markersByType.asMap().entrySet()) {
            ret = ret + PacketUtils.getPacketSizeOfString(entry.getKey()) + 2;

            for (Marker marker : entry.getValue()) {
                ret = ret + 4 + PacketUtils.getPacketSizeOfString(marker. getLabel()) + 4 + 4 + 1;
            }
        }

        return ret;
    }
}