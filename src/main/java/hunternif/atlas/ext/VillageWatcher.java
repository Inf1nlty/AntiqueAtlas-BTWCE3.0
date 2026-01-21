package hunternif.atlas.ext;

import hunternif.atlas.AntiqueAtlasMod;
import hunternif.atlas.api.AtlasAPI;
import hunternif.atlas.marker.Marker;
import hunternif.atlas.marker.MarkersData;
import net.minecraft.src.*;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Set;

public class VillageWatcher {
    private final Set<Village> visited = new HashSet();
    // 用来记录我们已经通过算法计算过的区块，防止重复计算卡顿
    private final Set<Long> scannedChunks = new HashSet();

    public void onWorldLoad(World world) {
        if (!world.isRemote) {
            this.visitAllUnvisitedVillages(world);
        }
    }

    public void onPopulateChunkPost(World world) {
        if (!world.isRemote) {
            this.visitAllUnvisitedVillages(world);
        }
    }

    // 旧方法：扫描活跃村庄（有村民的）
    public void visitAllUnvisitedVillages(World world) {
        // 1. 先尝试获取活跃村庄（原版逻辑）
        VillageCollection villageCollection = world.villageCollectionObj;
        if (villageCollection != null) {
            List villageList = villageCollection.getVillageList();
            if (villageList != null) {
                for(Object o : villageList) {
                    Village village = (Village)o;
                    if (!this.visited.contains(village)) {
                        this.visitVillage(world, village);
                    }
                }
            }
        }

        // 2. 【新增】扫描玩家附近的“僵尸村庄/遗迹”结构
        for (Object p : world.playerEntities) {
            if (p instanceof EntityPlayer) {
                scanSurroundingChunks(world, (EntityPlayer) p);
            }
        }
    }

    // 【新增】核心算法：根据种子计算村庄位置
    public void scanSurroundingChunks(World world, EntityPlayer player) {
        int pChunkX = MathHelper.floor_double(player.posX) >> 4;
        int pChunkZ = MathHelper.floor_double(player.posZ) >> 4;

        // 扫描玩家周围 10x10 区块的范围
        int range = 10;

        for (int x = pChunkX - range; x <= pChunkX + range; x++) {
            for (int z = pChunkZ - range; z <= pChunkZ + range; z++) {
                long chunkKey = ChunkCoordIntPair.chunkXZ2Int(x, z);

                // 如果这个区块还没扫过
                if (!scannedChunks.contains(chunkKey)) {
                    scannedChunks.add(chunkKey);

                    // 运行 Minecraft 村庄生成算法
                    if (isVillageChunk(world, x, z)) {
                        // 找到了！这里应该有个村庄结构
                        int centerX = x * 16 + 8;
                        int centerZ = z * 16 + 8;

                        // 直接添加标记
                        addVillageMarker(world, centerX, centerZ);
                    }
                }
            }
        }
    }

    // 这是一个标准的 Minecraft 1.6.4 村庄生成检测算法
    private boolean isVillageChunk(World world, int chunkX, int chunkZ) {
        int k = 32; // 村庄间距
        int l = 8;  // 最小间距
        int i1 = chunkX;
        int j1 = chunkZ;

        if (chunkX < 0) chunkX -= k - 1;
        if (chunkZ < 0) chunkZ -= k - 1;

        int k1 = chunkX / k;
        int l1 = chunkZ / k;

        // 魔法数字，来自 MapGenVillage
        long i2 = (long)k1 * 341873128712L + (long)l1 * 132897987541L + world.getSeed() + 10387312L;
        Random random = new Random(i2);

        k1 *= k;
        l1 *= k;
        k1 += random.nextInt(k - l);
        l1 += random.nextInt(k - l);

        if (i1 == k1 && j1 == l1) {
            // 坐标匹配，最后检查生物群系是否允许生成村庄
            boolean isViable = world.getWorldChunkManager().areBiomesViable(i1 * 16 + 8, j1 * 16 + 8, 0, MapGenVillage.villageSpawnBiomes);
            return isViable;
        }
        return false;
    }

    // 提取出来的添加标记逻辑
    private void addVillageMarker(World world, int centerX, int centerZ) {
        int dim = world.provider.dimensionId;
        MarkersData markersData = AntiqueAtlasMod.globalMarkersData.getData();
        boolean foundMarker = false;

        // 检查是否已经有标记了
        int chunkX = centerX >> 4;
        int chunkZ = centerZ >> 4;
        List<Marker> markers = markersData.getMarkersAtChunk(dim, chunkX / 4, chunkZ / 4);
        if (markers != null) {
            for (Marker marker : markers) {
                if (marker.getType().equals("village")) {
                    foundMarker = true;
                    break;
                }
            }
        }

        if (!foundMarker) {
            String label = "Village";
            try {
                String translated = I18n.getString("gui.antiqueatlas.marker.village");
                if (translated != null && !translated.isEmpty()) label = translated;
            } catch (Exception e) {}

            System.out.println("[Atlas] Detected Village Structure at: " + centerX + ", " + centerZ);
            AtlasAPI.getMarkerAPI().putGlobalMarker(world, false, "village", label, centerX, centerZ);

            // 顺便把周围染成村庄领地色（大概范围，因为没有具体数据）
            AtlasAPI.getTileAPI().putCustomGlobalTile(world, "npcVillageTerritory", chunkX, chunkZ);
        }
    }

    public void visitVillage(World world, Village village) {
        // 这个方法保留给活跃村庄使用，用来绘制更精确的门和领地
        int centerX = village.getCenter().posX;
        int centerZ = village.getCenter().posZ;
        addVillageMarker(world, centerX, centerZ);

        for(Object o : village.getVillageDoorInfoList()) {
            VillageDoorInfo door = (VillageDoorInfo)o;
            AtlasAPI.getTileAPI().putCustomGlobalTile(world, "npcVillageDoor", door.posX >> 4, door.posZ >> 4);
        }
        this.visited.add(village);
    }
}