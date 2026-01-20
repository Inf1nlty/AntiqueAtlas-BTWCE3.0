package hunternif.atlas;

import api.BTWAddon;
import hunternif.atlas.client.gui.GuiAtlas;
import hunternif.atlas.ext.ExtBiomeDataHandler;
import hunternif.atlas.ext.VillageWatcher;
import hunternif.atlas.marker.GlobalMarkersDataHandler;
import net.minecraft.src.*;

import java.io.File;

public class AntiqueAtlasMod {

    private static BTWAddon addonInstance;

    public static final ExtBiomeDataHandler extBiomeData = new ExtBiomeDataHandler();
    public static final GlobalMarkersDataHandler globalMarkersData = new GlobalMarkersDataHandler();
    private static final VillageWatcher villageWatcher = new VillageWatcher();
    public static final SettingsConfig settings = new SettingsConfig();

    public static void initialize(BTWAddon addon) {
        addonInstance = addon;

        File configDir = new File("config");
        if (!configDir.exists()) {
            configDir.mkdirs();
        }
        settings.load(new File(configDir, "antiqueatlas_settings.cfg"));
    }

    public static BTWAddon getAddon() {
        return addonInstance;
    }

    public static void onPopulateChunkPost(World world) {
        if (villageWatcher != null) {
            villageWatcher.onPopulateChunkPost(world);
        }
    }

    public static void openAtlasGUI(ItemStack stack) {
        Minecraft mc = Minecraft.getMinecraft();
        if (mc != null && mc.currentScreen == null) {
            GuiAtlas guiAtlas = new GuiAtlas();
            mc.displayGuiScreen(guiAtlas. setAtlasItemStack(stack));
        }
    }

    public static void onPlayerLogin(EntityPlayerMP player) {
        if (player != null && ! player.worldObj.isRemote) {
            extBiomeData.onPlayerLogin(player);
            globalMarkersData.onPlayerLogin(player);
        }
    }

    public static void onWorldLoad(World world) {
        if (world != null && !world.isRemote) {
            extBiomeData.onWorldLoad(world);
            globalMarkersData.onWorldLoad(world);
            villageWatcher.onWorldLoad(world);
        }
    }

    public static void updateBiomeTextureConfig() {
        // TODO:
    }

    public static void updateMarkerTextureConfig() {
        // TODO:
    }

    public static void updateExtTileConfig() {
        // TODO:
    }
}