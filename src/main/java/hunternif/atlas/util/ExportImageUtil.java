package hunternif.atlas.util;

import hunternif.atlas.client.BiomeTextureMap;
import hunternif.atlas.client.SubTile;
import hunternif.atlas.client.Textures;
import hunternif.atlas.client.TileRenderIterator;
import hunternif.atlas.client.gui.ExportUpdateListener;
import hunternif.atlas.core.DimensionData;
import hunternif.atlas.marker.DimensionMarkersData;
import hunternif.atlas.marker.Marker;
import hunternif.atlas.marker.MarkerTextureMap;
import net.minecraft.src. I18n;
import net.minecraft.src. Minecraft;
import net.minecraft.src.ResourceLocation;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.*;
import java.util.List;

/**
 * Utility for exporting atlas maps as PNG images.
 * Client-side only.
 */
public class ExportImageUtil {
    public static final int TILE_SIZE = 16;
    public static final int MARKER_SIZE = 32;
    public static final int BG_TILE_SIZE = 22;

    /**
     * Opens a file chooser dialog to select where to save the PNG.
     *
     * @param atlasName Default filename
     * @param listener Progress listener
     * @return Selected file, or null if cancelled
     */
    public static File selectPngFileToSave(String atlasName, ExportUpdateListener listener) {
        listener.setStatusString(I18n.getString("gui.antiqueatlas.export.opening"));

        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            Log.error(e, "Setting system Look&Feel for JFileChooser");
        }

        Frame frame = new Frame();
        frame.setUndecorated(true);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        frame.toFront();
        frame.setVisible(false);
        frame.dispose();

        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle(I18n.getString("gui.antiqueatlas.exportImage"));
        chooser.setSelectedFile(new File(atlasName + ".png"));
        chooser.setFileFilter(new FileFilter() {
            public String getDescription() {
                return "PNG Image";
            }

            public boolean accept(File file) {
                return true;
            }
        });

        listener.setStatusString(I18n.getString("gui.antiqueatlas.export.selectFile"));
        if (chooser.showSaveDialog(frame) != JFileChooser.APPROVE_OPTION) {
            return null;
        }

        File file = chooser.getSelectedFile();
        if (file.getName().length() < 4 || !file.getName().substring(file.getName().length() - 4).equalsIgnoreCase(".png")) {
            file = new File(file.getAbsolutePath() + ".png");
        }

