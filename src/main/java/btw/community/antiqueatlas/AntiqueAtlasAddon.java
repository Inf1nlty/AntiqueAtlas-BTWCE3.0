package btw.community.antiqueatlas;

import api.AddonHandler;
import api.BTWAddon;

import hunternif.atlas.AntiqueAtlasItems;
import hunternif.atlas.AntiqueAtlasMod;
import hunternif.atlas.AntiqueAtlasRecipes;
import hunternif.atlas.network.AntiqueAtlasNetwork;
import hunternif.atlas.network.AtlasVanillaPacketRegistry;

import net.minecraft.src.EntityPlayerMP;
import net.minecraft.src.NetServerHandler;

public class AntiqueAtlasAddon extends BTWAddon {

    @Override
    public void initialize() {
        AddonHandler.logMessage(getName() + " v" + getVersionString() + " Initializing...");

        AntiqueAtlasMod.initialize();

        AntiqueAtlasItems.registerItems();

        AntiqueAtlasRecipes.registerRecipes();

        AtlasVanillaPacketRegistry.registerAll();

        AntiqueAtlasNetwork.register(this);

        AddonHandler.logMessage(getName() + " initialized successfully!");
    }

    @Override
    public void preInitialize() {
    }

    @Override
    public void postInitialize() {
    }

    @Override
    public void serverPlayerConnectionInitialized(NetServerHandler serverHandler, EntityPlayerMP playerMP) {
        super.serverPlayerConnectionInitialized(serverHandler, playerMP);
        AntiqueAtlasMod.onPlayerLogin(playerMP);
    }
}