package me.n1ar4.jar.obfuscator.utils;

/**
 * OS Util
 */
public class OSUtil {
    /**
     * Is windows
     *
     * @return yes/no
     */
    public static boolean isWin() {
        return System.getProperty("os.name").toLowerCase().contains("windows");
    }

    /**
     * Is arch 64
     *
     * @return yes/no
     */
    public static boolean isArch64() {
        return System.getProperty("sun.arch.data.model").equalsIgnoreCase("64");
    }
}
