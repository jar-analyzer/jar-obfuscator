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

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DescUtil {
    public static List<String> extractClassNames(String descriptor) {
        List<String> classNames = new ArrayList<>();
        Pattern pattern = Pattern.compile("L([^;]+);");
        Matcher matcher = pattern.matcher(descriptor);
        while (matcher.find()) {
            classNames.add(matcher.group(1));
        }
        return classNames;
    }
}
