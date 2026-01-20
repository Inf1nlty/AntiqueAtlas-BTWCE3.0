package hunternif.atlas.network;

import hunternif.atlas.api.AtlasNetHandler;
import hunternif.atlas.util.ShortVec2;

import net.minecraft.src.NetHandler;
import net.minecraft.src.Packet;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class TilesPacket extends Packet {
    public static final int ENTRY_SIZE_BYTES = 6;
    public int dimension;
    public final Map<ShortVec2, Integer> biomeMap = new HashMap();

    public TilesPacket() {
    }

    public TilesPacket(int dimension) {
        this.dimension = dimension;
    }

    public TilesPacket addTile(int x, int y, int biomeID) {
        this.biomeMap.put(new ShortVec2(x, y), biomeID);
        return this;
    }

    public boolean isEmpty() {
        return this.biomeMap.isEmpty();
    }

    public void writePacketData(DataOutput out) throws IOException {
        out.writeShort(this.dimension);
        out.writeShort(this.biomeMap.size());

        for(Map.Entry<ShortVec2, Integer> entry : this.biomeMap.entrySet()) {
            out.writeShort(((ShortVec2)entry.getKey()).x);
            out.writeShort(((ShortVec2)entry.getKey()).y);
            out.writeShort((Integer)entry.getValue());
        }

    }

    public void readPacketData(DataInput in) throws IOException {
        this.dimension = in.readShort();
        int length = in.readShort();

        for(int i = 0; i < length; ++i) {
            ShortVec2 coords = new ShortVec2(in.readShort(), in.readShort());
            this.biomeMap.put(coords, Integer.valueOf(in.readShort()));
        }

    }

    public void processPacket(NetHandler handler) {
        ((AtlasNetHandler)handler).handleMapData(this);
    }

    public int getPacketSize() {
        return 4 + this.biomeMap.size() * 6;
    }
}
