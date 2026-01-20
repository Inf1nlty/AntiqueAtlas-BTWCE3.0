package hunternif.atlas.network;

import net.minecraft.server.MinecraftServer;
import net. minecraft.src.EntityPlayerMP;
import net.minecraft.src. Minecraft;
import net.minecraft.src. Packet;

public class AtlasNetwork {

    public static void sendTo(Packet packet, EntityPlayerMP player) {
        if (player != null && player.playerNetServerHandler != null) {
            player.playerNetServerHandler.sendPacketToPlayer(packet);
        }
    }

    public static void sendToAll(Packet packet) {
        MinecraftServer server = MinecraftServer.getServer();
        if (server != null && server.getConfigurationManager() != null) {
            server. getConfigurationManager().sendPacketToAllPlayers(packet);
        }
    }

    public static void sendToServer(Packet packet) {
        Minecraft mc = Minecraft.getMinecraft();
        if (mc != null && mc.thePlayer != null && mc.thePlayer.sendQueue != null) {
            mc.thePlayer.sendQueue.addToSendQueue(packet);
        }
    }
}