package hunternif.atlas.mixin;

import hunternif.atlas.AntiqueAtlasItems;
import hunternif.atlas.api.AtlasAPI;
import hunternif.atlas.api.AtlasNetHandler;
import hunternif.atlas.ext.ExtTileIdMap;
import hunternif.atlas.marker.Marker;
import hunternif.atlas.marker.MarkersData;
import hunternif.atlas.network.AddMarkerPacket;
import hunternif.atlas.network.AtlasNetwork;
import hunternif.atlas.network.DeleteMarkerPacket;
import hunternif.atlas.network.MarkersPacket;
import hunternif.atlas.network.PutBiomeTilePacket;
import hunternif.atlas.network.RegisterTileIdPacket;
import hunternif.atlas.network.TileNameIDPacket;
import hunternif.atlas.util.Log;

import net.minecraft.src.ItemStack;
import net.minecraft.src.NetServerHandler;
import net.minecraft.src.EntityPlayerMP;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(NetServerHandler.class)
public class NetServerHandlerMixin implements AtlasNetHandler {

    @Shadow
    public EntityPlayerMP playerEntity;

    @Override
    public void handleMapData(AddMarkerPacket pkt) {

        if (!this. playerEntity.inventory.hasItemStack(
                new ItemStack(AntiqueAtlasItems.itemAtlas, 1, pkt.atlasID))) {
            Log.error("Player %s attempted to put marker into someone else's Atlas #%d",
                    this.playerEntity.username,
                    pkt.atlasID);
            return;
        }

        MarkersData markersData = AntiqueAtlasItems.itemAtlas.getMarkersData(
                pkt.atlasID,
                this.playerEntity.worldObj);

        Marker marker = markersData.createAndSaveMarker(
                pkt.type,
                pkt.label,
                pkt.dimension,
                pkt.x,
                pkt.y,
                pkt.visibleAhead);

        MarkersPacket packetForClients = new MarkersPacket(
                pkt.atlasID,
                pkt. dimension,
                new Marker[]{marker});
        AtlasNetwork.sendToAll(packetForClients);
    }

    @Override
    public void handleMapData(RegisterTileIdPacket pkt) {
        int biomeID = ExtTileIdMap.instance().getOrCreatePseudoBiomeID(pkt.name);
        TileNameIDPacket packet = new TileNameIDPacket();
        packet.put(pkt.name, biomeID);
        AtlasNetwork.sendToAll(packet);
    }

    @Override
    public void handleMapData(PutBiomeTilePacket pkt) {
        if (!this.playerEntity.inventory.hasItemStack(
                new ItemStack(AntiqueAtlasItems.itemAtlas, 1, pkt.atlasID))) {
            Log.error("Player %s attempted to modify someone else's Atlas #%d",
                    this.playerEntity.username,
                    pkt.atlasID);
        } else {
            AtlasAPI.getTileAPI().putBiomeTile(
                    this.playerEntity.worldObj,
                    pkt.atlasID,
                    pkt.biomeID,
                    pkt.x,
                    pkt.z);
        }
    }

    @Override
    public void handleMapData(DeleteMarkerPacket pkt) {
        if (!this.playerEntity.inventory.hasItemStack(
                new ItemStack(AntiqueAtlasItems.itemAtlas, 1, pkt.atlasID))) {
            Log.error("Player %s attempted to delete marker from someone else's Atlas #%d",
                    this.playerEntity.username,
                    pkt.atlasID);
        } else if (pkt.isGlobal()) {
            AtlasAPI.getMarkerAPI().deleteGlobalMarker(
                    this.playerEntity. worldObj,
                    pkt.markerID);
        } else {
            AtlasAPI.getMarkerAPI().deleteMarker(
                    this.playerEntity.worldObj,
                    pkt.atlasID,
                    pkt.markerID);
        }
    }
}