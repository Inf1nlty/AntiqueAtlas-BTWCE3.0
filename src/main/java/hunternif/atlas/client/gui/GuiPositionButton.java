package hunternif.atlas.client.gui;

import hunternif.atlas.client.Textures;
import hunternif.atlas.client.gui.core.GuiComponentButton;
import hunternif.atlas.util.AtlasRenderHelper;
import java.util.Collections;

import net.minecraft.src.Minecraft;
import net.minecraft.src.RenderHelper;
import org.lwjgl.opengl.GL11;

public class GuiPositionButton extends GuiComponentButton {
    public static final int WIDTH = 11;
    public static final int HEIGHT = 11;

    public GuiPositionButton() {
        this.setSize(11, 11);
    }

    public void drawScreen(int mouseX, int mouseY, float partialTick) {
        if (this.isEnabled()) {
            RenderHelper.disableStandardItemLighting();
            GL11.glEnable(3042);
            GL11.glBlendFunc(770, 771);
            int x = this.getGuiX();
            int y = this.getGuiY();
            if (this.isMouseOver) {
                GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            } else {
                GL11.glColor4f(1.0F, 1.0F, 1.0F, 0.5F);
            }

            AtlasRenderHelper.drawFullTexture(Textures.BTN_POSITION, (double)x, (double)y, 11, 11);
            GL11.glDisable(3042);
            if (this.isMouseOver) {
                this.drawTooltip(Collections.singletonList("跟随该玩家"), Minecraft.getMinecraft().fontRenderer);
            }
        }

    }
}
