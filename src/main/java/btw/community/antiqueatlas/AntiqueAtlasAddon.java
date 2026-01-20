package btw.community.antiqueatlas;

import api.AddonHandler;
import api.BTWAddon;

import hunternif.atlas.AntiqueAtlasItems;
import hunternif.atlas.AntiqueAtlasMod;
import hunternif.atlas.AntiqueAtlasRecipes;
import hunternif.atlas.network.AntiqueAtlasNetwork;

public class AntiqueAtlasAddon extends BTWAddon {

    @Override
    public void initialize() {
        AddonHandler.logMessage(getName() + " v" + getVersionString() + " Initializing...");

        AntiqueAtlasMod.initialize(this);

        AntiqueAtlasItems.registerItems();

        AntiqueAtlasRecipes.registerRecipes();

        AntiqueAtlasNetwork.register(this);

        AddonHandler.logMessage(getName() + " initialized successfully!");
    }
}