package hunternif.atlas.client.gui;

import hunternif.atlas.AntiqueAtlasItems;
import hunternif.atlas.AntiqueAtlasMod;
import hunternif.atlas.api.AtlasAPI;
import hunternif.atlas.client.BiomeTextureMap;
import hunternif.atlas.client.SubTile;
import hunternif.atlas.client.Textures;
import hunternif.atlas.client.TileRenderIterator;
import hunternif.atlas.client.gui.core.GuiComponent;
import hunternif.atlas.client.gui.core.GuiComponentButton;
import hunternif.atlas.client.gui.core.GuiCursor;
import hunternif.atlas.client.gui.core.GuiStates;
import hunternif.atlas.client.gui.core.IButtonListener;
import hunternif.atlas.core.DimensionData;
import hunternif.atlas.marker.DimensionMarkersData;
import hunternif.atlas.marker.Marker;
import hunternif.atlas.marker.MarkerTextureMap;
import hunternif.atlas.marker.MarkersData;
import hunternif.atlas.util.AtlasRenderHelper;
import hunternif.atlas.util.ExportImageUtil;
import hunternif.atlas.util.Log;
import hunternif.atlas.util.MathUtil;
import hunternif.atlas.util.Rect;
import java.io.File;
import java.util.Arrays;
import java.util.List;

import net.minecraft.src.*;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

public class GuiAtlas extends GuiComponent {
    public static final int WIDTH = 310;
    public static final int HEIGHT = 218;
    private static final int CONTENT_X = 17;
    private static final int CONTENT_Y = 11;
    private static final int MAP_WIDTH = 276;
    private static final int MAP_HEIGHT = 194;
    private static final float PLAYER_ROTATION_STEPS = 16.0F;
    private static final int PLAYER_ICON_WIDTH = 7;
    private static final int PLAYER_ICON_HEIGHT = 8;
    public static final int MARKER_SIZE = 32;
    private static final int MARKER_RADIUS = 7;
    private static final double MAX_SCALE = (double)4.0F;
    private static final double MIN_SCALE = (double)0.03125F;
    private final GuiArrowButton btnUp;
    private final GuiArrowButton btnDown;
    private final GuiArrowButton btnLeft;
    private final GuiArrowButton btnRight;
    private final GuiBookmarkButton btnExportPng;
    private final GuiBookmarkButton btnMarker;
    private final GuiBookmarkButton btnDelMarker;
    private final GuiBookmarkButton btnShowMarkers;
    private final GuiPositionButton btnPosition;
    private static final int BUTTON_PAUSE = 8;
    private int dragMouseX;
    private int dragMouseY;
    private int dragMapOffsetX;
    private int dragMapOffsetY;
    private int mapOffsetX;
    private int mapOffsetY;
    private boolean followPlayer;
    private int tileHalfSize;
    private int tile2ChunkScale;
    private DimensionMarkersData localMarkersData;
    private DimensionMarkersData globalMarkersData;
    private Marker toDelete;
    private EntityPlayer player;
    private ItemStack stack;
    private DimensionData biomeData;
    private int screenScale;
    public static int navigateStep = 24;
    private static final double MIN_SCALE_THRESHOLD = (double)0.5F;
    private static double mapScale = (double)0.5F;
    private boolean DEBUG_RENDERING = false;
    private long[] renderTimes = new long[30];
    private int renderTimesIndex = 0;
    private final GuiStates state = new GuiStates();
    private final GuiStates.IState NORMAL = new GuiStates.SimpleState();
    private final GuiStates.IState HIDING_MARKERS = new GuiStates.IState() {
        public void onEnterState() {
            GuiAtlas.this.btnShowMarkers.setSelected(false);
            GuiAtlas.this.btnShowMarkers.setTitle(I18n.getString("gui.antiqueatlas.showMarkers"));
            GuiAtlas.this.btnShowMarkers.setIconTexture(Textures.ICON_SHOW_MARKERS);
        }

        public void onExitState() {
            GuiAtlas.this.btnShowMarkers.setSelected(false);
            GuiAtlas.this.btnShowMarkers.setTitle(I18n.getString("gui.antiqueatlas.hideMarkers"));
            GuiAtlas.this.btnShowMarkers.setIconTexture(Textures.ICON_HIDE_MARKERS);
        }
    };
    private final GuiStates.IState PLACING_MARKER = new GuiStates.IState() {
        public void onEnterState() {
            GuiAtlas.this.btnMarker.setSelected(true);
        }

        public void onExitState() {
            GuiAtlas.this.btnMarker.setSelected(false);
        }
    };
    private final GuiStates.IState DELETING_MARKER = new GuiStates.IState() {
        public void onEnterState() {
            GuiAtlas.this.mc.mouseHelper.grabMouseCursor();
            GuiAtlas.this.addChild(GuiAtlas.this.eraser);
            GuiAtlas.this.btnDelMarker.setSelected(true);
        }

        public void onExitState() {
            GuiAtlas.this.mc.mouseHelper.ungrabMouseCursor();
            GuiAtlas.this.removeChild(GuiAtlas.this.eraser);
            GuiAtlas.this.btnDelMarker.setSelected(false);
        }
    };
    private final GuiCursor eraser = new GuiCursor();
    private final GuiStates.IState EXPORTING_IMAGE = new GuiStates.IState() {
        public void onEnterState() {
            GuiAtlas.this.btnExportPng.setSelected(true);
        }

        public void onExitState() {
            GuiAtlas.this.btnExportPng.setSelected(false);
        }
    };
    private GuiComponentButton selectedButton = null;
    private long timeButtonPressed = 0L;
    private boolean isDragging = false;
    private GuiScaleBar scaleBar = new GuiScaleBar();
    private GuiMarkerFinalizer markerFinalizer = new GuiMarkerFinalizer();
    private GuiBlinkingMarker blinkingIcon = new GuiBlinkingMarker();
    private ProgressBarOverlay progressBar = new ProgressBarOverlay(100, 2);

