package hunternif.atlas.util;

import net.minecraft.src.Minecraft;
import net.minecraft.src.ResourceLocation;
import net.minecraft.src.Tessellator;
import org.lwjgl.opengl.GL11;

public class AtlasRenderHelper {
    public static void drawTexturedRect(ResourceLocation texture, double x, double y, int u, int v, int width, int height, int imageWidth, int imageHeight, double scaleX, double scaleY) {
        Minecraft.getMinecraft().getTextureManager().bindTexture(texture);
        double minU = (double)u / (double)imageWidth;
        double maxU = (double)(u + width) / (double)imageWidth;
        double minV = (double)v / (double)imageHeight;
        double maxV = (double)(v + height) / (double)imageHeight;
        Tessellator tessellator = Tessellator.instance;
        tessellator.startDrawingQuads();
        tessellator.addVertexWithUV(x + scaleX * (double)width, y + scaleY * (double)height, (double)0.0F, maxU, maxV);
        tessellator.addVertexWithUV(x + scaleX * (double)width, y, (double)0.0F, maxU, minV);
        tessellator.addVertexWithUV(x, y, (double)0.0F, minU, minV);
        tessellator.addVertexWithUV(x, y + scaleY * (double)height, (double)0.0F, minU, maxV);
        tessellator.draw();
    }

    public static void drawTexturedRect(ResourceLocation texture, double x, double y, int u, int v, int width, int height, int imageWidth, int imageHeight) {
        drawTexturedRect(texture, x, y, u, v, width, height, imageWidth, imageHeight, (double)1.0F, (double)1.0F);
    }

    public static void drawFullTexture(ResourceLocation texture, double x, double y, int width, int height, double scaleX, double scaleY) {
        drawTexturedRect(texture, x, y, 0, 0, width, height, width, height, scaleX, scaleY);
    }

    public static void drawFullTexture(ResourceLocation texture, double x, double y, int width, int height) {
        drawFullTexture(texture, x, y, width, height, (double)1.0F, (double)1.0F);
    }

    public static void drawAutotileCorner(ResourceLocation texture, int x, int y, int u, int v, int tileHalfSize) {
        Minecraft.getMinecraft().getTextureManager().bindTexture(texture);
        double minU = (double)u / (double)4.0F;
        double maxU = (double)(u + 1) / (double)4.0F;
        double minV = (double)v / (double)6.0F;
        double maxV = (double)(v + 1) / (double)6.0F;
        Tessellator tessellator = Tessellator.instance;
        tessellator.startDrawingQuads();
        tessellator.addVertexWithUV((double)(x + tileHalfSize), (double)(y + tileHalfSize), (double)0.0F, maxU, maxV);
        tessellator.addVertexWithUV((double)(x + tileHalfSize), (double)y, (double)0.0F, maxU, minV);
        tessellator.addVertexWithUV((double)x, (double)y, (double)0.0F, minU, minV);
        tessellator.addVertexWithUV((double)x, (double)(y + tileHalfSize), (double)0.0F, minU, maxV);
        tessellator.draw();
    }

    public static void setGLColor(int color, float alpha) {
        float r = (float)(color >> 16 & 255) / 256.0F;
        float g = (float)(color >> 8 & 255) / 256.0F;
        float b = (float)(color & 255) / 256.0F;
        GL11.glColor4f(r, g, b, alpha);
    }
}