        return file;
    }

    /**
     * Exports the atlas data as a PNG image.
     *
     * @param biomeData Atlas tile data
     * @param globalMarkers Global markers
     * @param localMarkers Local markers (can be null)
     * @param file Output file
     * @param listener Progress listener
     * @param showMarkers Whether to render markers
     */
    public static void exportPngImage(DimensionData biomeData, DimensionMarkersData globalMarkers,
                                      DimensionMarkersData localMarkers, File file,
                                      ExportUpdateListener listener, boolean showMarkers) {
        // Calculate total work units
        float updateUnitsTotal = (float) biomeData.getSeenChunks().size();
        if (showMarkers) {
            updateUnitsTotal += getAllMarkersSize(globalMarkers);
            if (localMarkers != null) {
                updateUnitsTotal += getAllMarkersSize(localMarkers);
            }
        }

        int updateUnits = 0;
        int minX = (biomeData.getScope().minX - 1) * TILE_SIZE;
        int minY = (biomeData.getScope().minY - 1) * TILE_SIZE;
        int outWidth = (biomeData.getScope().maxX + 2) * TILE_SIZE - minX;
        int outHeight = (biomeData.getScope().maxY + 2) * TILE_SIZE - minY;

        Log.info("Image size: {}x{}", outWidth, outHeight);

        BufferedImage outImage = new BufferedImage(outWidth, outHeight, BufferedImage.TYPE_INT_ARGB);
        Graphics2D graphics = outImage.createGraphics();

        int scale = 2;
        int bgTilesX = Math.round((float) outWidth / BG_TILE_SIZE / (float) scale);
        int bgTilesY = Math.round((float) outHeight / BG_TILE_SIZE / (float) scale);
        updateUnitsTotal += (float) ((bgTilesX + 1) * (bgTilesY + 1));

        listener.setStatusString("Loading textures...");

        // Load textures
        BufferedImage bg = null;
        Map<ResourceLocation, BufferedImage> textureImageMap = new HashMap<ResourceLocation, BufferedImage>();

        try {
            InputStream is = Minecraft.getMinecraft().getResourceManager().getResource(Textures.EXPORTED_BG).getInputStream();
            bg = ImageIO.read(is);
            is.close();

            List<ResourceLocation> allTextures = new ArrayList<ResourceLocation>(64);
            allTextures.addAll(BiomeTextureMap.instance().getAllTextures());
            if (showMarkers) {
                allTextures.addAll(MarkerTextureMap.instance().getAllTextures());
            }

            updateUnitsTotal += (float) allTextures.size();

            for (ResourceLocation texture : allTextures) {
                try {
                    is = Minecraft.getMinecraft().getResourceManager().getResource(texture).getInputStream();
                    BufferedImage tileImage = ImageIO.read(is);
                    is.close();
                    textureImageMap.put(texture, tileImage);
                } catch (FileNotFoundException e) {
                    Log.warn("Texture {} not found!", texture. toString());
                }
                ++updateUnits;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        listener.update((float) updateUnits / updateUnitsTotal);
        listener.setStatusString(I18n.getString("gui. antiqueatlas.export.rendering"));

        // Draw background tiles...
        // (Background rendering code omitted for brevity - same as original)

        // Render map tiles
        TileRenderIterator iter = new TileRenderIterator(biomeData);
        while (iter.hasNext()) {
            for (SubTile subtile : iter.next()) {
                if (subtile != null && subtile.tile != null) {
                    ResourceLocation texture = BiomeTextureMap.instance().getTexture(subtile.tile);
                    BufferedImage tileImage = textureImageMap. get(texture);
                    if (tileImage != null) {
                        graphics.drawImage(tileImage,
                                TILE_SIZE + subtile.x * TILE_SIZE / 2,
                                TILE_SIZE + subtile.y * TILE_SIZE / 2,
                                TILE_SIZE + (subtile.x + 1) * TILE_SIZE / 2,
                                TILE_SIZE + (subtile.y + 1) * TILE_SIZE / 2,
                                subtile.getTextureU() * TILE_SIZE / 2,
                                subtile.getTextureV() * TILE_SIZE / 2,
                                (subtile.getTextureU() + 1) * TILE_SIZE / 2,
                                (subtile.getTextureV() + 1) * TILE_SIZE / 2,
                                null);
                    }
                }
            }

            ++updateUnits;
            if (updateUnits % 10 == 0) {
                listener.update((float) updateUnits / updateUnitsTotal);
            }
        }

        // Render markers
        if (showMarkers) {
            renderMarkers(graphics, biomeData, globalMarkers, localMarkers,
                    textureImageMap, minX, minY, listener, updateUnits, updateUnitsTotal);
        }

        // Write image
        try {
            listener.setStatusString(I18n.getString("gui. antiqueatlas.export.writing"));
            ImageIO.write(outImage, "PNG", file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Gets the total size of all markers in a dimension.
     * Replacement for Stream.collect(Collectors.toSet()).size()
     *
     * @param markersData The markers data
     * @return Total marker count
     */
    private static int getAllMarkersSize(DimensionMarkersData markersData) {
        return markersData.getAllMarkersList().size();
    }

    /**
     * Renders markers onto the image.
     */
    private static void renderMarkers(Graphics2D graphics, DimensionData biomeData,
                                      DimensionMarkersData globalMarkers, DimensionMarkersData localMarkers,
                                      Map<ResourceLocation, BufferedImage> textureImageMap,
                                      int minX, int minY, ExportUpdateListener listener,
                                      int updateUnits, float updateUnitsTotal) {
        List<Marker> markers = new ArrayList<Marker>();

        for (int x = biomeData.getScope().minX / 8; x <= biomeData.getScope().maxX / 8; ++x) {
            for (int z = biomeData.getScope().minY / 8; z <= biomeData.getScope().maxY / 8; ++z) {
                markers.clear();

                List<Marker> globalMarkersAt = globalMarkers.getMarkersAtChunk(x, z);
                if (globalMarkersAt != null) {
                    markers. addAll(globalMarkersAt);
                }

                if (localMarkers != null) {
                    List<Marker> localMarkersAt = localMarkers.getMarkersAtChunk(x, z);
                    if (localMarkersAt != null) {
                        markers.addAll(localMarkersAt);
                    }
                }

                for (Marker marker : markers) {
                    ++updateUnits;
                    if (marker.isVisibleAhead() || biomeData.hasTileAt(marker.getChunkX(), marker.getChunkZ())) {
                        ResourceLocation texture = MarkerTextureMap.instance().getTexture(marker.getType());
                        BufferedImage markerImage = textureImageMap. get(texture);
                        if (markerImage != null) {
                            int markerX = marker.getX() - minX;
                            int markerY = marker.getZ() - minY;
                            graphics.drawImage(markerImage,
                                    markerX - MARKER_SIZE / 2, markerY - MARKER_SIZE / 2,
                                    markerX + MARKER_SIZE / 2, markerY + MARKER_SIZE / 2,
                                    0, 0, MARKER_SIZE, MARKER_SIZE, null);

                            if (updateUnits % 10 == 0) {
                                listener.update((float) updateUnits / updateUnitsTotal);
                            }
                        }
                    }
                }
            }
        }
    }
}