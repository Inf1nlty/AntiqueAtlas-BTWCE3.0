package hunternif.atlas.network;

import hunternif.atlas.api.AtlasNetHandler;
import net.minecraft.src.NetHandler;
import net.minecraft.src.Packet;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class DeleteMarkerPacket extends Packet {
    private static final int GLOBAL = -1;
    public int atlasID;
    public int markerID;

    public DeleteMarkerPacket() {
    }

    public DeleteMarkerPacket(int atlasID, int markerID) {
        this.atlasID = atlasID;
        this.markerID = markerID;
    }

    public DeleteMarkerPacket(int markerID) {
        this(-1, markerID);
    }

    public void readPacketData(DataInput in) throws IOException {
        this.atlasID = in.readUnsignedShort();
        this.markerID = in.readInt();
    }

    public void writePacketData(DataOutput out) throws IOException {
        out.writeShort(this.atlasID);
        out.writeInt(this.markerID);
    }

    public boolean isGlobal() {
        return this.atlasID == -1;
    }

    public void processPacket(NetHandler handler) {
        ((AtlasNetHandler) handler).handleMapData(this);
    }

    public int getPacketSize() {
        return 6;
    }
}