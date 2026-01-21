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

    public MapDataPacket() {
        this.atlasID = -1;
        this.data = new byte[0];
    }

    public MapDataPacket(int atlasID, byte[] data) {
        this.atlasID = atlasID;
        this.data = data == null ? new byte[0] : data;
    }

    @Override
    public void writePacketData(DataOutput out) throws IOException {
        out.writeShort(this.atlasID);
        out.writeInt(this.data.length);
        out.write(this.data);
    }

    @Override
    public void readPacketData(DataInput in) throws IOException {
        // Use readShort to match writeShort above (signed)
        this.atlasID = in.readShort();
        int len = in.readInt();
        if (len < 0) len = 0;
        this.data = new byte[len];
        in.readFully(this.data);
    }

    @Override
    public void processPacket(NetHandler handler) {
        ((AtlasNetHandler)handler).handleMapData(this);
    }

    @Override
    public int getPacketSize() {
        return 6 + (this.data == null ? 0 : this.data.length);
    }
}