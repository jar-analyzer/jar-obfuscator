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

import me.n1ar4.jar.obfuscator.base.ClassReference;
import me.n1ar4.jar.obfuscator.base.MethodReference;
import me.n1ar4.jar.obfuscator.core.ObfEnv;
import org.objectweb.asm.Handle;
import org.objectweb.asm.Type;

import java.util.List;
import java.util.Map;

public class BytecodeRemapUtil {
    private BytecodeRemapUtil() {
    }

    public static String remapClassName(String name) {
        if (name == null) {
            return null;
        }
        return ObfEnv.classNameObfMapping.getOrDefault(name, name);
    }

    public static String remapDesc(String descriptor) {
        if (descriptor == null) {
            return null;
        }
        List<String> classes = DescUtil.extractClassNames(descriptor);
        for (String c : classes) {
            descriptor = descriptor.replace(c, remapClassName(c));
        }
        return descriptor;
    }

    public static Type remapType(Type type) {
        if (type == null) {
            return null;
        }
        if (type.getSort() == Type.OBJECT) {
            return Type.getObjectType(remapClassName(type.getInternalName()));
        }
        if (type.getSort() == Type.ARRAY || type.getSort() == Type.METHOD) {
            return Type.getType(remapDesc(type.getDescriptor()));
        }
        return type;
    }

    public static Handle remapHandle(Handle handle) {
        if (handle == null) {
            return null;
        }
        return new Handle(
                handle.getTag(),
                remapClassName(handle.getOwner()),
                handle.getName(),
                remapDesc(handle.getDesc()),
                handle.isInterface());
    }

    public static String remapAnnotationElementName(String annotationOwner, String elementName) {
        if (annotationOwner == null || elementName == null) {
            return elementName;
        }
        for (Map.Entry<MethodReference.Handle, MethodReference.Handle> entry :
                ObfEnv.methodNameObfMapping.entrySet()) {
            MethodReference.Handle oldHandle = entry.getKey();
            if (oldHandle.getClassReference().equals(new ClassReference.Handle(annotationOwner)) &&
                    oldHandle.getName().equals(elementName)) {
                return entry.getValue().getName();
            }
        }
        return elementName;
    }
}