    public GuiAtlas() {
        this.setSize(310, 218);
        this.setMapScale((double)0.5F);
        this.followPlayer = true;
        this.setInterceptKeyboard(false);
        this.btnUp = GuiArrowButton.up();
        this.addChild(this.btnUp).offsetGuiCoords(148, 10);
        this.btnDown = GuiArrowButton.down();
        this.addChild(this.btnDown).offsetGuiCoords(148, 194);
        this.btnLeft = GuiArrowButton.left();
        this.addChild(this.btnLeft).offsetGuiCoords(15, 100);
        this.btnRight = GuiArrowButton.right();
        this.addChild(this.btnRight).offsetGuiCoords(283, 100);
        this.btnPosition = new GuiPositionButton();
        this.btnPosition.setEnabled(!this.followPlayer);
        this.addChild(this.btnPosition).offsetGuiCoords(283, 194);
        IButtonListener positionListener = new IButtonListener() {
            public void onClick(GuiComponentButton button) {
                GuiAtlas.this.selectedButton = button;
                if (button.equals(GuiAtlas.this.btnPosition)) {
                    GuiAtlas.this.followPlayer = true;
                    GuiAtlas.this.btnPosition.setEnabled(false);
                } else {
                    GuiAtlas.this.navigateByButton(GuiAtlas.this.selectedButton);
                    GuiAtlas.this.timeButtonPressed = GuiAtlas.this.player.worldObj.getTotalWorldTime();
                }

            }
        };
        this.btnUp.addListener(positionListener);
        this.btnDown.addListener(positionListener);
        this.btnLeft.addListener(positionListener);
        this.btnRight.addListener(positionListener);
        this.btnPosition.addListener(positionListener);
        this.btnExportPng = new GuiBookmarkButton(1, Textures.ICON_EXPORT, I18n.getString("gui.antiqueatlas.exportImage"));
        this.addChild(this.btnExportPng).offsetGuiCoords(300, 75);
        this.btnExportPng.addListener((button) -> {
            this.progressBar.reset();
            if (this.stack != null) {
                (new Thread(() -> this.exportImage(this.stack.copy()))).start();
            }

        });
        this.btnMarker = new GuiBookmarkButton(0, Textures.ICON_ADD_MARKER, I18n.getString("gui.antiqueatlas.addMarker"));
        this.addChild(this.btnMarker).offsetGuiCoords(300, 14);
        this.btnMarker.addListener((button) -> {
            if (this.stack != null) {
                if (this.state.is(this.PLACING_MARKER)) {
                    this.selectedButton = null;
                    this.state.switchTo(this.NORMAL);
                } else {
                    this.selectedButton = button;
                    this.state.switchTo(this.PLACING_MARKER);
                }
            }

        });
        this.btnDelMarker = new GuiBookmarkButton(2, Textures.ICON_DELETE_MARKER, I18n.getString("gui.antiqueatlas.delMarker"));
        this.addChild(this.btnDelMarker).offsetGuiCoords(300, 33);
        this.btnDelMarker.addListener((button) -> {
            if (this.stack != null) {
                if (this.state.is(this.DELETING_MARKER)) {
                    this.selectedButton = null;
                    this.state.switchTo(this.NORMAL);
                } else {
                    this.selectedButton = button;
                    this.state.switchTo(this.DELETING_MARKER);
                }
            }

        });
        this.btnShowMarkers = new GuiBookmarkButton(3, Textures.ICON_HIDE_MARKERS, I18n.getString("gui.antiqueatlas.hideMarkers"));
        this.addChild(this.btnShowMarkers).offsetGuiCoords(300, 52);
        this.btnShowMarkers.addListener((button) -> {
            if (this.stack != null) {
                this.selectedButton = null;
                this.state.switchTo(this.state.is(this.HIDING_MARKERS) ? this.NORMAL : this.HIDING_MARKERS);
            }

        });
        this.addChild(this.scaleBar).offsetGuiCoords(20, 198);
        this.scaleBar.setMapScale((double)1.0F);
        this.markerFinalizer.addListener(this.blinkingIcon);
        this.eraser.setTexture(Textures.ERASER, 12, 14, 2, 11);
    }

