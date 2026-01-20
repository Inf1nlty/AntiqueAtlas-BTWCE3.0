package hunternif.atlas.client.gui.core;

import java.util.ArrayList;
import java.util.List;

public class GuiComponentButton extends GuiComponent {
    public static final String DEFAULT_CLICK_SOUND = "random.click";
    private final List<IButtonListener> listeners = new ArrayList();
    private boolean enabled = true;
    private String clickSound = "random.click";

    public void setEnabled(boolean value) {
        this.enabled = value;
    }

    public boolean isEnabled() {
        return this.enabled;
    }

    public void setClickSound(String clickSound) {
        this.clickSound = clickSound;
    }

    public void mute() {
        this.clickSound = null;
    }

    protected void mouseClicked(int x, int y, int mouseButton) {
        super.mouseClicked(x, y, mouseButton);
        if (mouseButton == 0 && this.enabled && this.isMouseOver) {
            this.onClick();
            this.mouseHasBeenHandled();
        }

    }

    protected void onClick() {
        if (this.clickSound != null) {
            this.mc.sndManager.playSoundFX(this.clickSound, 1.0F, 1.0F);
        }

        for(IButtonListener listener : this.listeners) {
            listener.onClick(this);
        }

    }

    public void addListener(IButtonListener listener) {
        this.listeners.add(listener);
    }

    public void removeListener(IButtonListener listener) {
        this.listeners.remove(listener);
    }
}
