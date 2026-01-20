package hunternif.atlas.util;

import net.minecraft.src.MathHelper;

public class ShortVec2 {
    public short x;
    public short y;

    public ShortVec2(ShortVec2 vec) {
        this(vec.x, vec.y);
    }

    public ShortVec2(short x, short y) {
        this.x = x;
        this.y = y;
    }

    public ShortVec2(int x, int y) {
        this.x = (short)x;
        this.y = (short)y;
    }

    public ShortVec2(double x, double y) {
        this.x = (short) MathHelper.floor_double(x);
        this.y = (short)MathHelper.floor_double(y);
    }

    public ShortVec2 add(int dx, int dy) {
        this.x = (short)(this.x + dx);
        this.y = (short)(this.y + dy);
        return this;
    }

    public ShortVec2 set(int x, int y) {
        this.x = (short)x;
        this.y = (short)y;
        return this;
    }

    public String toString() {
        return "(" + this.x + ", " + this.y + ")";
    }

    public ShortVec2 clone() {
        return new ShortVec2(this.x, this.y);
    }

    public double distanceTo(ShortVec2 intVec2) {
        double x1 = (double)this.x;
        double y1 = (double)this.y;
        double x2 = (double)intVec2.x;
        double y2 = (double)intVec2.y;
        return Math.sqrt((x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2));
    }

    public boolean equals(Object obj) {
        if (!(obj instanceof ShortVec2 vec)) {
            return false;
        } else {
            return vec.x == this.x && vec.y == this.y;
        }
    }

    public int hashCode() {
        return this.x + (this.y << 16);
    }

    public boolean equalsIntVec3(ShortVec2 vec) {
        return vec.x == this.x && vec.y == this.y;
    }
}
