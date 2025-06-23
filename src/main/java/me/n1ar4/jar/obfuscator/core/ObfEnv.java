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

import me.n1ar4.jar.obfuscator.base.ClassField;
import me.n1ar4.jar.obfuscator.base.ClassReference;
import me.n1ar4.jar.obfuscator.base.MethodReference;
import me.n1ar4.jar.obfuscator.config.BaseConfig;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ObfEnv {
    public static BaseConfig config = null;
    public static String ADVANCE_STRING_NAME = null;
    public static Map<String, String> classNameObfMapping = new ObfHashMap();
    public static Map<String, List<String>> ignoredClassMethodsMapping = new HashMap<>();
    public static Map<MethodReference.Handle, MethodReference.Handle> methodNameObfMapping = new HashMap<>();
    public static Map<ClassField, ClassField> fieldNameObfMapping = new HashMap<>();
    public static final HashMap<ClassReference.Handle, ArrayList<String>> stringInClass = new HashMap<>();
    public static final HashMap<String, ArrayList<String>> newStringInClass = new HashMap<>();
}
