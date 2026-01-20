package hunternif.atlas.client.gui.core;

import net.minecraft.src.ResourceLocation;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

public abstract class AGuiScrollbar extends GuiComponent {
    protected ResourceLocation texture;
    protected int textureWidth;
    protected int textureHeight;
    protected int capLength;
    protected int textureBodyLength;
    private static int scrollStep = 18;
    protected int anchorSize;
    protected final GuiViewport viewport;
    protected boolean visible = false;
    private boolean isDragged = false;
    private boolean wasClicking = false;
    private boolean usesWheel = true;
    private float contentRatio = 1.0F;
    private float scrollRatio = 0.0F;
    protected int anchorPos = 0;
    protected double bodyTextureScale = (double)1.0F;
    protected int scrollPos = 0;

    protected abstract int getTextureLength();

    protected abstract int getScrollbarLength();

    protected abstract int getViewportSize();

    protected abstract int getContentSize();

    protected abstract int getMousePos(int var1, int var2);

    protected abstract void drawAnchor();

    protected abstract void updateContentPos();

    protected abstract void setScrollbarWidth(int var1, int var2);

    public AGuiScrollbar(GuiViewport viewport) {
        this.viewport = viewport;
    }

    public void setTexture(ResourceLocation texture, int width, int height, int capLength) {
        this.texture = texture;
        this.textureWidth = width;
        this.textureHeight = height;
        this.capLength = capLength;
        this.textureBodyLength = this.getTextureLength() - capLength * 2;
        this.setScrollbarWidth(width, height);
    }

    public void setUsesWheel(boolean value) {
        this.usesWheel = value;
    }

    public void updateContent() {
        this.contentRatio = (float)(this.getViewportSize() / this.getContentSize());
        this.visible = this.contentRatio < 1.0F;
        this.updateAnchorSize();
        this.updateAnchorPos();
    }

    public void setScrollPos(int scrollPos) {
        this.viewport.content.validateSize();
        this.viewport.validateSize();
        this.doSetScrollPos(scrollPos);
    }

    private void doSetScrollPos(int scrollPos) {
        int scrollPos2 = Math.max(0, Math.min(scrollPos, this.getContentSize() - this.getViewportSize()));
        this.scrollPos = scrollPos2;
        this.scrollRatio = (float)(scrollPos2 / (this.getContentSize() - this.getViewportSize()));
        this.updateAnchorPos();
    }

    public void setScrollRatio(float scrollRatio) {
        this.viewport.content.validateSize();
        this.viewport.validateSize();
        this.doSetScrollRatio(scrollRatio);
    }

    private void doSetScrollRatio(float scrollRatio) {
        if (scrollRatio < 0.0F) {
            scrollRatio = 0.0F;
        }

        if (scrollRatio > 1.0F) {
            scrollRatio = 1.0F;
        }

        this.scrollRatio = scrollRatio;
        this.scrollPos = Math.round(scrollRatio * (float)(this.getContentSize() - this.getViewportSize()));
        this.updateAnchorPos();
    }

    public void handleMouseInput() {
        super.handleMouseInput();
        int wheelMove;
        if (this.usesWheel && (wheelMove = Mouse.getEventDWheel()) != 0 && this.visible) {
            int wheelMove2 = wheelMove > 0 ? -1 : 1;
            this.doSetScrollPos(this.scrollPos + wheelMove2 * scrollStep);
        }

    }

    public void drawScreen(int mouseX, int mouseY, float partialTick) {
        if (!this.visible) {
            this.isDragged = false;
        } else {
            boolean mouseDown = Mouse.isButtonDown(0);
            if (!this.wasClicking && mouseDown && this.isMouseOver) {
                this.isDragged = true;
            }

            if (!mouseDown) {
                this.isDragged = false;
            }

            this.wasClicking = mouseDown;
            if (this.isDragged) {
                this.doSetScrollRatio((float)((this.getMousePos(mouseX, mouseY) - this.anchorSize / 2) / (this.getScrollbarLength() - this.anchorSize)));
            }

            GL11.glEnable(3553);
            GL11.glEnable(3042);
            GL11.glBlendFunc(770, 771);
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            this.drawAnchor();
            GL11.glDisable(3042);
        }
    }

    private void updateAnchorSize() {
        this.anchorSize = Math.max(this.capLength * 2, Math.round(Math.min(1.0F, this.contentRatio) * (float)this.getScrollbarLength()));
        this.bodyTextureScale = (double)((this.anchorSize - this.capLength * 2) / this.textureBodyLength);
    }

    private void updateAnchorPos() {
        this.anchorPos = Math.round(this.scrollRatio * (float)(this.getViewportSize() - this.anchorSize));
        this.updateContentPos();
    }
}
