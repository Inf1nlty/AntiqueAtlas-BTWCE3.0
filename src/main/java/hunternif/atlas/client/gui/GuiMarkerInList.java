package hunternif.atlas.client.gui;

import hunternif.atlas.client.Textures;
import hunternif.atlas.client.gui.core.GuiToggleButton;
import hunternif.atlas.marker.MarkerTextureMap;
import hunternif.atlas.util.AtlasRenderHelper;
import net.minecraft.src.ResourceLocation;
import org.lwjgl.opengl.GL11;

public class GuiMarkerInList extends GuiToggleButton {
    public static final int FRAME_SIZE = 34;
    private final String markerType;

    public GuiMarkerInList(String markerType) {
        this.markerType = markerType;
        this.setSize(34, 34);
    }

    public String getMarkerType() {
        return this.markerType;
    }

    public void drawScreen(int mouseX, int mouseY, float partialTick) {
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        AtlasRenderHelper.drawFullTexture(this.isSelected() ? Textures.MARKER_FRAME_ON : Textures.MARKER_FRAME_OFF, (double)this.getGuiX(), (double)this.getGuiY(), 34, 34);
        ResourceLocation texture = MarkerTextureMap.instance().getTexture(this.markerType);
        if (texture != null) {
            AtlasRenderHelper.drawFullTexture(texture, (double)(this.getGuiX() + 1), (double)(this.getGuiY() + 1), 32, 32);
        }

        super.drawScreen(mouseX, mouseY, partialTick);
    }
}
