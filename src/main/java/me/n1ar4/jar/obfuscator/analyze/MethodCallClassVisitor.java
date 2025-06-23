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

package me.n1ar4.jar.obfuscator.analyze;

import me.n1ar4.jar.obfuscator.Const;
import me.n1ar4.jar.obfuscator.base.MethodReference;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;

import java.util.HashMap;
import java.util.HashSet;

public class MethodCallClassVisitor extends ClassVisitor {
    private String name;

    private final HashMap<MethodReference.Handle, HashSet<MethodReference.Handle>> methodCalls;

    public MethodCallClassVisitor(HashMap<MethodReference.Handle,
            HashSet<MethodReference.Handle>> methodCalls) {
        super(Const.ASMVersion);
        this.methodCalls = methodCalls;
    }

    @Override
    public void visit(int version, int access, String name, String signature,
                      String superName, String[] interfaces) {
        super.visit(version, access, name, signature, superName, interfaces);
        this.name = name;
    }

    public MethodVisitor visitMethod(int access, String name, String desc,
                                     String signature, String[] exceptions) {
        MethodVisitor mv = super.visitMethod(access, name, desc, signature, exceptions);
        return new MethodCallMethodVisitor(api, mv, this.name, name, desc, methodCalls);
    }
}
