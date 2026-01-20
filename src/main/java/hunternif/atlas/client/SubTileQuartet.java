package hunternif.atlas.client;

import hunternif.atlas.client.SubTile.Part;
import hunternif.atlas.util.ArrayIterator;
import java.util.Iterator;

public class SubTileQuartet implements Iterable<SubTile> {
    private final SubTile[] array;

    public SubTileQuartet() {
        this(new SubTile(Part.BOTTOM_RIGHT), new SubTile(Part.BOTTOM_LEFT), new SubTile(Part.TOP_RIGHT), new SubTile(Part.TOP_LEFT));
    }

    public SubTileQuartet(SubTile a, SubTile b, SubTile c, SubTile d) {
        this.array = new SubTile[]{a, b, c, d};
    }

    public SubTile get(int i) {
        return this.array[i];
    }

    public void setCoords(int x, int y) {
        this.array[0].x = x;
        this.array[0].y = y;
        this.array[1].x = x + 1;
        this.array[1].y = y;
        this.array[2].x = x;
        this.array[2].y = y + 1;
        this.array[3].x = x + 1;
        this.array[3].y = y + 1;
    }

    public Iterator<SubTile> iterator() {
        return new ArrayIterator(this.array);
    }
}
