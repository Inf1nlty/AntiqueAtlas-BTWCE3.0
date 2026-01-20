package hunternif.atlas.core;

import hunternif.atlas.util.Rect;

public interface ITileStorage {
    void setTile(int var1, int var2, Tile var3);

    Tile getTile(int var1, int var2);

    boolean hasTileAt(int var1, int var2);

    Rect getScope();
}
