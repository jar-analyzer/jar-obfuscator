package me.n1ar4.jar.obfuscator.core;

import me.n1ar4.jar.obfuscator.base.ClassFileEntity;
import me.n1ar4.jar.obfuscator.base.ClassReference;
import me.n1ar4.jar.obfuscator.base.MethodReference;

import java.util.*;

public class AnalyzeEnv {
    public static Set<ClassFileEntity> classFileList = new HashSet<>();
    public static final Set<ClassReference> discoveredClasses = new HashSet<>();
    public static final Set<MethodReference> discoveredMethods = new HashSet<>();
    public static final Map<ClassReference.Handle, List<MethodReference>> methodsInClassMap = new HashMap<>();
    public static final Map<String, List<String>> fieldsInClassMap = new HashMap<>();
    public static final Map<ClassReference.Handle, ClassReference> classMap = new HashMap<>();
    public static final Map<MethodReference.Handle, MethodReference> methodMap = new HashMap<>();
    public static final HashMap<MethodReference.Handle,
            HashSet<MethodReference.Handle>> methodCalls = new HashMap<>();
}