    public GuiAtlas setAtlasItemStack(ItemStack stack) {
        this.player = Minecraft.getMinecraft().thePlayer;
        this.stack = stack;
        this.updateAtlasData();
        return this;
    }

    public void initGui() {
        super.initGui();
        this.state.switchTo(this.NORMAL);
        Keyboard.enableRepeatEvents(true);
        this.screenScale = (new ScaledResolution(this.mc.gameSettings, this.mc.displayWidth, this.mc.displayHeight)).getScaleFactor();
        this.setCentered();
    }

    protected void mouseClicked(int mouseX, int mouseY, int mouseState) {
        super.mouseClicked(mouseX, mouseY, mouseState);
        if (!this.state.is(this.EXPORTING_IMAGE)) {
            int mapX = (this.width - 276) / 2;
            int mapY = (this.height - 194) / 2;
            boolean isMouseOverMap = mouseX >= mapX && mouseX <= mapX + 276 && mouseY >= mapY && mouseY <= mapY + 194;
            if (!this.state.is(this.NORMAL) && !this.state.is(this.HIDING_MARKERS)) {
                if (this.state.is(this.PLACING_MARKER) && isMouseOverMap && mouseState == 0) {
                    this.markerFinalizer.setMarkerData(this.player.worldObj, this.stack.getItemDamage(), this.player.dimension, this.screenXToWorldX(mouseX), this.screenYToWorldZ(mouseY));
                    this.addChild(this.markerFinalizer);
                    this.blinkingIcon.setTexture(MarkerTextureMap.instance().getTexture(this.markerFinalizer.selectedType), 32, 32);
                    this.addChildBehind(this.markerFinalizer, this.blinkingIcon).setRelativeCoords(mouseX - this.getGuiX() - 16, mouseY - this.getGuiY() - 16);
                    this.setInterceptKeyboard(true);
                    KeyBinding.unPressAllKeys();
                } else if (this.state.is(this.DELETING_MARKER) && this.toDelete != null && isMouseOverMap && mouseState == 0) {
                    AtlasAPI.getMarkerAPI().deleteMarker(this.player.worldObj, this.stack.getItemDamage(), this.toDelete.getId());
                }

                this.state.switchTo(this.NORMAL);
            } else if (isMouseOverMap && this.selectedButton == null) {
                this.isDragging = true;
                this.dragMouseX = mouseX;
                this.dragMouseY = mouseY;
                this.dragMapOffsetX = this.mapOffsetX;
                this.dragMapOffsetY = this.mapOffsetY;
            }

        }
    }

