package hunternif.atlas.util;

public abstract class SaveData {
    private boolean dirty;

    public void markDirty() {
        this.dirty = true;
    }

    public void setDirty(boolean value) {
        this.dirty = value;
    }

    public boolean isDirty() {
        return this.dirty;
    }
}
