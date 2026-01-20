package hunternif.atlas.client;

import hunternif.atlas.client.SubTile.Part;
import hunternif.atlas.client.SubTile.Shape;
import hunternif.atlas.core.ITileStorage;
import hunternif.atlas.core.Tile;
import hunternif.atlas.util.Rect;
import java.util.Iterator;

public class TileRenderIterator implements Iterator<SubTileQuartet> {
    private final ITileStorage tiles;
    private int step = 1;
    private final Rect scope = new Rect();
    private Tile a;
    private Tile b;
    private Tile c;
    private Tile d;
    private Tile e;
    private Tile f;
    private Tile g;
    private Tile h;
    private Tile i;
    private Tile j;
    private Tile k;
    private Tile l;
    private final SubTile _d;
    private final SubTile _e;
    private final SubTile _h;
    private final SubTile _i;
    private final SubTileQuartet quartet;
    private int chunkX;
    private int chunkY;
    private int subtileX;
    private int subtileY;

    public void setStep(int step) {
        if (step >= 1) {
            this.step = step;
        }

    }

    public void setScope(int minX, int minY, int maxX, int maxY) {
        this.scope.set(minX, minY, maxX, maxY);
        this.chunkX = minX;
        this.chunkY = minY;
    }

    public void setScope(Rect scope) {
        this.scope.set(scope);
        this.chunkX = scope.minX;
        this.chunkY = scope.minY;
    }

    public TileRenderIterator(ITileStorage tiles) {
        this._d = new SubTile(Part.BOTTOM_RIGHT);
        this._e = new SubTile(Part.BOTTOM_LEFT);
        this._h = new SubTile(Part.TOP_RIGHT);
        this._i = new SubTile(Part.TOP_LEFT);
        this.quartet = new SubTileQuartet(this._d, this._e, this._h, this._i);
        this.subtileX = -1;
        this.subtileY = -1;
        this.tiles = tiles;
        this.setScope(tiles.getScope());
    }

    public boolean hasNext() {
        return this.chunkX >= this.scope.minX && this.chunkX <= this.scope.maxX + 1 && this.chunkY >= this.scope.minY && this.chunkY <= this.scope.maxY + 1;
    }

