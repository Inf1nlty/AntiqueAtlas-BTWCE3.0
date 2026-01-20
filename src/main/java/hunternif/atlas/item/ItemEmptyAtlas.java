package hunternif.atlas.item;

import hunternif.atlas.AntiqueAtlasItems;
import hunternif.atlas.core.AtlasData;
import hunternif.atlas.marker.MarkersData;
import net.minecraft.src.*;

public class ItemEmptyAtlas extends Item {

    public ItemEmptyAtlas(int id) {
        super(id);
        this.setMaxStackSize(1);
        this.setCreativeTab(CreativeTabs.tabTools);
        this.setTextureName("antiqueatlas:emptyAntiqueAtlas");
        this.setUnlocalizedName("emptyAntiqueAtlas");
    }

    @Override
    public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {
        if (world.isRemote) {
            return stack;
        }

        // Check atlas limit
        int currentAtlasCount = getAtlasCount(world);
        if (currentAtlasCount >= 32000) {
            return stack;
        }

        // Get unique atlas ID
        int atlasID = world.getUniqueDataId("aAtlas");

        // Create atlas item stack
        ItemStack atlasStack = new ItemStack(AntiqueAtlasItems.itemAtlas, 1, atlasID);

        // Create and save atlas data
        String atlasKey = AntiqueAtlasItems.itemAtlas.getAtlasDataKey(atlasID);
        AtlasData atlasData = new AtlasData(atlasKey);
        world.setItemData(atlasKey, atlasData);

        // Create and save markers data
        String markersKey = AntiqueAtlasItems.itemAtlas. getMarkersDataKey(atlasID);
        MarkersData markersData = new MarkersData(markersKey);
        world.setItemData(markersKey, markersData);

        // Replace empty atlas with new atlas
        stack.stackSize--;
        if (stack.stackSize <= 0) {
            return atlasStack;
        } else {
            if (! player.inventory.addItemStackToInventory(atlasStack)) {
                player.dropPlayerItem(atlasStack);
            }
            return stack;
        }
    }

    private int getAtlasCount(World world) {
        for (int i = 0; i < 32000; i++) {
            String key = "aAtlas_" + i;
            if (world.loadItemData(AtlasData.class, key) == null) {
                return i;
            }
        }
        return 32000;
    }
}