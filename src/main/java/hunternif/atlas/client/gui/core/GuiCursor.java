package hunternif.atlas.client.gui.core;

import hunternif.atlas.util.AtlasRenderHelper;
import net.minecraft.src.ResourceLocation;

public class GuiCursor extends GuiComponent {
    private ResourceLocation texture;
    private int textureWidth;
    private int textureHeight;
    private int pointX;
    private int pointY;

    public void setTexture(ResourceLocation texture, int width, int height, int pointX, int pointY) {
        this.texture = texture;
        this.textureWidth = width;
        this.textureHeight = height;
        this.pointX = pointX;
        this.pointY = pointY;
    }

    public int getWidth() {
        return 0;
    }

    public int getHeight() {
        return 0;
    }

    public void drawScreen(int mouseX, int mouseY, float partialTick) {
        AtlasRenderHelper.drawFullTexture(this.texture, (double)(mouseX - this.pointX), (double)(mouseY - this.pointY), this.textureWidth, this.textureHeight);
    }
}
