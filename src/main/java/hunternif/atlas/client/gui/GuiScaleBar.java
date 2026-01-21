package hunternif.atlas.client.gui;

import com.google.common.collect.ImmutableMap;
import hunternif.atlas.client.Textures;
import hunternif.atlas.client.gui.core.GuiComponent;
import hunternif.atlas.util.AtlasRenderHelper;

import net.minecraft.src.Minecraft;
import net.minecraft.src.ResourceLocation;
import net.minecraft.src.StatCollector;

import java.util.Collections;
import java.util.Map;

public class GuiScaleBar extends GuiComponent {
    public static final int WIDTH = 20;
    public static final int HEIGHT = 8;
    private static Map<Double, ResourceLocation> textureMap;
    private double mapScale;

    public GuiScaleBar() {
        ImmutableMap.Builder<Double, ResourceLocation> builder = ImmutableMap.builder();
        builder.put((double)0.0625F, Textures.SCALEBAR_512);
        builder.put((double)0.125F, Textures.SCALEBAR_256);
        builder.put((double)0.25F, Textures.SCALEBAR_128);
        builder.put((double)0.5F, Textures.SCALEBAR_64);
        builder.put((double)1.0F, Textures.SCALEBAR_32);
        builder.put((double)2.0F, Textures.SCALEBAR_16);
        builder.put((double)4.0F, Textures.SCALEBAR_8);
        builder.put((double)8.0F, Textures.SCALEBAR_4);
        textureMap = builder.build();
        this.mapScale = (double)1.0F;
        this.setSize(20, 8);
    }

    public void setMapScale(double scale) {
        this.mapScale = scale;
    }

    private ResourceLocation getTexture() {
        return (ResourceLocation)textureMap.get(this.mapScale);
    }

    public void initGui() {
        super.initGui();
    }

    public void drawScreen(int mouseX, int mouseY, float partialTick) {
        ResourceLocation texture = this.getTexture();
        if (texture != null) {
            AtlasRenderHelper.drawFullTexture(texture, (double)this.getGuiX(), (double)this.getGuiY(), 20, 8);
            if (this.isMouseOver) {
                this.drawTooltip(Collections.singletonList(StatCollector.translateToLocal("gui.antiqueatlas.scalebar")), Minecraft.getMinecraft().fontRenderer);
            }

        }
    }
}
