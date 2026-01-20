package hunternif.atlas.mixin;

import hunternif.atlas.AntiqueAtlasItems;
import hunternif.atlas.AntiqueAtlasMod;
import hunternif.atlas.api.AtlasAPI;
import hunternif.atlas.api.AtlasNetHandler;
import hunternif.atlas.api.impl.TileApiImpl;
import hunternif.atlas.core.AtlasData;
import hunternif.atlas.core.Tile;
import hunternif.atlas.ext.ExtBiomeData;
import hunternif.atlas.ext.ExtTileIdMap;
import hunternif.atlas.marker.Marker;
import hunternif.atlas.marker.MarkersData;
import hunternif.atlas.network.DeleteMarkerPacket;
import hunternif.atlas.network.MapDataPacket;
import hunternif.atlas.network.MarkersPacket;
import hunternif.atlas.network.PutBiomeTilePacket;
import hunternif.atlas.network.TileNameIDPacket;
import hunternif.atlas.network.TilesPacket;
import hunternif.atlas.util.ShortVec2;

import java.util.Map;

import net.minecraft.src.Minecraft;
import net.minecraft.src.NetClientHandler;
import net.minecraft.src.WorldClient;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(NetClientHandler.class)
public class NetClientHandlerMixin implements AtlasNetHandler {
    @Shadow
    private Minecraft mc;
    @Shadow
    private WorldClient worldClient;

    public void handleMapData(MapDataPacket pkt) {
        if (pkt.data != null) {
            AtlasData atlasData = AntiqueAtlasItems.itemAtlas.getAtlasData(pkt.atlasID, this.worldClient);
            atlasData.readFromPacket(pkt);
        }
    }

    public void handleMapData(PutBiomeTilePacket pkt) {
        AtlasData data = AntiqueAtlasItems.itemAtlas.getAtlasData(pkt.atlasID, this.mc.theWorld);
        data.setTile(pkt.dimension, pkt.x, pkt.z, new Tile(pkt.biomeID));
    }

    public void handleMapData(TilesPacket pkt) {
        ExtBiomeData data = AntiqueAtlasMod.extBiomeData.getData();

        for(Map.Entry<ShortVec2, Integer> entry : pkt.biomeMap.entrySet()) {
            ShortVec2 key = (ShortVec2)entry.getKey();
            data.setBiomeIdAt(pkt.dimension, key.x, key.y, (Integer)entry.getValue());
        }

    }

    public void handleMapData(MarkersPacket pkt) {
        MarkersData markersData;
        if (pkt.isGlobal()) {
            markersData = AntiqueAtlasMod.globalMarkersData.getData();
        } else {
            markersData = AntiqueAtlasItems.itemAtlas.getMarkersData(pkt.atlasID, this.mc.theWorld);
        }

        MarkersData markersData2 = markersData;

        for(Marker marker : pkt.markersByType.values()) {
            markersData2.loadMarker(marker);
        }

    }

    public void handleMapData(DeleteMarkerPacket pkt) {
        MarkersData markersData;
        if (pkt.isGlobal()) {
            markersData = AntiqueAtlasMod.globalMarkersData.getData();
        } else {
            markersData = AntiqueAtlasItems.itemAtlas.getMarkersData(pkt.atlasID, this.mc.theWorld);
        }

        markersData.removeMarker(pkt.markerID);
    }

    public void handleMapData(TileNameIDPacket pkt) {
        for(Map.Entry<String, Integer> entry : pkt.nameToIdMap.entrySet()) {
            ExtTileIdMap.instance().setPseudoBiomeID((String)entry.getKey(), (Integer)entry.getValue());
        }

        TileApiImpl tileAPI = (TileApiImpl)AtlasAPI.getTileAPI();
        tileAPI.onTileIdRegistered(pkt.nameToIdMap);
    }
}
