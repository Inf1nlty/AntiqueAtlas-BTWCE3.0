package hunternif.atlas;

import hunternif.atlas.ext.ExtBiomeDataHandler;
import hunternif.atlas.ext.VillageWatcher;
import hunternif.atlas.marker.GlobalMarkersDataHandler;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.src.*;

import java.io.File;

public class AntiqueAtlasMod {

    public static final ExtBiomeDataHandler extBiomeData = new ExtBiomeDataHandler();
    public static final GlobalMarkersDataHandler globalMarkersData = new GlobalMarkersDataHandler();
    public static final VillageWatcher villageWatcher = new VillageWatcher();
    public static final SettingsConfig settings = new SettingsConfig();

    public static void initialize() {

        File configDir = new File("config");
        if (!configDir.exists()) {
            configDir.mkdirs();
        }
        settings.load(new File(configDir, "antiqueatlas_settings.cfg"));
    }


    public static void onPopulateChunkPost(World world) {
        villageWatcher.onPopulateChunkPost(world);
    }

    @Environment(EnvType.CLIENT)
    public static void openAtlasGUI(ItemStack stack) {
        Minecraft mc = Minecraft.getMinecraft();
        if (mc != null && mc.currentScreen == null) {
            hunternif.atlas.client.gui.GuiAtlas guiAtlas = new hunternif.atlas.client.gui.GuiAtlas();
            mc.displayGuiScreen(guiAtlas. setAtlasItemStack(stack));
        }
    }

    public static void onPlayerLogin(EntityPlayerMP player) {
        if (player != null && !player.worldObj.isRemote) {
//            extBiomeData.onWorldLoad(player.worldObj);
            extBiomeData.onPlayerLogin(player);
            globalMarkersData.onPlayerLogin(player);
            villageWatcher.onWorldLoad(player.worldObj);
        }
    }

    public static void updateBiomeTextureConfig() {
    }

    public static void updateMarkerTextureConfig() {
    }

    public static void updateExtTileConfig() {
    }
}