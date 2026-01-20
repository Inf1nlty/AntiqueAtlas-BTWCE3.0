package hunternif.atlas.network;

import hunternif.atlas.api.AtlasNetHandler;
import net.minecraft.src.NetHandler;
import net.minecraft.src.Packet;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class PutBiomeTilePacket extends Packet {
    public int atlasID;
    public int dimension;
    public int x;
    public int z;
    public int biomeID;

    public PutBiomeTilePacket(int atlasID, int dimension, int x, int z, int biomeID) {
        this.atlasID = atlasID;
        this.dimension = dimension;
        this.x = x;
        this.z = z;
        this.biomeID = biomeID;
    }

    public void readPacketData(DataInput in) throws IOException {
        this.atlasID = in.readUnsignedShort();
        this.dimension = in.readUnsignedShort();
        this.x = in.readInt();
        this.z = in.readInt();
        this.biomeID = in.readUnsignedShort();
    }

    public void writePacketData(DataOutput out) throws IOException {
        out.writeShort(this.atlasID);
        out.writeShort(this.dimension);
        out.writeInt(this.x);
        out.writeInt(this.z);
        out.writeShort(this.biomeID);
    }

    public void processPacket(NetHandler handler) {
        ((AtlasNetHandler)handler).handleMapData(this);
    }

    public int getPacketSize() {
        return 14;
    }
}
