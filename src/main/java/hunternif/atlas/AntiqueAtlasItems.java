package hunternif.atlas;

import hunternif.atlas.item.ItemAtlas;
import hunternif.atlas.item.ItemEmptyAtlas;

public class AntiqueAtlasItems {

    public static ItemEmptyAtlas emptyAtlas;
    public static ItemAtlas itemAtlas;

    public static void registerItems() {

        emptyAtlas = new ItemEmptyAtlas(23601);

        itemAtlas = new ItemAtlas(23602);
    }
}