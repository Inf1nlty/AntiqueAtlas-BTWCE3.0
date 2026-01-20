package hunternif.atlas.client.gui.core;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ToggleGroup<B extends GuiToggleButton> implements Iterable<B> {
    private final List<B> buttons = new ArrayList();
    private final List<ISelectListener<? extends B>> listeners = new ArrayList();
    private B selectedButton = null;
    private final ToggleGroup<B>.ClickListener clickListener = new ClickListener();

    public boolean addButton(B button) {
        if (!this.buttons.contains(button)) {
            this.buttons.add(button);
            button.addListener(this.clickListener);
            button.setRadioGroup(this);
            return true;
        } else {
            return false;
        }
    }

    public boolean removeButton(B button) {
        if (this.buttons.remove(button)) {
            button.removeListener(this.clickListener);
            button.setRadioGroup((ToggleGroup)null);
            return true;
        } else {
            return false;
        }
    }

    public void removeAllButtons() {
        Iterator<B> iter = this.buttons.iterator();

        while(iter.hasNext()) {
            B button = (B)(iter.next());
            button.removeListener(this.clickListener);
            button.setRadioGroup((ToggleGroup)null);
            iter.remove();
        }

    }

    public B getSelectedButton() {
        return this.selectedButton;
    }

    public void setSelectedButton(B button) {
        if (this.buttons.contains(button)) {
            if (this.selectedButton != null) {
                this.selectedButton.setSelected(false);
            }

            button.setSelected(true);
            this.selectedButton = button;
        }

    }

    public Iterator<B> iterator() {
        return this.buttons.iterator();
    }

    public void addListener(ISelectListener<? extends B> listener) {
        this.listeners.add(listener);
    }

    public void removeListener(ISelectListener<? extends B> listener) {
        this.listeners.remove(listener);
    }

    private class ClickListener implements IButtonListener<B> {
        public void onClick(B button) {
            if (button != ToggleGroup.this.selectedButton) {
                if (ToggleGroup.this.selectedButton != null) {
                    ToggleGroup.this.selectedButton.setSelected(false);
                }

                ToggleGroup.this.selectedButton = button;

                for(ISelectListener listener : ToggleGroup.this.listeners) {
                    listener.onSelect(ToggleGroup.this.selectedButton);
                }
            }

        }
    }
}
