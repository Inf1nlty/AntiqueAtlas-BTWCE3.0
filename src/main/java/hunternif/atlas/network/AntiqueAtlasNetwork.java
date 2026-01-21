package hunternif.atlas.network;

import api.BTWAddon;
import hunternif.atlas.AntiqueAtlasItems;
import hunternif.atlas.AntiqueAtlasMod;
import hunternif.atlas.api.AtlasAPI;
import hunternif.atlas.marker.Marker;
import hunternif.atlas.marker.MarkersData;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.server.MinecraftServer;
import net.minecraft.src.*;

import java.io.*;

public class AntiqueAtlasNetwork {

    public static String CHANNEL;

    private static final int OP_MAP_DATA = 0;
    private static final int OP_TILES = 1;
    private static final int OP_MARKERS = 2;
    private static final int OP_PUT_BIOME_TILE = 3;
    private static final int OP_TILE_NAME_ID = 4;
    private static final int OP_DELETE_MARKER = 5;
    private static final int OP_ADD_MARKER = 6;
    private static final int OP_REGISTER_TILE_ID = 7;
    private static final int OP_PUT_CUSTOM_TILE = 8;

    private AntiqueAtlasNetwork() {}

    public static void register(BTWAddon addon) {
        CHANNEL = addon.getModID() + "|ATLAS";
        addon.registerPacketHandler(CHANNEL, (packet, player) -> {
            if (packet == null || packet.data == null || player == null) return;
            try (DataInputStream in = new DataInputStream(new ByteArrayInputStream(packet.data))) {
                int opcode = in.readUnsignedByte();
                if (!player.worldObj.isRemote) {
                    handleServerPacket(opcode, in, player);
                } else {
                    handleClientPacket(opcode, in, player);
                }
            } catch (Exception e) {
            }
        });
    }

    private static void handleServerPacket(int opcode, DataInputStream in, EntityPlayer player) throws IOException {
        if (!(player instanceof EntityPlayerMP)) return;
        EntityPlayerMP serverPlayer = (EntityPlayerMP) player;
        switch (opcode) {
            case OP_MAP_DATA:
                break;
            case OP_TILES:
                handlePutBiomeTile(in, serverPlayer);
                break;
            case OP_MARKERS:
                break;
            case OP_PUT_BIOME_TILE:
                handlePutBiomeTile(in, serverPlayer);
                break;
            case OP_DELETE_MARKER:
                handleDeleteMarker(in, serverPlayer);
                break;
            case OP_ADD_MARKER:
                handleAddMarker(in, serverPlayer);
                break;
            case OP_REGISTER_TILE_ID:
                handleRegisterTileId(in, serverPlayer);
                break;
            case OP_PUT_CUSTOM_TILE:
                handlePutCustomTile(in, serverPlayer);
                break;
        }
    }

    @Environment(EnvType.CLIENT)
    private static void handleClientPacket(int opcode, DataInputStream in, EntityPlayer player) throws IOException {
        switch (opcode) {
            case OP_MAP_DATA:
                handleMapData(in);
                break;
            case OP_TILES:
                handleTiles(in);
                break;
            case OP_MARKERS:
                handleMarkers(in);
                break;
            case OP_TILE_NAME_ID:
                handleTileNameID(in);
                break;
        }
    }

    private static void handlePutBiomeTile(DataInputStream in, EntityPlayerMP player) throws IOException {
        int atlasID = in.readInt();
        int dimension = in.readInt();
        int biomeID = in.readInt();
        int x = in.readInt();
        int z = in.readInt();
        AtlasAPI.getTileAPI().putBiomeTile(player.worldObj, atlasID, biomeID, x, z);
    }

    private static void handlePutCustomTile(DataInputStream in, EntityPlayerMP player) throws IOException {
        int atlasID = in.readInt();
        int dimension = in.readInt();
        String customTileName = in.readUTF();
        int x = in.readInt();
        int z = in.readInt();
        AtlasAPI.getTileAPI().putCustomTile(player.worldObj, atlasID, customTileName, x, z);
    }

    private static void handleDeleteMarker(DataInputStream in, EntityPlayerMP player) throws IOException {
        int atlasID = in.readInt();
        int markerID = in.readInt();
        AtlasAPI.getMarkerAPI().deleteMarker(player.worldObj, atlasID, markerID);
    }

    private static void handleAddMarker(DataInputStream in, EntityPlayerMP player) throws IOException {
        int atlasID = in.readInt();
        String type = in.readUTF();
        String label = in.readUTF();
        int x = in.readInt();
        int z = in.readInt();
        boolean visibleAhead = in.readBoolean();
        AtlasAPI.getMarkerAPI().putMarker(player.worldObj, visibleAhead, atlasID, type, label, x, z);
    }

    private static void handleRegisterTileId(DataInputStream in, EntityPlayerMP player) throws IOException {
        String name = in.readUTF();
    }