    private void exportImage(ItemStack stack) {
        boolean showMarkers = !this.state.is(this.HIDING_MARKERS);
        this.state.switchTo(this.EXPORTING_IMAGE);
        File file = ExportImageUtil.selectPngFileToSave("Atlas " + stack.getItemDamage(), this.progressBar);
        if (file != null) {
            try {
                Log.info("Exporting image from Atlas #%d to file %s", new Object[]{stack.getItemDamage(), file.getAbsolutePath()});
                ExportImageUtil.exportPngImage(this.biomeData, this.globalMarkersData, this.localMarkersData, file, this.progressBar, showMarkers);
                Log.info("Finished exporting image", new Object[0]);
            } catch (OutOfMemoryError e) {
                Log.error(e, "Image is too large", new Object[0]);
                this.progressBar.setStatusString(I18n.getString("gui.antiqueatlas.export.tooLarge"));
                return;
            }
        }

        this.state.switchTo(showMarkers ? this.NORMAL : this.HIDING_MARKERS);
    }

    public void handleKeyboardInput() {
        super.handleKeyboardInput();
        if (Keyboard.getEventKeyState()) {
            int key = Keyboard.getEventKey();
            if (key == 200) {
                this.navigateMap(0, navigateStep);
                return;
            }

            if (key == 208) {
                this.navigateMap(0, -navigateStep);
                return;
            }

            if (key == 203) {
                this.navigateMap(navigateStep, 0);
                return;
            }

            if (key == 205) {
                this.navigateMap(-navigateStep, 0);
                return;
            }

            if (key != 78 && key != 13) {
                if (key == 74 || key == 12) {
                    this.setMapScale(mapScale / (double)2.0F);
                }
            } else {
                this.setMapScale(mapScale * (double)2.0F);
            }
        }

    }

    public void handleMouseInput() {
        super.handleMouseInput();
        int wheelMove = Mouse.getEventDWheel();
        if (wheelMove != 0) {
            this.setMapScale(mapScale * Math.pow((double)2.0F, wheelMove > 0 ? (double)1.0F : (double)-1.0F));
        }

    }

    protected void mouseMovedOrUp(int mouseX, int mouseY, int eventButton) {
        super.mouseMovedOrUp(mouseX, mouseY, eventButton);
        if (eventButton != -1) {
            this.selectedButton = null;
            this.isDragging = false;
        }

    }

    protected void mouseClickMove(int mouseX, int mouseY, int lastMouseButton, long timeSinceMouseClick) {
        super.mouseClickMove(mouseX, mouseY, lastMouseButton, timeSinceMouseClick);
        if (this.isDragging) {
            this.followPlayer = false;
            this.btnPosition.setEnabled(true);
            this.mapOffsetX = this.dragMapOffsetX + mouseX - this.dragMouseX;
            this.mapOffsetY = this.dragMapOffsetY + mouseY - this.dragMouseY;
        }

    }

    public void updateScreen() {
        super.updateScreen();
        if (this.player != null) {
            if (this.followPlayer) {
                this.mapOffsetX = (int)(-this.player.posX * mapScale);
                this.mapOffsetY = (int)(-this.player.posZ * mapScale);
            }

            if (this.player.worldObj.getTotalWorldTime() > this.timeButtonPressed + 8L) {
                this.navigateByButton(this.selectedButton);
            }

            this.updateAtlasData();
        }
    }

    private void updateAtlasData() {
        this.biomeData = AntiqueAtlasItems.itemAtlas.getAtlasData(this.stack, this.player.worldObj).getDimensionData(this.player.dimension);
        this.globalMarkersData = AntiqueAtlasMod.globalMarkersData.getData().getMarkersDataInDimension(this.player.dimension);
        MarkersData markersData = AntiqueAtlasItems.itemAtlas.getMarkersData(this.stack, this.player.worldObj);
        if (markersData != null) {
            this.localMarkersData = markersData.getMarkersDataInDimension(this.player.dimension);
        } else {
            this.localMarkersData = null;
        }

    }

