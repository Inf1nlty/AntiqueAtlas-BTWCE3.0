package hunternif.atlas.api.impl;

import hunternif.atlas.AntiqueAtlasItems;
import hunternif.atlas.AntiqueAtlasMod;
import hunternif.atlas.api.TileAPI;
import hunternif.atlas.client.BiomeTextureMap;
import hunternif.atlas.client.TextureSet;
import hunternif.atlas.client.TextureSetMap;
import hunternif.atlas.core.AtlasData;
import hunternif.atlas.core.Tile;
import hunternif.atlas.ext.ExtBiomeData;
import hunternif.atlas.ext.ExtTileIdMap;
import hunternif.atlas.network.AtlasNetwork;
import hunternif.atlas.network.PutBiomeTilePacket;
import hunternif.atlas.network.TileNameIDPacket;
import hunternif.atlas.network.TilesPacket;
import hunternif.atlas.network.RegisterTileIdPacket;
import hunternif.atlas.util.Log;
import net.minecraft.src.*;

import java.util.HashMap;
import java.util.Map;

public class TileApiImpl implements TileAPI {
    private final Map<String, TextureSet> pendingTextures = new HashMap();
    private final Map<String, TileData> pendingTiles = new HashMap();

    public TextureSet registerTextureSet(String name, ResourceLocation... textures) {
        TextureSet textureSet = new TextureSet(name, textures);
        TextureSetMap.instance().register(textureSet);
        return textureSet;
    }

    public void setBiomeTexture(int biomeID, String textureSetName, ResourceLocation... textures) {
        TextureSet textureSet = new TextureSet(textureSetName, textures);
        TextureSetMap.instance().register(textureSet);
        this.setBiomeTexture(biomeID, textureSet);
    }

    public void setBiomeTexture(BiomeGenBase biome, String textureSetName, ResourceLocation... textures) {
        this.setBiomeTexture(biome.biomeID, textureSetName, textures);
    }

    public void setBiomeTexture(int biomeID, TextureSet textureSet) {
        BiomeTextureMap.instance().setTexture(biomeID, textureSet);
    }

    public void setBiomeTexture(BiomeGenBase biome, TextureSet textureSet) {
        this.setBiomeTexture(biome.biomeID, textureSet);
    }

    public void setCustomTileTexture(String uniqueTileName, ResourceLocation... textures) {
        TextureSet set = new TextureSet(uniqueTileName, textures);
        TextureSetMap.instance().register(set);
        this.setCustomTileTexture(uniqueTileName, set);
    }

    public void setCustomTileTexture(String uniqueTileName, TextureSet textureSet) {
        int id = ExtTileIdMap.instance().getPseudoBiomeID(uniqueTileName);
        if (id != -1) {
            BiomeTextureMap.instance().setTexture(id, textureSet);
        } else {
            this.pendingTextures.put(uniqueTileName, textureSet);
            AtlasNetwork.sendToServer(new RegisterTileIdPacket(uniqueTileName));
        }

    }

    public void putBiomeTile(World world, int atlasID, int biomeID, int chunkX, int chunkZ) {
        int dimension = world.provider.dimensionId;
        PutBiomeTilePacket packet = new PutBiomeTilePacket(atlasID, dimension, chunkX, chunkZ, biomeID);
        if (world.isRemote) {
            AtlasNetwork.sendToServer(packet);
        } else {
            AtlasData data = AntiqueAtlasItems.itemAtlas.getAtlasData(atlasID, world);
            Tile tile = new Tile(biomeID);
            data.setTile(dimension, chunkX, chunkZ, tile);
            data.sendPacketToSyncPlayer(packet);
        }
    }

    public void putBiomeTile(World world, int atlasID, BiomeGenBase biome, int chunkX, int chunkZ) {
        this.putBiomeTile(world, atlasID, biome.biomeID, chunkX, chunkZ);
    }

    public void putCustomTile(World world, int atlasID, String tileName, int chunkX, int chunkZ) {
        if (world.isRemote) {
            int biomeID = ExtTileIdMap.instance().getPseudoBiomeID(tileName);
            if (biomeID != -1) {
                this.putBiomeTile(world, atlasID, biomeID, chunkX, chunkZ);
            } else {
                this.pendingTiles.put(tileName, new TileData(world, atlasID, chunkX, chunkZ));
                AtlasNetwork.sendToServer(new RegisterTileIdPacket(tileName));
            }
        } else {
            int biomeID2 = ExtTileIdMap.instance().getPseudoBiomeID(tileName);
            if (biomeID2 == -1) {
                biomeID2 = ExtTileIdMap.instance().getOrCreatePseudoBiomeID(tileName);
                TileNameIDPacket packet = new TileNameIDPacket();
                packet.put(tileName, biomeID2);
                AtlasNetwork.sendToAll(packet);
            }

            this.putBiomeTile(world, atlasID, biomeID2, chunkX, chunkZ);
        }
    }

    public void putCustomGlobalTile(World world, String tileName, int chunkX, int chunkZ) {
        if (world.isRemote) {
            Log.warn("Client tried to put global tile", new Object[0]);
        } else {
            boolean isIdRegistered = ExtTileIdMap.instance().getPseudoBiomeID(tileName) != -1;
            int biomeID = ExtTileIdMap.instance().getOrCreatePseudoBiomeID(tileName);
            ExtBiomeData data = AntiqueAtlasMod.extBiomeData.getData();
            data.setBiomeIdAt(world.provider.dimensionId, chunkX, chunkZ, biomeID);
            if (!isIdRegistered) {
                TileNameIDPacket packet = new TileNameIDPacket();
                packet.put(tileName, biomeID);
                AtlasNetwork.sendToAll(packet);
            }

            TilesPacket packet2 = new TilesPacket(world.provider.dimensionId);
            packet2.addTile(chunkX, chunkZ, biomeID);
            AtlasNetwork.sendToAll(packet2);
        }
    }

    public void onTileIdRegistered(Map<String, Integer> nameToIdMap) {
        for(Map.Entry<String, Integer> entry : nameToIdMap.entrySet()) {
            TextureSet texture = (TextureSet)this.pendingTextures.remove(entry.getKey());
            if (texture != null) {
                BiomeTextureMap.instance().setTexture((Integer)entry.getValue(), texture);
            }

            TileData tile = (TileData)this.pendingTiles.remove(entry.getKey());
            if (tile != null) {
                this.putBiomeTile(tile.world, tile.atlasID, (Integer)entry.getValue(), tile.x, tile.z);
            }
        }

    }

    private static class TileData {
        World world;
        int atlasID;
        int x;
        int z;

        TileData(World world, int atlasID, int x, int z) {
            this.world = world;
            this.atlasID = atlasID;
            this.x = x;
            this.z = z;
        }
    }
}
