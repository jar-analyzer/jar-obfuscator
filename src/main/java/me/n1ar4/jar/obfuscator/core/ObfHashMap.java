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

package me.n1ar4.jar.obfuscator.core;

import java.util.HashMap;


public class ObfHashMap extends HashMap<String, String> {
    @Override
    public String put(String key, String value) {
        value = value.replaceAll("^[/\\\\]+", "");
        value = value.replaceAll("[/\\\\]+$", "");
        if (value.endsWith(".class")) {
            value = value.substring(0, value.length() - 6);
        }
        return super.put(key, value);
    }

    @Override
    public String putIfAbsent(String key, String value) {
        value = value.replaceAll("^[/\\\\]+", "");
        value = value.replaceAll("[/\\\\]+$", "");
        if (value.endsWith(".class")) {
            value = value.substring(0, value.length() - 6);
        }
        return super.putIfAbsent(key, value);
    }
}