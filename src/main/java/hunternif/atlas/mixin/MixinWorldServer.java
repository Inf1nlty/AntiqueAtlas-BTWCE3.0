package hunternif.atlas.mixin;

import hunternif.atlas.AntiqueAtlasMod;
import net.minecraft.src.WorldServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(WorldServer.class)
public class MixinWorldServer {

    @Inject(method = "tick", at = @At("RETURN"))
    private void onTick(CallbackInfo ci) {
        WorldServer world = (WorldServer) (Object) this;

        if (!world.isRemote) {
            // 每 20 tick (1秒) 扫描一次
            if (world.getTotalWorldTime() % 20 == 0) {
                if (AntiqueAtlasMod.villageWatcher != null) {
                    // 这会触发我们新写的 scanSurroundingChunks
                    AntiqueAtlasMod.villageWatcher.visitAllUnvisitedVillages(world);
                }
            }
        }
    }
}