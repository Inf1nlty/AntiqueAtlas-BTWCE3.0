package hunternif.atlas.network;

import net.minecraft.src.Packet;

import java.util.HashMap;
import java.util.Map;

public final class AtlasVanillaPacketRegistry {

    private static final Map<Integer, Class<? extends Packet>> idToClassMap = new HashMap<>();
    private static final Map<Class<? extends Packet>, Integer> classToIdMap = new HashMap<>();

    private AtlasVanillaPacketRegistry() {
    }

    public static synchronized void registerAll() {
        register(230, MapDataPacket.class);
        register(231, TilesPacket.class);
        register(232, MarkersPacket.class);
        register(233, PutBiomeTilePacket.class);
        register(234, TileNameIDPacket.class);
        register(235, DeleteMarkerPacket.class);
        register(236, AddMarkerPacket.class);
        register(237, RegisterTileIdPacket.class);
    }

    private static void register(int id, Class<? extends Packet> packetClass) {
        if (idToClassMap.containsKey(id)) {
            throw new IllegalArgumentException("Packet ID " + id + " is already registered!");
        }
        if (classToIdMap.containsKey(packetClass)) {
            throw new IllegalArgumentException("Packet class " + packetClass.getName() + " is already registered!");
        }
        idToClassMap.put(id, packetClass);
        classToIdMap.put(packetClass, id);
        Packet.addIdClassMapping(id, true, true, packetClass);
    }

    public static Class<? extends Packet> getPacketClass(int id) {
        return idToClassMap.get(id);
    }

    public static Integer getPacketId(Class<? extends Packet> packetClass) {
        return classToIdMap.get(packetClass);
    }
}