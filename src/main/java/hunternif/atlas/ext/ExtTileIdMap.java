package hunternif.atlas.ext;

import com.google.common.collect.BiMap;
import com.google.common.collect. HashBiMap;
import hunternif.atlas.network.AtlasNetwork;
import hunternif.atlas.network.TileNameIDPacket;
import hunternif.atlas.util.Log;
import hunternif.atlas.util.SaveData;
import net.minecraft. src.EntityPlayer;
import net.minecraft.src.EntityPlayerMP;

import java.util.Map;

public class ExtTileIdMap extends SaveData {
    private static final ExtTileIdMap INSTANCE = new ExtTileIdMap();
    public static final String TILE_VILLAGE_HOUSE = "npcVillageDoor";
    public static final String TILE_VILLAGE_TERRITORY = "npcVillageTerritory";
    public static final int NOT_FOUND = -1;
    private int lastID = -1;
    private final BiMap<String, Integer> nameToIdMap = HashBiMap.create();

    public static ExtTileIdMap instance() {
        return INSTANCE;
    }

    public int getOrCreatePseudoBiomeID(String uniqueName) {
        Integer id = this.nameToIdMap.get(uniqueName);
        if (id == null) {
            id = this.findNewID();
            this.nameToIdMap.put(uniqueName, id);
            this.markDirty();
        }

        return id;
    }

    public int getPseudoBiomeID(String uniqueName) {
        Integer id = this.nameToIdMap.get(uniqueName);
        return id == null ? -1 : id;
    }

    private int findNewID() {
        while (true) {
            if (this.lastID > -32768) {
                BiMap<Integer, String> inverse = this.nameToIdMap.inverse();
                int i = this.lastID - 1;
                this.lastID = i;
                if (inverse.containsKey(i)) {
                    continue;
                }
            }

            return this. lastID;
        }
    }

    public void setPseudoBiomeID(String uniqueName, int id) {
        this.nameToIdMap.forcePut(uniqueName, id);
    }

    Map<String, Integer> getMap() {
        return this.nameToIdMap;
    }

    public void syncOnPlayer(EntityPlayer player) {

        if (!(player instanceof EntityPlayerMP)) {
            Log.warn("syncOnPlayer called with non-server player: {}", new Object[]{player});
            return;
        }

        AtlasNetwork. sendTo(new TileNameIDPacket(this.nameToIdMap), (EntityPlayerMP)player);
    }
}