    public void navigateByButton(GuiComponentButton btn) {
        if (btn != null) {
            if (btn.equals(this.btnUp)) {
                this.navigateMap(0, navigateStep);
            } else {
                if (btn.equals(this.btnDown)) {
                    this.navigateMap(0, -navigateStep);
                } else if (btn.equals(this.btnLeft)) {
                    this.navigateMap(navigateStep, 0);
                } else if (btn.equals(this.btnRight)) {
                    this.navigateMap(-navigateStep, 0);
                }

            }
        }
    }

    public void navigateMap(int dx, int dy) {
        this.mapOffsetX += dx;
        this.mapOffsetY += dy;
        this.followPlayer = false;
        this.btnPosition.setEnabled(true);
    }

    public void setMapScale(double scale) {
        double oldScale = mapScale;
        mapScale = scale;
        if (mapScale < (double)0.03125F) {
            mapScale = (double)0.03125F;
        }

        if (mapScale > (double)4.0F) {
            mapScale = (double)4.0F;
        }

        if (mapScale >= (double)0.5F) {
            this.tileHalfSize = (int)Math.round((double)8.0F * mapScale);
            this.tile2ChunkScale = 1;
        } else {
            this.tileHalfSize = (int)Math.round((double)4.0F);
            this.tile2ChunkScale = (int)Math.round((double)0.5F / mapScale);
        }

        this.scaleBar.setMapScale(mapScale * (double)2.0F);
        this.mapOffsetX = (int)((double)this.mapOffsetX * (mapScale / oldScale));
        this.mapOffsetY = (int)((double)this.mapOffsetY * (mapScale / oldScale));
        this.dragMapOffsetX = (int)((double)this.dragMapOffsetX * (mapScale / oldScale));
        this.dragMapOffsetY = (int)((double)this.dragMapOffsetY * (mapScale / oldScale));
    }

