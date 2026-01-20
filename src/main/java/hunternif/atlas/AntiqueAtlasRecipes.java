package hunternif.atlas;

import btw.crafting.recipe.RecipeManager;
import net.minecraft.src.Item;
import net.minecraft.src.ItemStack;

public class AntiqueAtlasRecipes {

    public static void registerRecipes() {
        addCraftingRecipes();
    }

    private static void addCraftingRecipes() {
        RecipeManager.addShapelessRecipe(
                new ItemStack(AntiqueAtlasItems.emptyAtlas, 1),
                new Object[] {
                        new ItemStack(Item.feather, 1),
                        new ItemStack(Item.dyePowder, 1, 0),
                        new ItemStack(Item.book, 1),
                        new ItemStack(Item.compass, 1)
                }
        );
    }
}