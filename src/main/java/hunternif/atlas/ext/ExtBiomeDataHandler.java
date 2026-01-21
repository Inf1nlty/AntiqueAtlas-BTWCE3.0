package hunternif.atlas.ext;

import hunternif.atlas.api.AtlasAPI;
import hunternif.atlas.client.TextureSet;

import net.minecraft.src.BiomeGenBase;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.World;

public class ExtBiomeDataHandler {
    private static final String DATA_KEY = "aAtlasExtTiles";
    private ExtBiomeData data;

    public ExtBiomeDataHandler() {
        registerVanillaBiomes();
    }

    private void registerVanillaBiomes() {

        AtlasAPI.getTileAPI().setBiomeTexture(BiomeGenBase.taiga.biomeID, TextureSet.PINES);
        AtlasAPI.getTileAPI().setBiomeTexture(BiomeGenBase.taigaHills.biomeID, TextureSet.PINES_HILLS);

    }

    public void onWorldLoad(World world) {
        if (!world.isRemote) {
            this.data = (ExtBiomeData)world.loadItemData(ExtBiomeData.class, "aAtlasExtTiles");
            if (this.data == null) {
                this.data = new ExtBiomeData("aAtlasExtTiles");
                this.data.markDirty();
                world.setItemData("aAtlasExtTiles", this.data);
            }
        }
    }

    public ExtBiomeData getData() {
        if (this.data == null) {
            this.data = new ExtBiomeData("aAtlasExtTiles");
        }

        return this.data;
    }

    public void onPlayerLogin(EntityPlayer player) {
        ExtTileIdMap.instance().syncOnPlayer(player);
        this.getData().syncOnPlayer(player);
    }
}