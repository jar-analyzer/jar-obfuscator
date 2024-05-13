package me.n1ar4.jar.obfuscator.core;

import me.n1ar4.jar.obfuscator.base.ClassField;
import me.n1ar4.jar.obfuscator.base.ClassReference;
import me.n1ar4.jar.obfuscator.base.MethodReference;
import me.n1ar4.jar.obfuscator.config.BaseConfig;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ObfEnv {
    public static String MAIN_CLASS = null;
    public static String NEW_MAIN_CLASS = null;
    public static BaseConfig config = null;
    public static String ADVANCE_STRING_NAME = null;
    public static Map<String, String> classNameObfMapping = new HashMap<>();
    public static Map<MethodReference.Handle, MethodReference.Handle> methodNameObfMapping = new HashMap<>();
    public static Map<ClassField, ClassField> fieldNameObfMapping = new HashMap<>();
    public static final HashMap<ClassReference.Handle, ArrayList<String>> stringInClass = new HashMap<>();
    public static final HashMap<String, ArrayList<String>> newStringInClass = new HashMap<>();
}
