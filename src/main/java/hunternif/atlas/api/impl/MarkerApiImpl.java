package hunternif.atlas.api.impl;

import hunternif.atlas.AntiqueAtlasItems;
import hunternif.atlas.AntiqueAtlasMod;
import hunternif.atlas.api.MarkerAPI;
import hunternif.atlas.marker.Marker;
import hunternif.atlas.marker.MarkerTextureMap;
import hunternif.atlas.marker.MarkersData;
import hunternif.atlas.network.AddMarkerPacket;
import hunternif.atlas.network.AtlasNetwork;
import hunternif.atlas.network.DeleteMarkerPacket;
import hunternif.atlas.network.MarkersPacket;
import net.minecraft.src.ResourceLocation;
import net.minecraft.src.World;

public class MarkerApiImpl implements MarkerAPI {
    private static final int GLOBAL = -1;

    public void setTexture(String markerType, ResourceLocation texture) {
        MarkerTextureMap.instance().setTexture(markerType, texture);
    }

    public void putMarker(World world, boolean visibleAhead, int atlasID, String markerType, String label, int x, int z) {
        this.doPutMarker(world, visibleAhead, atlasID, markerType, label, x, z);
    }

    public void putGlobalMarker(World world, boolean visibleAhead, String markerType, String label, int x, int z) {
        this.doPutMarker(world, visibleAhead, -1, markerType, label, x, z);
    }

    private void doPutMarker(World world, boolean visibleAhead, int atlasID, String markerType, String label, int x, int z) {
        if (world.isRemote) {
            if (atlasID == -1) {

            } else {

                AtlasNetwork.sendToServer(new AddMarkerPacket(atlasID, world.provider.dimensionId, markerType, label, x, z, visibleAhead));
            }
        } else {
            if (atlasID == -1) {
                MarkersData data = AntiqueAtlasMod.globalMarkersData.getData();
                Marker marker = data.createAndSaveMarker(markerType, label, world.provider.dimensionId, x, z, visibleAhead);
                MarkersPacket pkt = new MarkersPacket(world.provider.dimensionId, new Marker[] { marker });
                AtlasNetwork.sendToAll(pkt);
            } else {
                MarkersData data2 = AntiqueAtlasItems.itemAtlas.getMarkersData(atlasID, world);
                Marker marker2 = data2.createAndSaveMarker(markerType, label, world.provider.dimensionId, x, z, visibleAhead);
                MarkersPacket pkt2 = new MarkersPacket(atlasID, world.provider.dimensionId, new Marker[]{marker2});
                AtlasNetwork.sendToAll(pkt2);
            }
        }
    }

    public void deleteMarker(World world, int atlasID, int markerID) {
        this.doDeleteMarker(world, atlasID, markerID);
    }

    public void deleteGlobalMarker(World world, int markerID) {
        this.doDeleteMarker(world, -1, markerID);
    }

    private void doDeleteMarker(World world, int atlasID, int markerID) {
        DeleteMarkerPacket packet = atlasID == -1 ? new DeleteMarkerPacket(markerID) : new DeleteMarkerPacket(atlasID, markerID);
        if (world.isRemote) {
            if (atlasID == -1) {
            } else {
                AtlasNetwork.sendToServer(packet);
            }
        } else {
            MarkersData markersData;
            if (atlasID == -1) {
                markersData = AntiqueAtlasMod.globalMarkersData.getData();
            } else {
                markersData = AntiqueAtlasItems.itemAtlas.getMarkersData(atlasID, world);
            }

            Marker removed = markersData.removeMarker(markerID);
            AtlasNetwork.sendToAll(packet);
        }
    }
}