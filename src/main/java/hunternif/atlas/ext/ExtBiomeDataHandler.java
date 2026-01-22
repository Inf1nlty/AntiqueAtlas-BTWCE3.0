package hunternif.atlas.ext;

import net.minecraft.src.EntityPlayer;
import net.minecraft.src.World;

public class ExtBiomeDataHandler {
    private static final String DATA_KEY = "aAtlasExtTiles";
    private ExtBiomeData data;

//    public void onWorldLoad(World world) {
//        if (!world.isRemote) {
//            this.data = (ExtBiomeData) world.loadItemData(ExtBiomeData.class, DATA_KEY);
//            if (this.data == null) {
//                this.data = new ExtBiomeData(DATA_KEY);
//                this.data.markDirty();
//                world.setItemData(DATA_KEY, this.data);
//            }
//        }
//    }

    public ExtBiomeData getData() {
        if (this.data == null) {
            this.data = new ExtBiomeData(DATA_KEY);
        }
        return this.data;
    }

    public void onPlayerLogin(EntityPlayer player) {
        ExtTileIdMap.instance().syncOnPlayer(player);
        this.getData().syncOnPlayer(player);
    }
}