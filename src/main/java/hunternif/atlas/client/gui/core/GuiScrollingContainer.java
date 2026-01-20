package hunternif.atlas.client.gui.core;

import hunternif.atlas.client.Textures;

public class GuiScrollingContainer extends GuiComponent {
    protected final GuiViewport viewport = new GuiViewport();
    protected final GuiHScrollbar scrollbarHor;
    protected final GuiVScrollbar scrollbarVer;

    public GuiScrollingContainer() {
        this.scrollbarHor = new GuiHScrollbar(this.viewport);
        this.scrollbarHor.setTexture(Textures.SCROLLBAR_HOR, 8, 7, 2);
        this.scrollbarVer = new GuiVScrollbar(this.viewport);
        this.scrollbarVer.setTexture(Textures.SCROLLBAR_VER, 7, 8, 2);
        this.setWheelScrollsVertially();
        this.addChild(this.viewport);
        this.addChild(this.scrollbarHor);
        this.addChild(this.scrollbarVer);
    }

    public GuiComponent addContent(GuiComponent child) {
        return this.viewport.addContent(child);
    }

    public GuiComponent removeContent(GuiComponent child) {
        return this.viewport.removeContent(child);
    }

    public void removeAllContent() {
        this.viewport.removeAllContent();
    }

    public void setViewportSize(int width, int height) {
        this.viewport.setSize(width, height);
        this.scrollbarHor.setRelativeCoords(0, height);
        this.scrollbarHor.setSize(width, this.scrollbarHor.getHeight());
        this.scrollbarVer.setRelativeCoords(width, 0);
        this.scrollbarVer.setSize(this.scrollbarVer.getWidth(), height);
    }

    protected void validateSize() {
        super.validateSize();
        this.scrollbarHor.updateContent();
        this.scrollbarVer.updateContent();
    }

    public void setWheelScrollsHorizontally() {
        this.scrollbarHor.setUsesWheel(true);
        this.scrollbarVer.setUsesWheel(false);
    }

    public void setWheelScrollsVertially() {
        this.scrollbarHor.setUsesWheel(false);
        this.scrollbarVer.setUsesWheel(true);
    }

    public void scrollTo(int x, int y) {
        this.scrollbarHor.setScrollPos(x);
        this.scrollbarVer.setScrollPos(y);
    }

    public int getWidth() {
        return super.getWidth() - (this.scrollbarVer.visible ? 0 : this.scrollbarVer.getWidth());
    }

    public int getHeight() {
        return super.getHeight() - (this.scrollbarHor.visible ? 0 : this.scrollbarHor.getHeight());
    }
}
