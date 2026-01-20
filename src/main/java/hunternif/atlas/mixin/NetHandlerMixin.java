package hunternif.atlas.mixin;

import hunternif.atlas.api.AtlasNetHandler;
import hunternif.atlas.network.AddMarkerPacket;
import hunternif.atlas.network.DeleteMarkerPacket;
import hunternif.atlas.network.MapDataPacket;
import hunternif.atlas.network.MarkersPacket;
import hunternif.atlas.network.PutBiomeTilePacket;
import hunternif.atlas.network.RegisterTileIdPacket;
import hunternif.atlas.network.TileNameIDPacket;
import hunternif.atlas.network.TilesPacket;

import net.minecraft.src.NetHandler;

import org.spongepowered.asm.mixin.Mixin;

@Mixin(NetHandler.class)
public class NetHandlerMixin implements AtlasNetHandler {
    public void handleMapData(MapDataPacket pkt) {
    }

    public void handleMapData(RegisterTileIdPacket pkt) {
    }

    public void handleMapData(PutBiomeTilePacket pkt) {
    }

    public void handleMapData(TilesPacket pkt) {
    }

    public void handleMapData(MarkersPacket pkt) {
    }

    public void handleMapData(AddMarkerPacket pkt) {
    }

    public void handleMapData(TileNameIDPacket pkt) {
    }

    public void handleMapData(DeleteMarkerPacket pkt) {
    }
}
