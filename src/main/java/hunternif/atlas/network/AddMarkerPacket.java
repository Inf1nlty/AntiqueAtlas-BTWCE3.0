package hunternif.atlas.network;

import hunternif.atlas.api.AtlasNetHandler;
import net.minecraft.src.NetHandler;
import net.minecraft. src.Packet;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

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

    public void readPacketData(DataInput in) throws IOException {
        this.atlasID = in.readUnsignedShort();
        this.dimension = in.readUnsignedShort();
        this.type = in.readUTF();
        this.label = in.readUTF();
        this.x = in. readInt();
        this.y = in.readInt();
        this.visibleAhead = in.readBoolean();
    }

    public void writePacketData(DataOutput out) throws IOException {
        out.writeShort(this.atlasID);
        out.writeShort(this.dimension);
        out.writeUTF(this.type);
        out.writeUTF(this.label);
        out.writeInt(this.x);
        out.writeInt(this.y);
        out.writeBoolean(this.visibleAhead);
    }

    public void processPacket(NetHandler handler) {
        ((AtlasNetHandler)handler).handleMapData(this);
    }

    public int getPacketSize() {
        int ret = 4 + PacketUtils.getPacketSizeOfString(this.type);
        return ret + PacketUtils.getPacketSizeOfString(this.label) + 9;
    }
}