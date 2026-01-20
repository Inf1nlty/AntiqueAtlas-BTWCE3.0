package hunternif.atlas.network;

import java.io.UnsupportedEncodingException;

public class PacketUtils {

    public static int getPacketSizeOfString(String str) {
        if (str == null) {
            return 2;
        }
        try {
            byte[] bytes = str.getBytes("UTF-8");
            return 2 + bytes.length;
        } catch (UnsupportedEncodingException e) {
            // UTF-8 should always be supported
            return 2 + str.length() * 3; // Worst case: 3 bytes per char
        }
    }
}