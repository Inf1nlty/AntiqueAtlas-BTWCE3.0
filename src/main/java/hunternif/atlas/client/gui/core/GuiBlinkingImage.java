package hunternif.atlas.client.gui.core;

import hunternif.atlas.util.AtlasRenderHelper;
import net.minecraft.src.ResourceLocation;
import org.lwjgl.opengl.GL11;

public class GuiBlinkingImage extends GuiComponent {
    private ResourceLocation texture;
    private long blinkTime = 500L;
    private float visibleAlpha = 1.0F;
    private float invisibleAlpha = 0.25F;
    private long lastTickTime;
    private boolean isVisible;

    public void setTexture(ResourceLocation texture, int width, int height) {
        this.texture = texture;
        this.setSize(width, height);
        this.lastTickTime = 0L;
        this.isVisible = false;
    }

    public void setBlinkTime(long blinkTime) {
        this.blinkTime = blinkTime;
    }

    public void setVisibleAlpha(float visibleAlpha) {
        this.visibleAlpha = visibleAlpha;
    }

    public void setInvisibleAlpha(float invisibleAlpha) {
        this.invisibleAlpha = invisibleAlpha;
    }

    public void drawScreen(int mouseX, int mouseY, float partialTick) {
        long currentTime = System.currentTimeMillis();
        if (this.lastTickTime + this.blinkTime < currentTime) {
            this.lastTickTime = currentTime;
            this.isVisible = !this.isVisible;
        }

        GL11.glColor4f(1.0F, 1.0F, 1.0F, this.isVisible ? this.visibleAlpha : this.invisibleAlpha);
        GL11.glEnable(3042);
        GL11.glBlendFunc(770, 771);
        this.drawImage();
    }

    protected void drawImage() {
        AtlasRenderHelper.drawFullTexture(this.texture, (double)this.getGuiX(), (double)this.getGuiY(), this.getWidth(), this.getHeight());
    }
}
