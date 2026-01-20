package hunternif.atlas.client.gui;

import net.minecraft.src.FontRenderer;
import net.minecraft.src.Minecraft;
import net.minecraft.src.Tessellator;
import org.lwjgl.opengl.GL11;

public class ProgressBarOverlay implements ExportUpdateListener {
    private final int barWidth;
    private final int barHeight;
    private int completedWidth;
    private String status;
    private final FontRenderer font;

    public ProgressBarOverlay(int barWidth, int barHeight) {
        this.font = Minecraft.getMinecraft().fontRenderer;
        this.barWidth = barWidth;
        this.barHeight = barHeight;
    }

    public void setStatusString(String status) {
        this.status = status;
    }

    public void update(float percentage) {
        if (percentage < 0.0F) {
            percentage = 0.0F;
        }

        if (percentage > 1.0F) {
            percentage = 1.0F;
        }

        this.completedWidth = Math.round(percentage * (float)this.barWidth);
    }

    public void draw(int x, int y) {
        int statusWidth = this.font.getStringWidth(this.status);
        this.font.drawStringWithShadow(this.status, x + (this.barWidth - statusWidth) / 2, y, 16777215);
        int y2 = y + 14;
        GL11.glDisable(3553);
        Tessellator tessellator = Tessellator.instance;
        tessellator.startDrawingQuads();
        tessellator.setColorOpaque_I(8421504);
        tessellator.addVertex((double)x, (double)y2, (double)0.0F);
        tessellator.addVertex((double)x, (double)(y2 + this.barHeight), (double)0.0F);
        tessellator.addVertex((double)(x + this.barWidth), (double)(y2 + this.barHeight), (double)0.0F);
        tessellator.addVertex((double)(x + this.barWidth), (double)y2, (double)0.0F);
        tessellator.setColorOpaque_I(8454016);
        tessellator.addVertex((double)x, (double)y2, (double)0.0F);
        tessellator.addVertex((double)x, (double)(y2 + this.barHeight), (double)0.0F);
        tessellator.addVertex((double)(x + this.completedWidth), (double)(y2 + this.barHeight), (double)0.0F);
        tessellator.addVertex((double)(x + this.completedWidth), (double)y2, (double)0.0F);
        tessellator.draw();
        GL11.glEnable(3553);
    }

    public void reset() {
        this.completedWidth = 0;
    }
}
