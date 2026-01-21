package hunternif.atlas.network;

import hunternif.atlas.api.AtlasNetHandler;
import net.minecraft.src.NetHandler;
import net.minecraft.src.Packet;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

/**
 * Packet used to broadcast marker deletion to clients.
 */
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

    @Override
    public void readPacketData(DataInput in) throws IOException {
        // IMPORTANT: readShort preserves signed short values (so -1 stays -1).
        // Previously readUnsignedShort caused -1 to be parsed as 65535, breaking global marker handling.
        this.atlasID = in.readShort();
        this.markerID = in.readInt();
    }

    @Override
    public void writePacketData(DataOutput out) throws IOException {
        // Atlas ID is written as a short (signed); -1 -> 0xFFFF and read back with readShort -> -1.
        out.writeShort(this.atlasID);
        out.writeInt(this.markerID);
    }

    public boolean isGlobal() {
        return this.atlasID == GLOBAL;
    }

    @Override
    public void processPacket(NetHandler handler) {
        ((AtlasNetHandler) handler).handleMapData(this);
    }

    @Override
    public int getPacketSize() {
        return 6; // 2 bytes (short) + 4 bytes (int)
    }
}