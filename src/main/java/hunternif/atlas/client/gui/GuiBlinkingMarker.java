package hunternif.atlas.client.gui;

import hunternif.atlas.client.gui.core.GuiBlinkingImage;
import hunternif.atlas.marker.MarkerTextureMap;

public class GuiBlinkingMarker extends GuiBlinkingImage implements GuiMarkerFinalizer.IMarkerTypeSelectListener {
    public void onSelectMarkerType(String markerType) {
        this.setTexture(MarkerTextureMap.instance().getTexture(markerType), 32, 32);
    }
}
