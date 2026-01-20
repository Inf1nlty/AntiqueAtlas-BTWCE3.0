package hunternif.atlas.client.gui.core;

import hunternif.atlas.util.AtlasRenderHelper;

public class GuiVScrollbar extends AGuiScrollbar {
    public GuiVScrollbar(GuiViewport viewport) {
        super(viewport);
    }

    protected void drawAnchor() {
        AtlasRenderHelper.drawTexturedRect(this.texture, (double)this.getGuiX(), (double)(this.getGuiY() + this.anchorPos), 0, 0, this.textureWidth, this.capLength, this.textureWidth, this.textureHeight);
        AtlasRenderHelper.drawTexturedRect(this.texture, (double)this.getGuiX(), (double)(this.getGuiY() + this.anchorPos + this.capLength), 0, this.capLength, this.textureWidth, this.textureBodyLength, this.textureWidth, this.textureHeight, (double)1.0F, this.bodyTextureScale);
        AtlasRenderHelper.drawTexturedRect(this.texture, (double)this.getGuiX(), (double)(this.getGuiY() + this.anchorPos + this.anchorSize - this.capLength), 0, this.textureHeight - this.capLength, this.textureWidth, this.capLength, this.textureWidth, this.textureHeight);
    }

    protected int getTextureLength() {
        return this.textureHeight;
    }

    protected int getScrollbarLength() {
        return this.getHeight();
    }

    protected int getViewportSize() {
        return this.viewport.getHeight();
    }

    protected int getContentSize() {
        return this.viewport.contentHeight;
    }

    protected int getMousePos(int mouseX, int mouseY) {
        return mouseY - this.getGuiY();
    }

    protected void updateContentPos() {
        this.viewport.content.setRelativeCoords(this.viewport.content.getRelativeX(), -this.scrollPos);
    }

    protected void setScrollbarWidth(int textureWidth, int textureHeight) {
        this.setSize(textureWidth, this.getHeight());
    }
}
