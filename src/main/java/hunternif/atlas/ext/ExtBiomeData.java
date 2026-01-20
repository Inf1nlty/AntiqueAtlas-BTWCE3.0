package hunternif.atlas.ext;

import hunternif.atlas.network.AtlasNetwork;
import hunternif.atlas.network.TilesPacket;
import hunternif.atlas.util.Log;
import hunternif.atlas.util.ShortVec2;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.EntityPlayerMP;
import net.minecraft. src.NBTTagCompound;
import net.minecraft.src.NBTTagList;
import net.minecraft.src.WorldSavedData;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ExtBiomeData extends WorldSavedData {
    private static final int VERSION = 1;
    private static final String TAG_VERSION = "aaVersion";
    private static final String TAG_DIMENSION_MAP_LIST = "dimMap";
    private static final String TAG_DIMENSION_ID = "dimID";
    private static final String TAG_BIOME_IDS = "biomeIDs";
    private final Map<Integer, Map<ShortVec2, Integer>> dimensionMap = new ConcurrentHashMap<Integer, Map<ShortVec2, Integer>>(2, 0.75F, 2);
    private final ShortVec2 tempCoords = new ShortVec2(0, 0);

    public ExtBiomeData(String key) {
        super(key);
    }

    public void readFromNBT(NBTTagCompound compound) {
        int version = compound.getInteger("aaVersion");
        if (version < 1) {
            Log.warn("Outdated atlas data format!  Was {} but current is {}", new Object[]{version, 1});
            this.markDirty();
        }

        NBTTagList dimensionMapList = compound.getTagList("dimMap");

        for (int d = 0; d < dimensionMapList.tagCount(); ++d) {
            NBTTagCompound tag = (NBTTagCompound)dimensionMapList.tagAt(d);
            int dimensionID = tag.getInteger("dimID");
            Map<ShortVec2, Integer> biomeMap = this.getBiomesInDimension(dimensionID);
            int[] intArray = tag.getIntArray("biomeIDs");

            for (int i = 0; i < intArray.length; i += 3) {
                ShortVec2 coords = new ShortVec2(intArray[i], intArray[i + 1]);
                biomeMap.put(coords, intArray[i + 2]);
            }
        }
    }

    public void writeToNBT(NBTTagCompound compound) {
        compound.setInteger("aaVersion", 1);
        NBTTagList dimensionMapList = new NBTTagList();

        for (Integer dimension : this.dimensionMap.keySet()) {
            NBTTagCompound tag = new NBTTagCompound();
            tag.setInteger("dimID", dimension);
            Map<ShortVec2, Integer> biomeMap = this.getBiomesInDimension(dimension);
            int[] intArray = new int[biomeMap.size() * 3];
            int i = 0;

            for (Map.Entry<ShortVec2, Integer> entry : biomeMap.entrySet()) {
                intArray[i++] = entry.getKey().x;
                intArray[i++] = entry.getKey().y;
                intArray[i++] = entry.getValue();
            }

            tag.setIntArray("biomeIDs", intArray);
            dimensionMapList.appendTag(tag);
        }

        compound.setTag("dimMap", dimensionMapList);
    }

    private Map<ShortVec2, Integer> getBiomesInDimension(int dimension) {
        Map<ShortVec2, Integer> map = this.dimensionMap.get(dimension);
        if (map == null) {
            map = new ConcurrentHashMap<ShortVec2, Integer>(2, 0.75F, 2);
            this.dimensionMap.put(dimension, map);
        }

        return map;
    }

    public int getBiomeIdAt(int dimension, int x, int z) {
        Integer biomeID = this.getBiomesInDimension(dimension).get(this.tempCoords.set(x, z));
        return biomeID == null ? -1 :  biomeID;
    }

    public void setBiomeIdAt(int dimension, int x, int z, int biomeID) {
        this.getBiomesInDimension(dimension).put(new ShortVec2(x, z), biomeID);
        this.markDirty();
    }

    public void removeBiomeAt(int dimension, int x, int z) {
        this.getBiomesInDimension(dimension).remove(this.tempCoords.set(x, z));
        this.markDirty();
    }

    public void syncOnPlayer(EntityPlayer player) {

        if (!(player instanceof EntityPlayerMP)) {
            Log.warn("syncOnPlayer called with non-server player: {}", new Object[]{player});
            return;
        }

        for (Integer dimension : this.dimensionMap.keySet()) {
            TilesPacket packet = new TilesPacket(dimension);
            Map<ShortVec2, Integer> biomes = this.getBiomesInDimension(dimension);

            for (Map.Entry<ShortVec2, Integer> entry : biomes.entrySet()) {
                packet.addTile(entry.getKey().x, entry.getKey().y, entry.getValue());
            }

            AtlasNetwork.sendTo(packet, (EntityPlayerMP)player);
        }

        Log.info("Sent custom biome data to player {}", new Object[]{player.getCommandSenderName()});
    }
}