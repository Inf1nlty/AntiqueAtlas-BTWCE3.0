package hunternif.atlas.api;

import hunternif.atlas.network.AddMarkerPacket;
import hunternif.atlas.network.DeleteMarkerPacket;
import hunternif.atlas.network.MapDataPacket;
import hunternif.atlas.network.MarkersPacket;
import hunternif.atlas.network.PutBiomeTilePacket;
import hunternif.atlas.network.RegisterTileIdPacket;
import hunternif.atlas.network.TileNameIDPacket;
import hunternif.atlas.network.TilesPacket;

public interface AtlasNetHandler {
    default void handleMapData(MapDataPacket pkt) {
    }

    default void handleMapData(RegisterTileIdPacket pkt) {
    }

    default void handleMapData(PutBiomeTilePacket pkt) {
    }

    default void handleMapData(TilesPacket pkt) {
    }

    default void handleMapData(MarkersPacket pkt) {
    }

    default void handleMapData(AddMarkerPacket pkt) {
    }

    default void handleMapData(TileNameIDPacket pkt) {
    }

    default void handleMapData(DeleteMarkerPacket pkt) {
    }
}
