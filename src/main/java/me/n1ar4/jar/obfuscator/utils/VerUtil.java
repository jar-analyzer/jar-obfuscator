package me.n1ar4.jar.obfuscator.utils;

public class VerUtil {
    /**
     * Is Java 8
     *
     * @return is java 8
     */
    public static boolean isJava8() {
        String javaVersion = System.getProperty("java.version");
        return javaVersion.startsWith("1.8.");
    }

}
