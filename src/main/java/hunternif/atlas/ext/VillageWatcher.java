package hunternif.atlas.ext;

import hunternif.atlas.AntiqueAtlasMod;
import hunternif.atlas.api.AtlasAPI;
import hunternif.atlas.marker.Marker;
import hunternif.atlas.marker.MarkersData;
import net.minecraft.src.*;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class VillageWatcher {
    private final Set<Village> visited = new HashSet();

    public void onWorldLoad(World world) {
        if (!world.isRemote) {
            this.visitAllUnvisitedVillages(world);
        }

    }

    public void onPopulateChunkPost(World world) {
        if (!world.isRemote) {
            this.visitAllUnvisitedVillages(world);
        }

    }

    public void visitAllUnvisitedVillages(World world) {
        VillageCollection villageCollection = world.villageCollectionObj;
        if (villageCollection != null) {
            for(Object o : villageCollection.getVillageList()) {
                Village village = (Village)o;
                if (!this.visited.contains(village)) {
                    this.visitVillage(world, village);
                }
            }

        }
    }

    public void visitVillage(World world, Village village) {
        int dim = world.provider.dimensionId;
        MarkersData markersData = AntiqueAtlasMod.globalMarkersData.getData();
        boolean foundMarker = false;
        int centerX = village.getCenter().posX;
        int centerZ = village.getCenter().posZ;

        for(int dx = -village.getVillageRadius(); dx <= village.getVillageRadius(); dx += 16) {
            for(int dz = -village.getVillageRadius(); dz <= village.getVillageRadius(); dz += 16) {
                if (dx * dx + dz * dz <= village.getVillageRadius() * village.getVillageRadius()) {
                    int chunkX = centerX + dx >> 4;
                    int chunkZ = centerZ + dz >> 4;
                    AtlasAPI.getTileAPI().putCustomGlobalTile(world, "npcVillageTerritory", chunkX, chunkZ);
                    List<Marker> markers = markersData.getMarkersAtChunk(dim, chunkX / 4, chunkZ / 4);
                    if (markers != null) {
                        Iterator<Marker> it = markers.iterator();

                        Marker marker;
                        do {
                            while(!it.hasNext()) {
                            }

                            marker = (Marker)it.next();
                        } while(!marker.getType().equals("village"));

                        foundMarker = true;
                    }
                }
            }
        }

        if (!foundMarker) {
            AtlasAPI.getMarkerAPI().putGlobalMarker(world, false, "village", I18n.getString("gui.antiqueatlas.marker.village"), centerX, centerZ);
        }

        for(Object o : village.getVillageDoorInfoList()) {
            VillageDoorInfo door = (VillageDoorInfo)o;
            AtlasAPI.getTileAPI().putCustomGlobalTile(world, "npcVillageDoor", door.posX >> 4, door.posZ >> 4);
        }

        this.visited.add(village);
    }
}
