package hunternif.atlas.client;

import hunternif.atlas.core.Tile;

public class SubTile {
    public Tile tile;
    public int x;
    public int y;
    public Shape shape;
    public Part part;

    public SubTile(Part part) {
        this.part = part;
    }

    public int getTextureU() {
        int var10000;
        switch (this.shape) {
            case SINGLE_OBJECT:
                var10000 = this.part.u;
                break;
            case CONCAVE:
                var10000 = 2 + this.part.u;
                break;
            case VERTICAL:
            case CONVEX:
                var10000 = this.part.u * 3;
                break;
            case HORIZONTAL:
            case FULL:
                var10000 = 2 - this.part.u;
                break;
            default:
                throw new IncompatibleClassChangeError();
        }

        return var10000;
    }

    public int getTextureV() {
        int var10000;
        switch (this.shape) {
            case SINGLE_OBJECT:
            case CONCAVE:
                var10000 = this.part.v;
                break;
            case VERTICAL:
            case FULL:
                var10000 = 4 - this.part.v;
                break;
            case CONVEX:
            case HORIZONTAL:
                var10000 = 2 + this.part.v * 3;
                break;
            default:
                throw new IncompatibleClassChangeError();
        }

        return var10000;
    }

    public static enum Shape {
        CONVEX,
        CONCAVE,
        HORIZONTAL,
        VERTICAL,
        FULL,
        SINGLE_OBJECT;
    }

    public static enum Part {
        TOP_LEFT(0, 0),
        TOP_RIGHT(1, 0),
        BOTTOM_LEFT(0, 1),
        BOTTOM_RIGHT(1, 1);

        int u;
        int v;

        private Part(int u, int v) {
            this.u = u;
            this.v = v;
        }
    }
}
