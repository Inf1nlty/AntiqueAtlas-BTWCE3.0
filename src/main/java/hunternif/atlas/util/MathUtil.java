package hunternif.atlas.util;

public class MathUtil {
    public static int roundToBase(int a, int base) {
        return a - a % base;
    }

    public static int ceilAbsToBase(int a, int base) {
        int ceil = a - a % base;
        return a >= 0 ? (a > ceil ? ceil + base : ceil) : (a < ceil ? ceil - base : ceil);
    }
}
