package hunternif.atlas.client.gui;

import hunternif.atlas.client.Textures;
import hunternif.atlas.client.gui.core.GuiToggleButton;
import hunternif.atlas.util.AtlasRenderHelper;
import java.util.Arrays;

import net.minecraft.src.Minecraft;
import net.minecraft.src.RenderHelper;
import net.minecraft.src.ResourceLocation;
import org.lwjgl.opengl.GL11;

public class GuiBookmarkButton extends GuiToggleButton {
    private static final int IMAGE_WIDTH = 84;
    private static final int IMAGE_HEIGHT = 36;
    public static final int WIDTH = 21;
    public static final int HEIGHT = 18;
    public static final int ICON_WIDTH = 16;
    public static final int ICON_HEIGHT = 16;
    private final int colorIndex;
    private ResourceLocation iconTexture;
    private String title;

    public GuiBookmarkButton(int colorIndex, ResourceLocation iconTexture, String title) {
        this.colorIndex = colorIndex;
        this.setIconTexture(iconTexture);
        this.setTitle(title);
        this.setSize(21, 18);
    }

    public void setIconTexture(ResourceLocation iconTexture) {
        this.iconTexture = iconTexture;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTitle() {
        return this.title;
    }

    public void drawScreen(int mouseX, int mouseY, float partialTick) {
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        RenderHelper.disableStandardItemLighting();
        int u = this.colorIndex * 21;
        int v = !this.isMouseOver && !this.isSelected() ? 18 : 0;
        AtlasRenderHelper.drawTexturedRect(Textures.BOOKMARKS, (double)this.getGuiX(), (double)this.getGuiY(), u, v, 21, 18, 84, 36);
        AtlasRenderHelper.drawFullTexture(this.iconTexture, (double)(this.getGuiX() + (!this.isMouseOver && !this.isSelected() ? 2 : 3)), (double)(this.getGuiY() + 1), 16, 16);
        if (this.isMouseOver) {
            this.drawTooltip(Arrays.asList(this.title), Minecraft.getMinecraft().fontRenderer);
        }

    }
}
