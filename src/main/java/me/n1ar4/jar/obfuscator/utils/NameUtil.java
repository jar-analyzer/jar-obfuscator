/*
 * MIT License
 *
 * Project URL: https://github.com/jar-analyzer/jar-obfuscator
 *
 * Copyright (c) 2024-2025 4ra1n (https://github.com/4ra1n)
 *
 * This project is distributed under the MIT license.
 *
 * https://opensource.org/license/mit
 */

package me.n1ar4.jar.obfuscator.utils;

import me.n1ar4.jrandom.core.JRandom;

import java.util.HashSet;

@SuppressWarnings("all")
public class NameUtil {
    public static char[] CHAR_POOL = null;
    private static final HashSet<String> generatedStrings = new HashSet<>();
    private static final HashSet<String> generatedMethods = new HashSet<>();
    private static final HashSet<String> generatedFields = new HashSet<>();
    private static final HashSet<String> packageNames = new HashSet<>();

    public static String genNewName() {
        return genBase(1);
    }

    public static String genNewMethod() {
        return genBase(2);
    }

    public static String genNewFields() {
        return genBase(3);
    }

    public static String genPackage() {
        return genBase(4);
    }

    public static String genWithSet(HashSet<String> exists) {
        JRandom random = JRandom.getInstance();
        while (true) {
            int length = 10 + random.getInt(0, 3);
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < length; i++) {
                sb.append(CHAR_POOL[random.getInt(0, CHAR_POOL.length)]);
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
        JRandom random = JRandom.getInstance();
        while (true) {
            int length = 10 + random.getInt(0, 3);
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < length; i++) {
                sb.append(CHAR_POOL[random.getInt(0, CHAR_POOL.length)]);
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
            } else if (op == 4) {
                if (!packageNames.contains(result)) {
                    packageNames.add(result);
                    return result;
                }
            } else {
                return null;
            }
        }
    }
}
