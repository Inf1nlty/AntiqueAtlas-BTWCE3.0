package hunternif.atlas.network;

import hunternif.atlas.api.AtlasNetHandler;
import net.minecraft.src.NetHandler;
import net.minecraft.src.Packet;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class MapDataPacket extends Packet {
    public int atlasID;
    public byte[] data;

    public MapDataPacket(int atlasID, byte[] data) {
        this.atlasID = atlasID;
        this.data = data;
    }

    public void writePacketData(DataOutput out) throws IOException {
        out.writeShort(this.atlasID);
        out.writeInt(this.data.length);
        out.write(this.data);
    }

    public void readPacketData(DataInput in) throws IOException {
        this.atlasID = in.readUnsignedShort();
        int len = in.readInt();
        this.data = new byte[len];
        in.readFully(this.data);
    }

    public void processPacket(NetHandler handler) {
        ((AtlasNetHandler)handler).handleMapData(this);
    }

    public int getPacketSize() {
        return 6 + this.data.length;
    }
}
