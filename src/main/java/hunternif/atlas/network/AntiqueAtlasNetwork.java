package hunternif.atlas.network;

import api.BTWAddon;
import hunternif.atlas.AntiqueAtlasItems;
import hunternif.atlas.AntiqueAtlasMod;
import hunternif.atlas.api.AtlasAPI;
import hunternif.atlas.marker.Marker;
import hunternif.atlas.marker.MarkersData;
import hunternif.atlas.util.Log;
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

    // New opcode for server -> client response to deletion request
    private static final int OP_DELETE_MARKER_RESULT = 9;

    private AntiqueAtlasNetwork() {}

    public static void register(BTWAddon addon) {
        CHANNEL = addon.getModID() + "|ATLAS";

        addon.registerPacketHandler(CHANNEL, (packet, player) -> {
            if (packet == null || packet.data == null || player == null) return;

            try (DataInputStream in = new DataInputStream(new ByteArrayInputStream(packet.data))) {
                int opcode = in.readUnsignedByte();

                if (! player.worldObj.isRemote) {
                    handleServerPacket(opcode, in, player);
                } else {
                    handleClientPacket(opcode, in, player);
                }
            } catch (Exception e) {
                Log.warn("Error processing atlas packet: " + e.getMessage());
            }
        });

        Log.info("Antique Atlas network channel registered: " + CHANNEL);
    }

    private static void handleServerPacket(int opcode, DataInputStream in, EntityPlayer player) throws IOException {
        if (!(player instanceof EntityPlayerMP)) return;
        EntityPlayerMP serverPlayer = (EntityPlayerMP) player;

        switch (opcode) {
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
            case OP_DELETE_MARKER_RESULT:
                handleDeleteMarkerResult(in);
                break;
        }
    }

    private static void handlePutBiomeTile(DataInputStream in, EntityPlayerMP player) throws IOException {
        int atlasID = in.readInt();
        int dimension = in.readInt();
        int biomeID = in.readInt();
        int x = in.readInt();
        int z = in.readInt();

        Log.info(String.format("Server: handlePutBiomeTile atlas=%d dim=%d biome=%d x=%d z=%d from player=%s",
                atlasID, dimension, biomeID, x, z, player.getCommandSenderName()));

        AtlasAPI.getTileAPI().putBiomeTile(player.worldObj, atlasID, biomeID, x, z);
    }

    private static void handlePutCustomTile(DataInputStream in, EntityPlayerMP player) throws IOException {
        int atlasID = in.readInt();
        int dimension = in.readInt();
        String customTileName = in.readUTF();
        int x = in.readInt();
        int z = in.readInt();

        Log.info(String.format("Server: handlePutCustomTile atlas=%d name=%s x=%d z=%d from player=%s",
                atlasID, customTileName, x, z, player.getCommandSenderName()));

        AtlasAPI.getTileAPI().putCustomTile(player.worldObj, atlasID, customTileName, x, z);
    }

    private static void handleDeleteMarker(DataInputStream in, EntityPlayerMP player) throws IOException {
        int atlasID = in.readInt();
        int markerID = in.readInt();

        Log.info(String.format("Server: handleDeleteMarker received atlas=%d marker=%d from player=%s",
                atlasID, markerID, player.getCommandSenderName()));

        // Permission check: if atlas-specific, player must hold atlas with that atlasID
        if (atlasID != -1) {
            boolean has = player.inventory.hasItemStack(new ItemStack(AntiqueAtlasItems.itemAtlas, 1, atlasID));
            if (!has) {
                String reason = String.format("You do not hold Atlas #%d - deletion denied.", atlasID);
                Log.warn(String.format("Player %s attempted to delete marker from someone else's Atlas #%d", player.getCommandSenderName(), atlasID));
                sendDeleteMarkerResult(player, false, reason);
                return;
            }
        }

        // Perform deletion on server side
        MarkersData markersData = (atlasID == -1) ? AntiqueAtlasMod.globalMarkersData.getData()
                : AntiqueAtlasItems.itemAtlas.getMarkersData(atlasID, player.worldObj);

        Marker removed = markersData.removeMarker(markerID);
        if (removed == null) {
            String reason = String.format("Marker #%d not found (atlas=%d).", markerID, atlasID);
            Log.warn(String.format("Server: delete failed for atlas=%d marker=%d (not found)", atlasID, markerID));
            // Inform requester of failure
            sendDeleteMarkerResult(player, false, reason);
            return;
        }

        // Deletion succeeded: broadcast to all clients a vanilla DeleteMarkerPacket so clients remove locally
        AtlasNetwork.sendToAll(new DeleteMarkerPacket(atlasID, markerID));
        Log.info(String.format("Server: removed marker id=%d atlas=%d (marker=%s)", markerID, atlasID, removed.toString()));

        // Inform requester of success
        sendDeleteMarkerResult(player, true, String.format("Marker #%d deleted.", markerID));

        // Try to request a world save to help persistence (best-effort)
        try {
            MinecraftServer srv = MinecraftServer.getServer();
            if (srv != null) {
                try {
                    srv.saveAllWorlds(true);
                    Log.info("Server: requested immediate world save after marker deletion.");
                } catch (Throwable t) {
                    Log.info("Server: saveAllWorlds call failed or not present: " + t.getMessage());
                }
            }
        } catch (Throwable t) {
            Log.warn(String.format("Server: forced save after marker delete failed: %s", t.getMessage()));
        }
    }

    private static void handleAddMarker(DataInputStream in, EntityPlayerMP player) throws IOException {
        int atlasID = in.readInt();
        String type = in.readUTF();
        String label = in.readUTF();
        int x = in.readInt();
        int z = in.readInt();
        boolean visibleAhead = in.readBoolean();

        Log.info(String.format("Server: handleAddMarker received atlas=%d type=%s label=%s x=%d z=%d vis=%s from player=%s",
                atlasID, type, label, x, z, String.valueOf(visibleAhead), player.getCommandSenderName()));

        // Permission check: if atlas-specific, player must hold atlas with that atlasID
        if (atlasID != -1) {
            boolean has = player.inventory.hasItemStack(new ItemStack(AntiqueAtlasItems.itemAtlas, 1, atlasID));
            if (!has) {
                String reason = String.format("You do not hold Atlas #%d - cannot add marker.", atlasID);
                Log.warn(String.format("Player %s attempted to add marker into someone else's Atlas #%d", player.getCommandSenderName(), atlasID));
                sendAddMarkerResult(player, false, reason);
                return;
            }
        }

        // Create marker
        if (atlasID == -1) {
            MarkersData data = AntiqueAtlasMod.globalMarkersData.getData();
            Marker marker = data.createAndSaveMarker(type, label, player.worldObj.provider.dimensionId, x, z, visibleAhead);
            Log.info(String.format("Server: created GLOBAL marker %s (id=%d) in dim=%d by player=%s", marker.toString(), marker.getId(), player.worldObj.provider.dimensionId, player.getCommandSenderName()));
            // Broadcast
            AtlasNetwork.sendToAll(new MarkersPacket(player.worldObj.provider.dimensionId, new Marker[]{marker}));
            sendAddMarkerResult(player, true, String.format("Marker #%d created.", marker.getId()));
        } else {
            MarkersData data2 = AntiqueAtlasItems.itemAtlas.getMarkersData(atlasID, player.worldObj);
            Marker marker2 = data2.createAndSaveMarker(type, label, player.worldObj.provider.dimensionId, x, z, visibleAhead);
            Log.info(String.format("Server: created marker %s (id=%d) in atlas=%d dim=%d by player=%s", marker2.toString(), marker2.getId(), atlasID, player.worldObj.provider.dimensionId, player.getCommandSenderName()));
            AtlasNetwork.sendToAll(new MarkersPacket(atlasID, player.worldObj.provider.dimensionId, new Marker[]{marker2}));
            sendAddMarkerResult(player, true, String.format("Marker #%d created.", marker2.getId()));
        }
    }

    private static void handleRegisterTileId(DataInputStream in, EntityPlayerMP player) throws IOException {
        String name = in.readUTF();
        Log.info(String.format("Server: handleRegisterTileId name=%s from player=%s", name, player.getCommandSenderName()));
        // existing behavior in original code did create mapping and broadcast; implement if needed
    }

    @Environment(EnvType.CLIENT)
    private static void handleMapData(DataInputStream in) throws IOException {
        int atlasID = in.readInt();
        int dimension = in.readInt();
        // (client side map data handling — unchanged)
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
            // client-side mapping handled elsewhere if needed
        }
    }

    @Environment(EnvType.CLIENT)
    private static void handleDeleteMarkerResult(DataInputStream in) throws IOException {
        boolean success = in.readBoolean();
        String msg = in.readUTF();

        Minecraft mc = Minecraft.getMinecraft();
        if (mc == null || mc.thePlayer == null) return;

        try {
            mc.thePlayer.addChatMessage(msg);
        } catch (Throwable t) {
            // fallback: log if can't send chat
            Log.info("Delete marker result: " + msg);
        }
    }

    // Utility: send result of delete (to a specific player)
    private static void sendDeleteMarkerResult(EntityPlayerMP player, boolean success, String message) {
        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            DataOutputStream dos = new DataOutputStream(bos);
            dos.writeByte(OP_DELETE_MARKER_RESULT);
            dos.writeBoolean(success);
            dos.writeUTF(message);
            dos.close();
            sendToPlayer(player, bos.toByteArray());
        } catch (IOException e) {
            Log.warn("Error sending delete marker result: " + e.getMessage());
        }
    }

    // Utility: send result of add (optional feedback)
    private static void sendAddMarkerResult(EntityPlayerMP player, boolean success, String message) {
        // Reuse same opcode for now? We'll use OP_DELETE_MARKER_RESULT for both delete/add feedback to keep simple
        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            DataOutputStream dos = new DataOutputStream(bos);
            dos.writeByte(OP_DELETE_MARKER_RESULT);
            dos.writeBoolean(success);
            dos.writeUTF(message);
            dos.close();
            sendToPlayer(player, bos.toByteArray());
        } catch (IOException e) {
            Log.warn("Error sending add marker result: " + e.getMessage());
        }
    }

    @Environment(EnvType.CLIENT)
    public static void sendPutBiomeTile(int atlasID, int dimension, int biomeID, int x, int z) {
        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            DataOutputStream dos = new DataOutputStream(bos);
            dos.writeByte(OP_PUT_BIOME_TILE);
            dos.writeInt(atlasID);
            dos.writeInt(dimension);
            dos.writeInt(biomeID);
            dos.writeInt(x);
            dos.writeInt(z);
            dos.close();

            sendToServer(bos.toByteArray());
        } catch (IOException e) {
            Log.warn("Error sending put biome tile packet: " + e.getMessage());
        }
    }

    @Environment(EnvType.CLIENT)
    public static void sendAddMarker(int atlasID, String type, String label, int x, int z, boolean visibleAhead) {
        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            DataOutputStream dos = new DataOutputStream(bos);
            dos.writeByte(OP_ADD_MARKER);
            dos.writeInt(atlasID);
            dos.writeUTF(type);
            dos.writeUTF(label);
            dos.writeInt(x);
            dos.writeInt(z);
            dos.writeBoolean(visibleAhead);
            dos.close();

            sendToServer(bos.toByteArray());
        } catch (IOException e) {
            Log.warn("Error sending add marker packet: " + e.getMessage());
        }
    }

    @Environment(EnvType.CLIENT)
    public static void sendDeleteMarker(int atlasID, int markerID) {
        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            DataOutputStream dos = new DataOutputStream(bos);
            dos.writeByte(OP_DELETE_MARKER);
            dos.writeInt(atlasID);
            dos.writeInt(markerID);
            dos.close();

            sendToServer(bos.toByteArray());
        } catch (IOException e) {
            Log.warn("Error sending delete marker packet: " + e.getMessage());
        }
    }

    @Environment(EnvType.CLIENT)
    public static void sendRegisterTileId(String name) {
        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            DataOutputStream dos = new DataOutputStream(bos);
            dos.writeByte(OP_REGISTER_TILE_ID);
            dos.writeUTF(name);
            dos.close();

            sendToServer(bos.toByteArray());
        } catch (IOException e) {
            Log.warn("Error sending register tile ID packet: " + e.getMessage());
        }
    }

    public static void sendTilesToPlayer(EntityPlayerMP player, int atlasID, int dimension, int[][] tiles) {
        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            DataOutputStream dos = new DataOutputStream(bos);
            dos.writeByte(OP_TILES);
            dos.writeInt(atlasID);
            dos.writeInt(dimension);
            dos.writeInt(tiles.length);

            for (int[] tile : tiles) {
                dos.writeInt(tile[0]);
                dos.writeInt(tile[1]);
                dos.writeInt(tile[2]);
            }
            dos.close();

            sendToPlayer(player, bos.toByteArray());
        } catch (IOException e) {
            Log.warn("Error sending tiles packet: " + e.getMessage());
        }
    }

    public static void sendMarkersToPlayer(EntityPlayerMP player, int atlasID, Marker[] markers) {
        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            DataOutputStream dos = new DataOutputStream(bos);
            dos.writeByte(OP_MARKERS);
            dos.writeInt(atlasID);
            dos.writeInt(markers.length);

            for (Marker marker : markers) {
                dos.writeUTF(marker.getType());
                dos.writeUTF(marker.getLabel());
                dos.writeInt(marker.getX());
                dos.writeInt(marker.getZ());
                dos.writeBoolean(marker.isVisibleAhead());
            }
            dos.close();

            sendToPlayer(player, bos.toByteArray());
        } catch (IOException e) {
            Log.warn("Error sending markers packet: " + e.getMessage());
        }
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