package hunternif.atlas.api;

import hunternif.atlas.api.impl.MarkerApiImpl;
import hunternif.atlas.api.impl.TileApiImpl;

public class AtlasAPI {
    private static final int VERSION = 3;
    private static final TileAPI tileApi = new TileApiImpl();
    private static final MarkerAPI markerApi = new MarkerApiImpl();

    public static int getVersion() {
        return 3;
    }

    public static TileAPI getTileAPI() {
        return tileApi;
    }

    public static MarkerAPI getMarkerAPI() {
        return markerApi;
    }
}
