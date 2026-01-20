package hunternif.atlas.util;

public class Rect {
    public int minX;
    public int minY;
    public int maxX;
    public int maxY;

    public Rect() {
        this(Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE);
    }

    public Rect(int minX, int minY, int maxX, int maxY) {
        this.set(minX, minY, maxX, maxY);
    }

    public Rect(Rect r) {
        this(r.minX, r.minY, r.maxX, r.maxY);
    }

    public Rect set(int minX, int minY, int maxX, int maxY) {
        this.minX = minX;
        this.minY = minY;
        this.maxX = maxX;
        this.maxY = maxY;
        return this;
    }

    public Rect set(Rect r) {
        this.set(r.minX, r.minY, r.maxX, r.maxY);
        return this;
    }

    public Rect setOrigin(int x, int y) {
        this.minX = x;
        this.minY = y;
        return this;
    }

    public Rect setSize(int width, int height) {
        this.maxX = this.minX + width;
        this.maxY = this.minY + height;
        return this;
    }

    public int getWidth() {
        return this.maxX - this.minX;
    }

    public int getHeight() {
        return this.maxY - this.minY;
    }

    public void extendTo(int x, int y) {
        if (x < this.minX) {
            this.minX = x;
        }

        if (x > this.maxX) {
            this.maxX = x;
        }

        if (y < this.minY) {
            this.minY = y;
        }

        if (y > this.maxY) {
            this.maxY = y;
        }

    }

    public boolean equals(Object obj) {
        if (!(obj instanceof Rect r)) {
            return false;
        } else {
            return this.minX == r.minX && this.minY == r.minY && this.maxX == r.maxX && this.maxY == r.maxY;
        }
    }

    public Rect clone() {
        return new Rect(this);
    }

    public String toString() {
        return String.format("Rect{%d, %d, %d, %d}", this.minX, this.minY, this.maxX, this.maxY);
    }
}
