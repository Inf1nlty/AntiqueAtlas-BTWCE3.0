package hunternif.atlas.mixin;

import hunternif.atlas.AntiqueAtlasMod;
import net.minecraft.src.ChunkProviderServer;
import net.minecraft.src.IChunkProvider;
import net.minecraft.src.WorldServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ChunkProviderServer.class)
public class MixinChunkProviderServer {

    @Shadow public WorldServer worldObj;

    // 注入到 populate 方法的最后，当区块装饰完成后调用
    @Inject(method = "populate", at = @At("RETURN"))
    private void onPopulateChunkPost(IChunkProvider par1IChunkProvider, int par2, int par3, CallbackInfo ci) {
        // 通知 AntiqueAtlasMod 区块生成完毕，检查村庄
        AntiqueAtlasMod.onPopulateChunkPost(this.worldObj);
    }
}