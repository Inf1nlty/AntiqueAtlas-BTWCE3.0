package hunternif.atlas.ext;

import net.minecraft.src.EntityPlayer;
import net.minecraft.src.World;

public class ExtBiomeDataHandler {
    private static final String DATA_KEY = "aAtlasExtTiles";
    private ExtBiomeData data;

//    public void onWorldLoad(World world) {
//        if (!world.isRemote) {
//            this.data = (ExtBiomeData)world.loadItemData(ExtBiomeData.class, "aAtlasExtTiles");
//            if (this.data == null) {
//                this.data = new ExtBiomeData("aAtlasExtTiles");
//                this.data.markDirty();
//                world.setItemData("aAtlasExtTiles", this.data);
//            }
//        }
//    }

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