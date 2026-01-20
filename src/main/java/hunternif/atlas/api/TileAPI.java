package hunternif.atlas.api;

import hunternif.atlas.client.TextureSet;
import net.minecraft.src.BiomeGenBase;
import net.minecraft.src.ResourceLocation;
import net.minecraft.src.World;

public interface TileAPI {
    int VERSION = 4;

    TextureSet registerTextureSet(String var1, ResourceLocation... var2);

    void setBiomeTexture(int var1, String var2, ResourceLocation... var3);

    void setBiomeTexture(BiomeGenBase var1, String var2, ResourceLocation... var3);

    void setBiomeTexture(int var1, TextureSet var2);

    void setBiomeTexture(BiomeGenBase var1, TextureSet var2);

    void setCustomTileTexture(String var1, ResourceLocation... var2);

    void setCustomTileTexture(String var1, TextureSet var2);

    void putBiomeTile(World var1, int var2, int var3, int var4, int var5);

    void putBiomeTile(World var1, int var2, BiomeGenBase var3, int var4, int var5);

    void putCustomTile(World var1, int var2, String var3, int var4, int var5);

    void putCustomGlobalTile(World var1, String var2, int var3, int var4);
}
