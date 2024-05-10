package me.n1ar4.jar.obfuscator.utils;

import java.security.SecureRandom;
import java.util.HashSet;


public class NameUtil {
    public static char[] CHAR_POOL = null;
    private static final SecureRandom random = new SecureRandom();
    private static final HashSet<String> generatedStrings = new HashSet<>();
    private static final HashSet<String> generatedMethods = new HashSet<>();
    private static final HashSet<String> generatedFields = new HashSet<>();

    static {
        random.setSeed(System.currentTimeMillis());
    }

    public static String genNewName() {
        return genBase(1);
    }

    public static String genNewMethod() {
        return genBase(2);
    }

    public static String genNewFields() {
        return genBase(3);
    }

    public static String genWithSet(HashSet<String> exists) {
        while (true) {
            int length = 10 + random.nextInt(3);
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < length; i++) {
                sb.append(CHAR_POOL[random.nextInt(CHAR_POOL.length)]);
            }
            if (sb.charAt(0) == '~' || sb.charAt(0) == '1') {
                continue;
            }
            String result = sb.toString();
            if (!exists.contains(result)) {
                exists.add(result);
                return result;
            }
        }
    }

    private static String genBase(int op) {
        while (true) {
            int length = 10 + random.nextInt(3);
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < length; i++) {
                sb.append(CHAR_POOL[random.nextInt(CHAR_POOL.length)]);
            }
            if (sb.charAt(0) == '~' || sb.charAt(0) == '1') {
                continue;
            }
            String result = sb.toString();
            if (op == 2) {
                if (!generatedMethods.contains(result)) {
                    generatedMethods.add(result);
                    return result;
                }
            } else if (op == 1) {
                if (!generatedStrings.contains(result)) {
                    generatedStrings.add(result);
                    return result;
                }
            } else if (op == 3) {
                if (!generatedFields.contains(result)) {
                    generatedFields.add(result);
                    return result;
                }
            } else {
                return null;
            }
        }
    }
}
