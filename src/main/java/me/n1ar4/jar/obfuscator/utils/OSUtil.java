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
    @SuppressWarnings("all")
    public static boolean isArch64() {
        return System.getProperty("sun.arch.data.model").equalsIgnoreCase("64");
    }

    /**
     * Is macOS
     *
     * @return yes/no
     */
    public static boolean isMac() {
        String osName = System.getProperty("os.name").toLowerCase();
        return osName.contains("mac") || osName.contains("darwin");
    }

    public enum OSType {
        WINDOWS, LINUX, MAC, UNKNOWN
    }

    public static OSType getOSType() {
        String osName = System.getProperty("os.name").toLowerCase();
        if (osName.contains("win")) {
            return OSType.WINDOWS;
        } else if (osName.contains("nux") || osName.contains("nix")) {
            return OSType.LINUX;
        } else if (osName.contains("mac")) {
            return OSType.MAC;
        } else {
            return OSType.UNKNOWN;
        }
    }
}