    @Environment(EnvType.CLIENT)
    private static void handleMapData(DataInputStream in) throws IOException {
        int atlasID = in.readInt();
        int dimension = in.readInt();
    }

    @Environment(EnvType.CLIENT)
    private static void handleTiles(DataInputStream in) throws IOException {
        int atlasID = in.readInt();
        int dimension = in.readInt();
        int count = in.readInt();
        Minecraft mc = Minecraft.getMinecraft();
        if (mc == null || mc.theWorld == null) return;
        for (int i = 0; i < count; i++) {
            int biomeID = in.readInt();
            int x = in.readInt();
            int z = in.readInt();
            AtlasAPI.getTileAPI().putBiomeTile(mc.theWorld, atlasID, biomeID, x, z);
        }
    }

    @Environment(EnvType.CLIENT)
    private static void handleMarkers(DataInputStream in) throws IOException {
        int atlasID = in.readInt();
        int count = in.readInt();
        Minecraft mc = Minecraft.getMinecraft();
        if (mc == null || mc.theWorld == null) return;
        for (int i = 0; i < count; i++) {
            String type = in.readUTF();
            String label = in.readUTF();
            int x = in.readInt();
            int z = in.readInt();
            boolean visibleAhead = in.readBoolean();
            AtlasAPI.getMarkerAPI().putMarker(mc.theWorld, visibleAhead, atlasID, type, label, x, z);
        }
    }

    @Environment(EnvType.CLIENT)
    private static void handleTileNameID(DataInputStream in) throws IOException {
        int count = in.readInt();
        for (int i = 0; i < count; i++) {
            String name = in.readUTF();
            int id = in.readInt();
        }
    }

    @Environment(EnvType.CLIENT)
    public static void sendPutBiomeTile(int atlasID, int dimension, int biomeID, int x, int z) {
        PutBiomeTilePacket pkt = new PutBiomeTilePacket(atlasID, dimension, x, z, biomeID);
        AtlasNetwork.sendToServer(pkt);
    }

    @Environment(EnvType.CLIENT)
    public static void sendAddMarker(int atlasID, String type, String label, int x, int z, boolean visibleAhead) {
        AddMarkerPacket pkt = new AddMarkerPacket(atlasID, Minecraft.getMinecraft().theWorld.provider.dimensionId, type, label, x, z, visibleAhead);
        AtlasNetwork.sendToServer(pkt);
    }

    @Environment(EnvType.CLIENT)
    public static void sendDeleteMarker(int atlasID, int markerID) {
        DeleteMarkerPacket pkt = new DeleteMarkerPacket(atlasID, markerID);
        AtlasNetwork.sendToServer(pkt);
    }

    @Environment(EnvType.CLIENT)
    public static void sendRegisterTileId(String name) {
        RegisterTileIdPacket pkt = new RegisterTileIdPacket(name);
        AtlasNetwork.sendToServer(pkt);
    }

    public static void sendTilesToPlayer(EntityPlayerMP player, int atlasID, int dimension, int[][] tiles) {
        TilesPacket pkt = new TilesPacket(dimension);
        for (int[] t : tiles) {
            pkt.addTile(t[0], t[1], t[2]);
        }
        AtlasNetwork.sendTo(pkt, player);
    }

    public static void sendMarkersToPlayer(EntityPlayerMP player, int atlasID, Marker[] markers) {
        MarkersPacket pkt;
        if (atlasID == -1) pkt = new MarkersPacket(markers.length > 0 ? markers[0].getDimension() : 0, markers);
        else pkt = new MarkersPacket(atlasID, markers.length > 0 ? markers[0].getDimension() : 0, markers);
        AtlasNetwork.sendTo(pkt, player);
    }

    @Environment(EnvType.CLIENT)
    private static void sendToServer(byte[] data) {
        Minecraft mc = Minecraft.getMinecraft();
        if (mc == null || mc.thePlayer == null || mc.thePlayer.sendQueue == null) return;
        Packet250CustomPayload pkt = new Packet250CustomPayload();
        pkt.channel = CHANNEL;
        pkt.data = data;
        pkt.length = data.length;
        mc.thePlayer.sendQueue.addToSendQueue(pkt);
    }

    private static void sendToPlayer(EntityPlayerMP player, byte[] data) {
        if (player == null || player.playerNetServerHandler == null) return;
        Packet250CustomPayload pkt = new Packet250CustomPayload();
        pkt.channel = CHANNEL;
        pkt.data = data;
        pkt.length = data.length;
        player.playerNetServerHandler.sendPacketToPlayer(pkt);
    }

    public static void sendToAll(byte[] data) {
        MinecraftServer server = MinecraftServer.getServer();
        if (server == null || server.getConfigurationManager() == null) return;
        Packet250CustomPayload pkt = new Packet250CustomPayload();
        pkt.channel = CHANNEL;
        pkt.data = data;
        pkt.length = data.length;
        server.getConfigurationManager().sendPacketToAllPlayers(pkt);
    }
}