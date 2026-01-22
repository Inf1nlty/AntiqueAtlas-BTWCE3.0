package hunternif.atlas. item;

import hunternif.atlas.AntiqueAtlasItems;
import hunternif.atlas.AntiqueAtlasMod;
import hunternif.atlas.core.AtlasData;
import hunternif.atlas.core.ChunkBiomeAnalyzer;
import hunternif.atlas.core.ITileStorage;
import hunternif.atlas.core.Tile;
import hunternif.atlas.marker. MarkersData;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.src.*;

import java.util.List;

public class ItemAtlas extends Item {

    public static final String ATLAS_DATA_PREFIX = "aAtlas_";
    public static final String WORLD_ATLAS_DATA_ID = "aAtlas";
    public static final String MARKERS_DATA_PREFIX = "aaMarkers_";
    public static double LOOK_RADIUS = 11.0F;
    public static int UPDATE_INTERVAL = 20;

    public ItemAtlas(int id) {
        super(id);
        this.setMaxStackSize(1);
        this.setCreativeTab(CreativeTabs.tabTools);
        this.setTextureName("antiqueatlas:antiqueAtlas");
        this.setUnlocalizedName("antiqueAtlas");
    }

    @Override
    @Environment(EnvType.CLIENT)
    public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {
        if (world.isRemote) {
            AntiqueAtlasMod. openAtlasGUI(stack);
        }
        return stack;
    }

    @Override
    public void onUpdate(ItemStack stack, World world, EntityPlayer player, int slot, boolean isCurrentItem) {
        AtlasData data = this.getAtlasData(stack, world);

        if (data == null) {
            return;
        }

        if (! world.isRemote && ! data.isSyncedOnPlayer(player) && !data.isEmpty()) {
            data.syncOnPlayer(getAtlasID(stack), player);
        }

        MarkersData markers = this.getMarkersData(stack, world);
        if (!world.isRemote && markers != null && !markers.isSyncedOnPlayer(player) && !markers.isEmpty()) {
            markers.syncOnPlayer(getAtlasID(stack), player);
        }

        ChunkBiomeAnalyzer biomeAnalyzer = ChunkBiomeAnalyzer. instance;
        if (player. ticksExisted % UPDATE_INTERVAL == 0 && biomeAnalyzer != null) {
            int playerX = MathHelper.floor_double(player.posX) >> 4;
            int playerZ = MathHelper.floor_double(player.posZ) >> 4;
            ITileStorage seenChunks = data.getDimensionData(player.dimension);

            for (double dx = -LOOK_RADIUS; dx <= LOOK_RADIUS; ++dx) {
                for (double dz = -LOOK_RADIUS; dz <= LOOK_RADIUS; ++dz) {
                    if (dx * dx + dz * dz <= LOOK_RADIUS * LOOK_RADIUS) {
                        int x = (int) ((double) playerX + dx);
                        int y = (int) ((double) playerZ + dz);

                        int biomeId2 = AntiqueAtlasMod.extBiomeData. getData().getBiomeIdAt(player.dimension, x, y);

                        if (biomeId2 == -1) {
                            if (! seenChunks.hasTileAt(x, y) && player.worldObj.blockExists(x << 4, 0, y << 4)) {
                                Chunk chunk = player.worldObj.getChunkFromChunkCoords(x, y);
                                int biomeId;

                                if (! chunk.isChunkLoaded) {
                                    biomeId = -1;
                                } else {
                                    biomeId = biomeAnalyzer.getMeanBiomeID(chunk);
                                }

                                if (biomeId != -1) {
                                    data.setTile(player.dimension, x, y, new Tile(biomeId));
                                }
                            }
                        } else {
                            Tile tile = new Tile(biomeId2);
                            if (world.isRemote) {
                                tile.randomizeTexture();
                            }
                            data.setTile(player.dimension, x, y, tile);
                        }
                    }
                }
            }
        }
    }

    public static int getAtlasID(ItemStack stack) {
        return stack.getItemDamage();
    }

    public static void setAtlasID(ItemStack stack, int atlasID) {
        stack.setItemDamage(atlasID);
    }

    @Override
    public String getItemDisplayName(ItemStack stack) {
        int atlasID = stack.getItemDamage();
        return super.getItemDisplayName(stack) + " #" + atlasID;
    }

    public AtlasData getAtlasData(ItemStack stack, World world) {
        if (stack.getItem() != AntiqueAtlasItems.itemAtlas) {
            return null;
        }
        return this.getAtlasData(getAtlasID(stack), world);
    }

    public AtlasData getAtlasData(int atlasID, World world) {
        String key = this.getAtlasDataKey(atlasID);
        AtlasData data = (AtlasData) world.loadItemData(AtlasData.class, key);

        if (data == null) {
            data = new AtlasData(key);
            world.setItemData(key, data);
        }

        return data;
    }

    public String getAtlasDataKey(int atlasID) {
        return ATLAS_DATA_PREFIX + atlasID;
    }

    public MarkersData getMarkersData(ItemStack stack, World world) {
        if (stack.getItem() != AntiqueAtlasItems.itemAtlas) {
            return null;
        }
        return this. getMarkersData(getAtlasID(stack), world);
    }

    public MarkersData getMarkersData(int atlasID, World world) {
        String key = this.getMarkersDataKey(atlasID);
        MarkersData data = (MarkersData) world.loadItemData(MarkersData.class, key);

        if (data == null) {
            data = new MarkersData(key);
            world.setItemData(key, data);
        }

        return data;
    }

    public String getMarkersDataKey(int atlasID) {
        return MARKERS_DATA_PREFIX + atlasID;
    }
}