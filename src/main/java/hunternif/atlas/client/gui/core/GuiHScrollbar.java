package hunternif.atlas.client.gui.core;

import hunternif.atlas.util.AtlasRenderHelper;

public class GuiHScrollbar extends AGuiScrollbar {
    public GuiHScrollbar(GuiViewport viewport) {
        super(viewport);
    }

    protected void drawAnchor() {
        AtlasRenderHelper.drawTexturedRect(this.texture, (double)(this.getGuiX() + this.anchorPos), (double)this.getGuiY(), 0, 0, this.capLength, this.textureHeight, this.textureWidth, this.textureHeight);
        AtlasRenderHelper.drawTexturedRect(this.texture, (double)(this.getGuiX() + this.anchorPos + this.capLength), (double)this.getGuiY(), this.capLength, 0, this.textureBodyLength, this.textureHeight, this.textureWidth, this.textureHeight, this.bodyTextureScale, (double)1.0F);
        AtlasRenderHelper.drawTexturedRect(this.texture, (double)(this.getGuiX() + this.anchorPos + this.anchorSize - this.capLength), (double)this.getGuiY(), this.textureWidth - this.capLength, 0, this.capLength, this.textureHeight, this.textureWidth, this.textureHeight);
    }

    protected int getTextureLength() {
        return this.textureWidth;
    }

    protected int getScrollbarLength() {
        return this.getWidth();
    }

    protected int getViewportSize() {
        return this.viewport.getWidth();
    }

    protected int getContentSize() {
        return this.viewport.contentWidth;
    }

    protected int getMousePos(int mouseX, int mouseY) {
        return mouseX - this.getGuiX();
    }

    protected void updateContentPos() {
        this.viewport.content.setRelativeCoords(-this.scrollPos, this.viewport.content.getRelativeY());
    }

    protected void setScrollbarWidth(int textureWidth, int textureHeight) {
        this.setSize(this.getWidth(), textureHeight);
    }
}
