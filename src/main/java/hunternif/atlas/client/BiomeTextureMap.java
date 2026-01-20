package hunternif.atlas.client;

import hunternif.atlas.core.Tile;
import hunternif.atlas.util.Log;
import hunternif.atlas.util.SaveData;
import net.minecraft.src.BiomeGenBase;
import net.minecraft.src.MathHelper;
import net.minecraft.src.ResourceLocation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BiomeTextureMap extends SaveData {
    final Map<Integer, TextureSet> textureMap = new HashMap();
    private static final BiomeTextureMap INSTANCE = new BiomeTextureMap();
    public static final TextureSet defaultTexture;

    public static BiomeTextureMap instance() {
        return INSTANCE;
    }

    public void setTexture(int biomeID, TextureSet textureSet) {
        if (textureSet == null) {
            Log.warn("Texture set is null!", new Object[0]);
        } else {
            TextureSet previous = (TextureSet)this.textureMap.put(biomeID, textureSet);
            if (previous == null) {
                this.markDirty();
            } else if (!previous.equals(textureSet)) {
                Log.error("Overwriting texture set for biome %d\n", new Object[]{biomeID});
                this.markDirty();
            }

            this.textureMap.put(biomeID, textureSet);
        }
    }

    public void autoRegister(int biomeID) {
        if (biomeID >= 0 && biomeID < 256) {
            BiomeGenBase biome = BiomeGenBase.biomeList[biomeID];
            if (biome == null) {
                Log.error("Biome ID %d is null. Auto-registering default texture set", new Object[]{biomeID});
                this.setTexture(biomeID, defaultTexture);
            } else {
                if (biome.equals(BiomeGenBase.swampland)) {
                    this.setTexture(biomeID, TextureSet.SWAMP);
                } else if (!biome.equals(BiomeGenBase.river) && !biome.equals(BiomeGenBase.ocean)) {
                    if (biome.equals(BiomeGenBase.beach)) {
                        this.setTexture(biomeID, TextureSet.SHORE);
                    } else if (!biome.equals(BiomeGenBase.jungle) && !biome.equals(BiomeGenBase.jungleHills)) {
                        if (!biome.equals(BiomeGenBase.forest) && !biome.equals(BiomeGenBase.forestHills)) {
                            if (!biome.equals(BiomeGenBase.plains) && !biome.equals(BiomeGenBase.desert)) {
                                if (!biome.equals(BiomeGenBase.extremeHills) && !biome.equals(BiomeGenBase.iceMountains)) {
                                    this.setTexture(biomeID, defaultTexture);
                                } else {
                                    this.setTexture(biomeID, TextureSet.MOUNTAINS_NAKED);
                                }
                            } else {
                                this.setTexture(biomeID, TextureSet.PLAINS);
                            }
                        } else {
                            this.setTexture(biomeID, TextureSet.DENSE_FOREST);
                        }
                    } else {
                        this.setTexture(biomeID, TextureSet.JUNGLE);
                    }
                } else {
                    this.setTexture(biomeID, TextureSet.WATER);
                }

                Log.info("Auto-registered standard texture set for biome %d", new Object[]{biomeID});
            }
        } else {
            Log.error("Biome ID %d is out of range. Auto-registering default texture set", new Object[]{biomeID});
            this.setTexture(biomeID, defaultTexture);
        }
    }

    public void checkRegistration(int biomeID) {
        if (!this.isRegistered(biomeID)) {
            this.autoRegister(biomeID);
            this.markDirty();
        }

    }

    public boolean isRegistered(int biomeID) {
        return this.textureMap.containsKey(biomeID);
    }

    public int getVariations(int biomeID) {
        this.checkRegistration(biomeID);
        TextureSet set = (TextureSet)this.textureMap.get(biomeID);
        return set.textures.length;
    }

    public TextureSet getTextureSet(Tile tile) {
        if (tile == null) {
            return defaultTexture;
        } else {
            this.checkRegistration(tile.biomeID);
            return (TextureSet)this.textureMap.get(tile.biomeID);
        }
    }

    public ResourceLocation getTexture(Tile tile) {
        this.checkRegistration(tile.biomeID);
        TextureSet set = (TextureSet)this.textureMap.get(tile.biomeID);
        int i = MathHelper.floor_float((float)tile.getVariationNumber() / 32767.0F * (float)set.textures.length);
        return set.textures[i];
    }

    public List<ResourceLocation> getAllTextures() {
        List<ResourceLocation> list = new ArrayList(this.textureMap.size());

        for(Map.Entry<Integer, TextureSet> entry : this.textureMap.entrySet()) {
            list.addAll(Arrays.asList(((TextureSet)entry.getValue()).textures));
        }

        return list;
    }

    static {
        defaultTexture = TextureSet.PLAINS;
    }
}
