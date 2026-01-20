package hunternif.atlas.client.gui;

import hunternif.atlas.client.Textures;
import hunternif.atlas.client.gui.core.GuiComponentButton;
import hunternif.atlas.util.AtlasRenderHelper;
import net.minecraft.src.RenderHelper;
import org.lwjgl.opengl.GL11;

public class GuiArrowButton extends GuiComponentButton {
    public static final int WIDTH = 12;
    public static final int HEIGHT = 12;
    private static final int IMAGE_WIDTH = 24;
    private static final int IMAGE_HEIGHT = 24;
    public ArrowDirection direction;

    public GuiArrowButton(ArrowDirection direction) {
        this.setSize(12, 12);
        this.direction = direction;
    }

    public static GuiArrowButton up() {
        return new GuiArrowButton(GuiArrowButton.ArrowDirection.UP);
    }

    public static GuiArrowButton down() {
        return new GuiArrowButton(GuiArrowButton.ArrowDirection.DOWN);
    }

    public static GuiArrowButton left() {
        return new GuiArrowButton(GuiArrowButton.ArrowDirection.LEFT);
    }

    public static GuiArrowButton right() {
        return new GuiArrowButton(GuiArrowButton.ArrowDirection.RIGHT);
    }

    public void drawScreen(int mouseX, int mouseY, float partialTick) {
        RenderHelper.disableStandardItemLighting();
        GL11.glEnable(3042);
        GL11.glBlendFunc(770, 771);
        int x = this.getGuiX();
        int y = this.getGuiY();
        if (this.isMouseOver) {
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        } else {
            int distanceSq = (mouseX - x - this.getWidth() / 2) * (mouseX - x - this.getWidth() / 2) + (mouseY - y - this.getHeight() / 2) * (mouseY - y - this.getHeight() / 2);
            double alpha = distanceSq < 400 ? (double)0.5F : Math.pow((double)distanceSq, -0.28);
            GL11.glColor4d((double)1.0F, (double)1.0F, (double)1.0F, alpha);
        }

        int u = 0;
        byte var10000;
        switch (this.direction) {
            case LEFT:
                u = 0;
                var10000 = 0;
                break;
            case RIGHT:
                u = 0;
                var10000 = 12;
                break;
            case UP:
                u = 12;
                var10000 = 0;
                break;
            case DOWN:
                u = 12;
                var10000 = 12;
                break;
            default:
                throw new IncompatibleClassChangeError();
        }

        int v = var10000;
        AtlasRenderHelper.drawTexturedRect(Textures.BTN_ARROWS, (double)x, (double)y, u, v, 12, 12, 24, 24);
        GL11.glDisable(3042);
    }

    public static enum ArrowDirection {
        UP("Up"),
        DOWN("Down"),
        LEFT("Left"),
        RIGHT("Right");

        public String description;

        private ArrowDirection(String text) {
            this.description = text;
        }
    }
}
