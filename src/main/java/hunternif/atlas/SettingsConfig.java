package hunternif.atlas;

import java.io.*;
import java.util. Properties;

public class SettingsConfig {
    private static final int VERSION = 3;

    public boolean doSaveBrowsingPos = true;
    public boolean autoDeathMarker = true;
    public boolean autoVillageMarkers = true;
    public boolean doScaleMarkers = false;
    public double defaultScale = 0.5;
    public double minScale = 0.03125;
    public double maxScale = 4.0;
    public boolean doReverseWheelZoom = false;
    public int scanRadius = 11;
    public boolean forceChunkLoading = false;
    public float newScanInterval = 1.0F;
    public boolean doRescan = true;
    public int rescanRate = 4;
    public boolean doScanPonds = true;

    public void load(File file) {
        Properties props = new Properties();

        if (file.exists()) {
            try (FileInputStream fis = new FileInputStream(file)) {
                props.load(fis);

                doSaveBrowsingPos = Boolean.parseBoolean(props.getProperty("do_save_browsing_pos", "true"));
                autoDeathMarker = Boolean.parseBoolean(props.getProperty("auto_death_marker", "true"));
                autoVillageMarkers = Boolean.parseBoolean(props. getProperty("auto_village_markers", "true"));
                defaultScale = Double.parseDouble(props.getProperty("default_scale", "0.5"));
                minScale = Double.parseDouble(props.getProperty("min_scale", "0.03125"));
                maxScale = Double.parseDouble(props.getProperty("max_scale", "4.0"));
                doScaleMarkers = Boolean.parseBoolean(props.getProperty("do_scale_markers", "false"));
                doReverseWheelZoom = Boolean.parseBoolean(props.getProperty("do_reverse_wheel_zoom", "false"));
                scanRadius = Integer.parseInt(props.getProperty("area_scan_radius", "11"));
                forceChunkLoading = Boolean.parseBoolean(props.getProperty("force_chunk_loading", "false"));
                newScanInterval = Float.parseFloat(props.getProperty("area_scan_interval", "1.0"));
                doRescan = Boolean.parseBoolean(props. getProperty("do_rescan", "true"));
                rescanRate = Integer.parseInt(props.getProperty("area_rescan_rate", "4"));
                doScanPonds = Boolean.parseBoolean(props.getProperty("do_scan_ponds", "true"));

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void save(File file) {
        Properties props = new Properties();

        props.setProperty("do_save_browsing_pos", String.valueOf(doSaveBrowsingPos));
        props.setProperty("auto_death_marker", String.valueOf(autoDeathMarker));
        props.setProperty("auto_village_markers", String.valueOf(autoVillageMarkers));
        props.setProperty("default_scale", String.valueOf(defaultScale));
        props.setProperty("min_scale", String.valueOf(minScale));
        props.setProperty("max_scale", String.valueOf(maxScale));
        props.setProperty("do_scale_markers", String.valueOf(doScaleMarkers));
        props.setProperty("do_reverse_wheel_zoom", String. valueOf(doReverseWheelZoom));
        props.setProperty("area_scan_radius", String.valueOf(scanRadius));
        props.setProperty("force_chunk_loading", String.valueOf(forceChunkLoading));
        props.setProperty("area_scan_interval", String.valueOf(newScanInterval));
        props.setProperty("do_rescan", String.valueOf(doRescan));
        props.setProperty("area_rescan_rate", String.valueOf(rescanRate));
        props.setProperty("do_scan_ponds", String. valueOf(doScanPonds));

        try (FileOutputStream fos = new FileOutputStream(file)) {
            props.store(fos, "Antique Atlas Configuration");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}