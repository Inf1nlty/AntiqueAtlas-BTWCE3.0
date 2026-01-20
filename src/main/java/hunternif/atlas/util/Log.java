package hunternif.atlas.util;

import api.AddonHandler;

import java.util.logging.Level;

public class Log {
    private static String modID = "AntiqueAtlas";

    public static void setModID(String id) {
        modID = id;
    }


    public static void log(Level level, Throwable ex, String msg, Object... data) {

        String formattedMsg = formatMessage(msg, data);

        if (ex != null) {
            formattedMsg += " [Exception: " + ex.getMessage() + "]";
        }

        if (level == Level.SEVERE || level == Level.WARNING) {
            AddonHandler.logMessage("[" + modID + "] " + level.getName() + ": " + formattedMsg);
            if (ex != null) {
                ex.printStackTrace();
            }
        } else if (level == Level. INFO) {
            AddonHandler.logMessage("[" + modID + "] " + formattedMsg);
        } else {

            System.out.println("[" + modID + "] " + level.getName() + ": " + formattedMsg);
        }
    }

    private static String formatMessage(String msg, Object...  data) {
        if (data == null || data.length == 0) {
            return msg;
        }

        String result = msg;
        for (Object obj : data) {

            result = result.replaceFirst("\\{\\}", obj != null ? obj.toString() : "null");
        }
        return result;
    }

    public static void debug(String msg, Object... data) {
        log(Level. FINE, null, msg, data);
    }

    public static void info(String msg, Object... data) {
        log(Level.INFO, null, msg, data);
    }

    public static void warn(String msg, Object... data) {
        log(Level.WARNING, null, msg, data);
    }

    public static void warn(Throwable ex, String msg, Object... data) {
        log(Level.WARNING, ex, msg, data);
    }

    public static void error(String msg, Object... data) {
        log(Level.SEVERE, null, msg, data);
    }

    public static void error(Throwable ex, String msg, Object... data) {
        log(Level. SEVERE, ex, msg, data);
    }
}