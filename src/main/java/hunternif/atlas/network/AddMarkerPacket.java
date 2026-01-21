package hunternif.atlas.network;

import hunternif.atlas.api.AtlasNetHandler;
import net.minecraft.src.NetHandler;
import net.minecraft.src.Packet;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

/**
 * Packet for adding a marker (client -> server, then server broadcasts).
 */
public class AddMarkerPacket extends Packet {
    public int atlasID;
    public int dimension;
    public String type;
    public String label;
    public int x;
    public int y;
    public boolean visibleAhead;

    public AddMarkerPacket() {
    }

    public AddMarkerPacket(int atlasID, int dimension, String type, String label, int x, int y, boolean visibleAhead) {
        this.atlasID = atlasID;
        this.dimension = dimension;
        this.type = type;
        this.label = label;
        this.x = x;
        this.y = y;
        this.visibleAhead = visibleAhead;
    }

    @Override
    public void readPacketData(DataInput in) throws IOException {
        // Use signed short to preserve negative values if any (match writeShort below)
        this.atlasID = in.readShort();
        this.dimension = in.readShort();
        this.type = in.readUTF();
        this.label = in.readUTF();
        this.x = in.readInt();
        this.y = in.readInt();
        this.visibleAhead = in.readBoolean();
    }

    @Override
    public void writePacketData(DataOutput out) throws IOException {
        // Write as signed short to match readShort() on the other side.
        out.writeShort(this.atlasID);
        out.writeShort(this.dimension);
        out.writeUTF(this.type);
        out.writeUTF(this.label);
        out.writeInt(this.x);
        out.writeInt(this.y);
        out.writeBoolean(this.visibleAhead);
    }

    @Override
    public void processPacket(NetHandler handler) {
        ((AtlasNetHandler)handler).handleMapData(this);
    }

    @Override
    public int getPacketSize() {
        int ret = 4 + PacketUtils.getPacketSizeOfString(this.type);
        return ret + PacketUtils.getPacketSizeOfString(this.label) + 9;
    }
}