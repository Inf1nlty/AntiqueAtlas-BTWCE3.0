package hunternif.atlas.network;

import net.minecraft.src.Packet;

import java.lang.reflect.Method;

public final class AtlasVanillaPacketRegistry {
    private static boolean done = false;

    private AtlasVanillaPacketRegistry() {
    }

    public static synchronized void registerAll() {
        if (done) return;
        done = true;

        register(230, MapDataPacket.class);
        register(231, TilesPacket.class);
        register(232, MarkersPacket.class);
        register(233, PutBiomeTilePacket.class);
        register(234, TileNameIDPacket.class);
        register(235, DeleteMarkerPacket.class);
        register(236, AddMarkerPacket.class);
        register(237, RegisterTileIdPacket.class);
    }

    private static void register(int id, Class<? extends Packet> cls) {
        try {
            Method m = Packet.class.getDeclaredMethod("addIdClassMapping", int.class, boolean.class, boolean.class, Class.class);
            m.setAccessible(true);
            m.invoke(null, id, true, true, cls);
        } catch (Throwable t) {
            throw new RuntimeException("Failed to register packet " + cls.getName() + " with id " + id, t);
        }
    }
}