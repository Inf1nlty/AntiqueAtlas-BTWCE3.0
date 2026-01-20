package hunternif.atlas.marker;


import net.minecraft.src.EntityPlayer;
import net.minecraft.src.World;

public class GlobalMarkersDataHandler {
    private static final String DATA_KEY = "aAtlasGlobalMarkers";
    private GlobalMarkersData data;

    public void onWorldLoad(World world) {
        if (!world.isRemote) {
            this.data = (GlobalMarkersData)world.loadItemData(GlobalMarkersData.class, "aAtlasGlobalMarkers");
            if (this.data == null) {
                this.data = new GlobalMarkersData("aAtlasGlobalMarkers");
                this.data.markDirty();
                world.setItemData("aAtlasGlobalMarkers", this.data);
            }
        }

    }

    public GlobalMarkersData getData() {
        if (this.data == null) {
            this.data = new GlobalMarkersData("aAtlasGlobalMarkers");
        }

        return this.data;
    }

    public void onPlayerLogin(EntityPlayer player) {
        this.getData().syncOnPlayer(player);
    }
}