    public SubTileQuartet next() {
        this.a = this.b;
        this.b = this.tiles.getTile(this.chunkX, this.chunkY - this.step * 2);
        this.c = this.d;
        this.d = this.e;
        this.e = this.f;
        this.f = this.tiles.getTile(this.chunkX + this.step, this.chunkY - this.step);
        this.g = this.h;
        this.h = this.i;
        this.i = this.j;
        this.j = this.tiles.getTile(this.chunkX + this.step, this.chunkY);
        this.k = this.l;
        this.l = this.tiles.getTile(this.chunkX, this.chunkY + this.step);
        this.quartet.setCoords(this.subtileX, this.subtileY);
        this._d.tile = this.d;
        this._e.tile = this.e;
        this._h.tile = this.h;
        this._i.tile = this.i;

        for(SubTile subtile : this.quartet) {
            subtile.shape = Shape.CONVEX;
        }

        if (shouldStitchToHorizontally(this.d, this.e)) {
            stitchHorizontally(this._d);
        }

        if (shouldStitchToHorizontally(this.e, this.d)) {
            stitchHorizontally(this._e);
        }

        if (shouldStitchToHorizontally(this.h, this.i)) {
            stitchHorizontally(this._h);
        }

        if (shouldStitchToHorizontally(this.i, this.h)) {
            stitchHorizontally(this._i);
        }

        if (shouldStitchToVertically(this.d, this.h)) {
            stitchVertically(this._d);
            if (this._d.shape == Shape.CONCAVE && shouldStitchTo(this.d, this.i)) {
                this._d.shape = Shape.FULL;
            }
        }

        if (shouldStitchToVertically(this.h, this.d)) {
            stitchVertically(this._h);
            if (this._h.shape == Shape.CONCAVE && shouldStitchTo(this.h, this.e)) {
                this._h.shape = Shape.FULL;
            }
        }

        if (shouldStitchToVertically(this.e, this.i)) {
            stitchVertically(this._e);
            if (this._e.shape == Shape.CONCAVE && shouldStitchTo(this.e, this.h)) {
                this._e.shape = Shape.FULL;
            }
        }

        if (shouldStitchToVertically(this.i, this.e)) {
            stitchVertically(this._i);
            if (this._i.shape == Shape.CONCAVE && shouldStitchTo(this.i, this.d)) {
                this._i.shape = Shape.FULL;
            }
        }

        if (this._d.shape == Shape.CONVEX && !shouldStitchToVertically(this.d, this.a) && !shouldStitchToHorizontally(this.d, this.c)) {
            this._d.shape = Shape.SINGLE_OBJECT;
        }

        if (this._e.shape == Shape.CONVEX && !shouldStitchToVertically(this.e, this.b) && !shouldStitchToHorizontally(this.e, this.f)) {
            this._e.shape = Shape.SINGLE_OBJECT;
        }

        if (this._h.shape == Shape.CONVEX && !shouldStitchToHorizontally(this.h, this.g) && !shouldStitchToVertically(this.h, this.k)) {
            this._h.shape = Shape.SINGLE_OBJECT;
        }

        if (this._i.shape == Shape.CONVEX && !shouldStitchToHorizontally(this.i, this.j) && !shouldStitchToVertically(this.i, this.l)) {
            this._i.shape = Shape.SINGLE_OBJECT;
        }

        this.chunkX += this.step;
        this.subtileX += 2;
        if (this.chunkX > this.scope.maxX + 1) {
            this.chunkX = this.scope.minX;
            this.subtileX = -1;
            this.chunkY += this.step;
            this.subtileY += 2;
            this.a = null;
            this.b = null;
            this.c = null;
            this.d = null;
            this.e = null;
            this.f = this.tiles.getTile(this.chunkX, this.chunkY - this.step);
            this.g = null;
            this.h = null;
            this.i = null;
            this.j = this.tiles.getTile(this.chunkX, this.chunkY);
            this.k = null;
            this.l = null;
        }

        return this.quartet;
    }

    private static boolean shouldStitchTo(Tile tile, Tile to) {
        if (tile == null) {
            return false;
        } else {
            TextureSet set = BiomeTextureMap.instance().getTextureSet(tile);
            TextureSet toSet = BiomeTextureMap.instance().getTextureSet(to);
            return set == null ? false : set.shouldStitchTo(toSet);
        }
    }

    private static boolean shouldStitchToHorizontally(Tile tile, Tile to) {
        if (tile == null) {
            return false;
        } else {
            TextureSet set = BiomeTextureMap.instance().getTextureSet(tile);
            TextureSet toSet = BiomeTextureMap.instance().getTextureSet(to);
            return set == null ? false : set.shouldStitchToHorizontally(toSet);
        }
    }

    private static boolean shouldStitchToVertically(Tile tile, Tile to) {
        if (tile == null) {
            return false;
        } else {
            TextureSet set = BiomeTextureMap.instance().getTextureSet(tile);
            TextureSet toSet = BiomeTextureMap.instance().getTextureSet(to);
            return set == null ? false : set.shouldStitchToVertically(toSet);
        }
    }

    private static void stitchVertically(SubTile subtile) {
        if (subtile.shape == Shape.HORIZONTAL) {
            subtile.shape = Shape.CONCAVE;
        }

        if (subtile.shape == Shape.CONVEX) {
            subtile.shape = Shape.VERTICAL;
        }

    }

    private static void stitchHorizontally(SubTile subtile) {
        if (subtile.shape == Shape.VERTICAL) {
            subtile.shape = Shape.CONCAVE;
        }

        if (subtile.shape == Shape.CONVEX) {
            subtile.shape = Shape.HORIZONTAL;
        }

    }

    public void remove() {
        throw new UnsupportedOperationException("cannot remove subtiles from tile storage");
    }
}
