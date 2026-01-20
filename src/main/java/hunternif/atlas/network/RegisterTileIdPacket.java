package hunternif.atlas.network;

import hunternif.atlas.api.AtlasNetHandler;
import net.minecraft.src.NetHandler;
import net.minecraft.src. Packet;

import java.io.DataInput;
import java. io.DataOutput;
import java.io.IOException;

public class RegisterTileIdPacket extends Packet {
    public String name;

    public RegisterTileIdPacket() {
    }

    public RegisterTileIdPacket(String uniqueTileName) {
        this.name = uniqueTileName;
    }

    public void readPacketData(DataInput in) throws IOException {
        this.name = in.readUTF();
    }

    public void writePacketData(DataOutput out) throws IOException {
        out.writeUTF(this.name);
    }

    public void processPacket(NetHandler handler) {
        ((AtlasNetHandler)handler).handleMapData(this);
    }

    public int getPacketSize() {
        return PacketUtils.getPacketSizeOfString(this.name);
    }
}