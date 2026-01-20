package hunternif.atlas.marker;

import hunternif.atlas.client.Textures;
import net.minecraft.src.ResourceLocation;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public enum MarkerTextureMap {
    INSTANCE;

    private final Map<String, ResourceLocation> map = new HashMap();
    private final ResourceLocation defaultTexture;

    public static MarkerTextureMap instance() {
        return INSTANCE;
    }

    private MarkerTextureMap() {
        this.defaultTexture = Textures.MARKER_RED_X_LARGE;
        this.registerDefaultMarker();
    }

    private void registerDefaultMarker() {
        this.setTexture("bed", Textures.MARKER_BED);
        this.setTexture("diamond", Textures.MARKER_DIAMOND);
        this.setTexture("google", Textures.MARKER_GOOGLE_MARKER);
        this.setTexture("nether_portal", Textures.MARKER_NETHER_PORTAL);
        this.setTexture("pickaxe", Textures.MARKER_PICKAXE);
        this.setTexture("red_x_large", Textures.MARKER_RED_X_LARGE);
        this.setTexture("red_x_small", Textures.MARKER_RED_X_SMALL);
        this.setTexture("scroll", Textures.MARKER_SCROLL);
        this.setTexture("skull", Textures.MARKER_SKULL);
        this.setTexture("sword", Textures.MARKER_SWORD);
        this.setTexture("tomb", Textures.MARKER_TOMB);
        this.setTexture("tower", Textures.MARKER_TOWER);
        this.setTexture("village", Textures.MARKER_VILLAGE);
    }

    public void setTexture(String markerType, ResourceLocation texture) {
        this.map.put(markerType, texture);
    }

    public boolean setTextureIfNone(String markerType, ResourceLocation texture) {
        if (this.map.containsKey(markerType)) {
            return false;
        } else {
            this.map.put(markerType, texture);
            return true;
        }
    }

    public ResourceLocation getTexture(String markerType) {
        ResourceLocation texture = (ResourceLocation)this.map.get(markerType);
        return texture == null ? this.defaultTexture : texture;
    }

    Map<String, ResourceLocation> getMap() {
        return this.map;
    }

    public Collection<String> getAllTypes() {
        return this.map.keySet();
    }

    public Collection<ResourceLocation> getAllTextures() {
        return this.map.values();
    }
}
