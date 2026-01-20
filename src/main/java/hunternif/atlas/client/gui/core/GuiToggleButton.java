package hunternif.atlas.client.gui.core;

public class GuiToggleButton extends GuiComponentButton {
    private boolean selected;
    private ToggleGroup radioGroup;

    public void setSelected(boolean value) {
        this.selected = value;
    }

    public boolean isSelected() {
        return this.selected;
    }

    protected void setRadioGroup(ToggleGroup radioGroup) {
        this.radioGroup = radioGroup;
    }

    public ToggleGroup getRadioGroup() {
        return this.radioGroup;
    }

    protected void onClick() {
        if (!this.isSelected()) {
            if (this.isEnabled()) {
                this.setSelected(true);
            }

            super.onClick();
        }

    }
}
