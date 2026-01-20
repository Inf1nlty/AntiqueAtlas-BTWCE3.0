package hunternif.atlas.api;

import net.minecraft.src.ResourceLocation;
import net.minecraft.src.World;

public interface MarkerAPI {
    int VERSION = 3;

    void setTexture(String var1, ResourceLocation var2);

    void putMarker(World var1, boolean var2, int var3, String var4, String var5, int var6, int var7);

    void putGlobalMarker(World var1, boolean var2, String var3, String var4, int var5, int var6);

    void deleteMarker(World var1, int var2, int var3);

    void deleteGlobalMarker(World var1, int var2);
}