    public void drawScreen(int mouseX, int mouseY, float par3) {
        if (this.DEBUG_RENDERING) {
            this.renderTimes[this.renderTimesIndex++] = System.currentTimeMillis();
            if (this.renderTimesIndex == this.renderTimes.length) {
                this.renderTimesIndex = 0;
                double elapsed = (double)0.0F;

                for(int i = 0; i < this.renderTimes.length - 1; ++i) {
                    elapsed += (double)(this.renderTimes[i + 1] - this.renderTimes[i]);
                }

                Log.info("GuiAtlas avg. render time: %.3f", new Object[]{elapsed / (double)this.renderTimes.length});
            }
        }

        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        GL11.glAlphaFunc(516, 0.0F);
        AtlasRenderHelper.drawFullTexture(Textures.BOOK, (double)this.getGuiX(), (double)this.getGuiY(), 310, 218);
        if (this.stack != null && this.biomeData != null) {
            if (this.state.is(this.DELETING_MARKER)) {
                GL11.glColor4f(1.0F, 1.0F, 1.0F, 0.5F);
            }

            GL11.glEnable(3089);
            GL11.glScissor((this.getGuiX() + 17) * this.screenScale, this.mc.displayHeight - (this.getGuiY() + 11 + 194) * this.screenScale, 276 * this.screenScale, 194 * this.screenScale);
            GL11.glEnable(3042);
            GL11.glBlendFunc(770, 771);
            int mapStartX = MathUtil.roundToBase((int)Math.floor(-((double)138.0F + (double)this.mapOffsetX + (double)(2 * this.tileHalfSize)) / mapScale / (double)16.0F), this.tile2ChunkScale);
            int mapStartZ = MathUtil.roundToBase((int)Math.floor(-((double)97.0F + (double)this.mapOffsetY + (double)(2 * this.tileHalfSize)) / mapScale / (double)16.0F), this.tile2ChunkScale);
            int mapEndX = MathUtil.roundToBase((int)Math.ceil(((double)138.0F - (double)this.mapOffsetX + (double)(2 * this.tileHalfSize)) / mapScale / (double)16.0F), this.tile2ChunkScale);
            int mapEndZ = MathUtil.roundToBase((int)Math.ceil(((double)97.0F - (double)this.mapOffsetY + (double)(2 * this.tileHalfSize)) / mapScale / (double)16.0F), this.tile2ChunkScale);
            int mapStartScreenX = this.getGuiX() + 155 + (int)((double)(mapStartX << 4) * mapScale) + this.mapOffsetX;
            int mapStartScreenY = this.getGuiY() + 109 + (int)((double)(mapStartZ << 4) * mapScale) + this.mapOffsetY;
            TileRenderIterator iter = new TileRenderIterator(this.biomeData);
            iter.setScope((new Rect()).setOrigin(mapStartX, mapStartZ).set(mapStartX, mapStartZ, mapEndX, mapEndZ));
            iter.setStep(this.tile2ChunkScale);

            while(iter.hasNext()) {
                for(SubTile subtile : iter.next()) {
                    if (subtile != null && subtile.tile != null) {
                        AtlasRenderHelper.drawAutotileCorner(BiomeTextureMap.instance().getTexture(subtile.tile), mapStartScreenX + subtile.x * this.tileHalfSize, mapStartScreenY + subtile.y * this.tileHalfSize, subtile.getTextureU(), subtile.getTextureV(), this.tileHalfSize);
                    }
                }
            }

            if (!this.state.is(this.HIDING_MARKERS)) {
                int markersStartX = MathUtil.roundToBase(mapStartX, 8) / 8 - 1;
                int markersStartZ = MathUtil.roundToBase(mapStartZ, 8) / 8 - 1;
                int markersEndX = MathUtil.roundToBase(mapEndX, 8) / 8 + 1;
                int markersEndZ = MathUtil.roundToBase(mapEndZ, 8) / 8 + 1;
                double iconScale = this.getIconScale();

                for(int x = markersStartX; x <= markersEndX; ++x) {
                    for(int z = markersStartZ; z <= markersEndZ; ++z) {
                        List<Marker> markers = this.globalMarkersData.getMarkersAtChunk(x, z);
                        if (markers != null) {
                            for(Marker marker : markers) {
                                this.renderMarker(marker, iconScale);
                            }
                        }
                    }
                }

                if (this.localMarkersData != null) {
                    for(int x = markersStartX; x <= markersEndX; ++x) {
                        for(int z = markersStartZ; z <= markersEndZ; ++z) {
                            List<Marker> markers = this.localMarkersData.getMarkersAtChunk(x, z);
                            if (markers != null) {
                                for(Marker marker : markers) {
                                    this.renderMarker(marker, iconScale);
                                }
                            }
                        }
                    }
                }
            }

            GL11.glDisable(3089);
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            AtlasRenderHelper.drawFullTexture(Textures.BOOK_FRAME, (double)this.getGuiX(), (double)this.getGuiY(), 310, 218);
            double iconScale = this.getIconScale();
            if (!this.state.is(this.HIDING_MARKERS)) {
                int playerOffsetX = (int)(this.player.posX * mapScale) + this.mapOffsetX;
                int playerOffsetZ = (int)(this.player.posZ * mapScale) + this.mapOffsetY;
                if (playerOffsetX < -138) {
                    playerOffsetX = -138;
                }

                if (playerOffsetX > 138) {
                    playerOffsetX = 138;
                }

                if (playerOffsetZ < -97) {
                    playerOffsetZ = -97;
                }

                if (playerOffsetZ > 95) {
                    playerOffsetZ = 95;
                }

                GL11.glColor4f(1.0F, 1.0F, 1.0F, this.state.is(this.PLACING_MARKER) ? 0.5F : 1.0F);
                GL11.glPushMatrix();
                GL11.glTranslated((double)(this.getGuiX() + 155 + playerOffsetX), (double)(this.getGuiY() + 109 + playerOffsetZ), (double)0.0F);
                float playerRotation = (float)Math.round(this.player.rotationYaw / 360.0F * 16.0F) / 16.0F * 360.0F;
                GL11.glRotatef(180.0F + playerRotation, 0.0F, 0.0F, 1.0F);
                GL11.glTranslated((double)-3.0F * iconScale, (double)-4.0F * iconScale, (double)0.0F);
                AtlasRenderHelper.drawFullTexture(Textures.PLAYER, (double)0.0F, (double)0.0F, (int)Math.round((double)7.0F * iconScale), (int)Math.round((double)8.0F * iconScale));
                GL11.glPopMatrix();
                GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            }

            super.drawScreen(mouseX, mouseY, par3);
            GL11.glEnable(3042);
            GL11.glBlendFunc(770, 771);
            if (this.state.is(this.PLACING_MARKER)) {
                GL11.glColor4f(1.0F, 1.0F, 1.0F, 0.5F);
                AtlasRenderHelper.drawFullTexture(MarkerTextureMap.instance().getTexture(this.markerFinalizer.selectedType), (double)mouseX - (double)16.0F * iconScale, (double)mouseY - (double)16.0F * iconScale, (int)Math.round((double)32.0F * iconScale), (int)Math.round((double)32.0F * iconScale));
                GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            }

            if (this.state.is(this.EXPORTING_IMAGE)) {
                this.drawDefaultBackground();
                this.progressBar.draw((this.width - 100) / 2, this.height / 2 - 34);
            }

        }
    }

