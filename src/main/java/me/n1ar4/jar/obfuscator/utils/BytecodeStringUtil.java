/*
 * MIT License
 *
 * Project URL: https://github.com/jar-analyzer/jar-obfuscator
 *
 * Copyright (c) 2024-2026 4ra1n (https://github.com/4ra1n)
 *
 * This project is distributed under the MIT license.
 *
 * https://opensource.org/license/mit
 */

package me.n1ar4.jar.obfuscator.utils;

public class BytecodeStringUtil {
    private static final int MAX_CONSTANT_UTF8_LENGTH = 65535;

    private BytecodeStringUtil() {
    }

    public static boolean canStoreAsConstantUtf8(String value) {
        return value != null && modifiedUtf8Length(value) <= MAX_CONSTANT_UTF8_LENGTH;
    }

    private static int modifiedUtf8Length(String value) {
        int length = 0;
        for (int i = 0; i < value.length(); i++) {
            char c = value.charAt(i);
            if (c >= 0x0001 && c <= 0x007F) {
                length++;
            } else if (c <= 0x07FF) {
                length += 2;
            } else {
                length += 3;
            }
            if (length > MAX_CONSTANT_UTF8_LENGTH) {
                return length;
            }
        }
        return length;
    }
}
