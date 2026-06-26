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

package me.n1ar4.jar.obfuscator.asm;

import me.n1ar4.jar.obfuscator.Const;
import me.n1ar4.jar.obfuscator.utils.BytecodeRemapUtil;
import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.Type;

public class AnnotationRemapVisitor extends AnnotationVisitor {
    private final String annotationOwner;
    private final boolean remapElementNames;

    public AnnotationRemapVisitor(AnnotationVisitor annotationVisitor,
                                  String descriptor,
                                  boolean remapElementNames) {
        super(Const.ASMVersion, annotationVisitor);
        this.annotationOwner = getOwner(descriptor);
        this.remapElementNames = remapElementNames;
    }

    private static String getOwner(String descriptor) {
        if (descriptor == null) {
            return null;
        }
        try {
            return Type.getType(descriptor).getInternalName();
        } catch (Exception ignored) {
            return null;
        }
    }

    private String remapName(String name) {
        if (!remapElementNames) {
            return name;
        }
        return BytecodeRemapUtil.remapAnnotationElementName(annotationOwner, name);
    }

    private Object remapValue(Object value) {
        if (value instanceof Type) {
            return BytecodeRemapUtil.remapType((Type) value);
        }
        return value;
    }

    @Override
    public void visit(String name, Object value) {
        super.visit(remapName(name), remapValue(value));
    }

    @Override
    public void visitEnum(String name, String descriptor, String value) {
        super.visitEnum(remapName(name), BytecodeRemapUtil.remapDesc(descriptor), value);
    }

    @Override
    public AnnotationVisitor visitAnnotation(String name, String descriptor) {
        String newDescriptor = BytecodeRemapUtil.remapDesc(descriptor);
        AnnotationVisitor av = super.visitAnnotation(remapName(name), newDescriptor);
        return new AnnotationRemapVisitor(av, newDescriptor, remapElementNames);
    }

    @Override
    public AnnotationVisitor visitArray(String name) {
        AnnotationVisitor av = super.visitArray(remapName(name));
        return new AnnotationRemapVisitor(av, null, remapElementNames);
    }
}