    private void renderMarker(Marker marker, double scale) {
        int markerX = this.worldXToScreenX(marker.getX());
        int markerY = this.worldZToScreenY(marker.getZ());
        if (marker.isVisibleAhead() || this.biomeData.hasTileAt(marker.getChunkX(), marker.getChunkZ())) {
            boolean mouseIsOverMarker = this.isMouseInRadius(markerX, markerY, (int)Math.ceil((double)7.0F * scale));
            if (this.state.is(this.PLACING_MARKER)) {
                GL11.glColor4f(1.0F, 1.0F, 1.0F, 0.5F);
            } else if (this.state.is(this.DELETING_MARKER)) {
                if (marker.isGlobal()) {
                    GL11.glColor4f(1.0F, 1.0F, 1.0F, 0.5F);
                } else if (mouseIsOverMarker) {
                    GL11.glColor4f(0.5F, 0.5F, 0.5F, 1.0F);
                    this.toDelete = marker;
                } else {
                    GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
                    if (this.toDelete == marker) {
                        this.toDelete = null;
                    }
                }
            } else {
                GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            }

            AtlasRenderHelper.drawFullTexture(MarkerTextureMap.instance().getTexture(marker.getType()), (double)markerX - (double)16.0F * scale, (double)markerY - (double)16.0F * scale, (int)Math.round((double)32.0F * scale), (int)Math.round((double)32.0F * scale));
            if (this.isMouseOver && mouseIsOverMarker && marker.getLabel().length() > 0) {
                this.drawTooltip(Arrays.asList(marker.getLocalizedLabel()), this.mc.fontRenderer);
            }

        }
    }

    public boolean doesGuiPauseGame() {
        return false;
    }

    public void onGuiClosed() {
        super.onGuiClosed();
        this.removeChild(this.markerFinalizer);
        this.removeChild(this.blinkingIcon);
        Keyboard.enableRepeatEvents(false);
    }

    private int screenXToWorldX(int mouseX) {
        return (int)Math.round((double)(mouseX - this.width / 2 - this.mapOffsetX) / mapScale);
    }

    private int screenYToWorldZ(int mouseY) {
        return (int)Math.round((double)(mouseY - this.height / 2 - this.mapOffsetY) / mapScale);
    }

    private int worldXToScreenX(int x) {
        return (int)Math.round((double)x * mapScale + (double)(this.width / 2) + (double)this.mapOffsetX);
    }

    private int worldZToScreenY(int z) {
        return (int)Math.round((double)z * mapScale + (double)(this.height / 2) + (double)this.mapOffsetY);
    }

    protected void onChildClosed(GuiComponent child) {
        if (child.equals(this.markerFinalizer)) {
            this.removeChild(this.blinkingIcon);
        }

    }

    public void updateL18n() {
        this.btnExportPng.setTitle(I18n.getString("gui.antiqueatlas.exportImage"));
        this.btnMarker.setTitle(I18n.getString("gui.antiqueatlas.addMarker"));
    }

    private double getIconScale() {
        return AntiqueAtlasMod.settings.doScaleMarkers ? (mapScale < (double)0.5F ? (double)0.5F : (mapScale > (double)1.0F ? (double)2.0F : (double)1.0F)) : (double)1.0F;
    }
}
