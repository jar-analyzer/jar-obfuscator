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

import me.n1ar4.jar.obfuscator.base.ClassReference;
import me.n1ar4.jar.obfuscator.base.MethodReference;
import org.objectweb.asm.Handle;
import org.objectweb.asm.MethodVisitor;

import java.util.HashMap;
import java.util.HashSet;

public class MethodCallMethodVisitor extends MethodVisitor {
    private final HashSet<MethodReference.Handle> calledMethods;

    public MethodCallMethodVisitor(final int api, final MethodVisitor mv,
                                   final String owner, String name, String desc,
                                   HashMap<MethodReference.Handle,
                                           HashSet<MethodReference.Handle>> methodCalls) {
        super(api, mv);
        this.calledMethods = new HashSet<>();
        methodCalls.put(
                new MethodReference.Handle(
                        new ClassReference.Handle(owner), name, desc), calledMethods);
    }

    @Override
    public void visitMethodInsn(int opcode, String owner, String name, String desc, boolean itf) {
        calledMethods.add(
                new MethodReference.Handle(
                        new ClassReference.Handle(owner), name, desc));
        super.visitMethodInsn(opcode, owner, name, desc, itf);
    }

    @Override
    public void visitInvokeDynamicInsn(String name, String descriptor,
                                       Handle bootstrapMethodHandle,
                                       Object... bootstrapMethodArguments) {
        for (Object bsmArg : bootstrapMethodArguments) {
            if (bsmArg instanceof Handle) {
                Handle handle = (Handle) bsmArg;
                calledMethods.add(new MethodReference.Handle(
                        new ClassReference.Handle(handle.getOwner()),
                        handle.getName(), handle.getDesc()));
            }
        }
        super.visitInvokeDynamicInsn(name, descriptor,
                bootstrapMethodHandle, bootstrapMethodArguments);
    }
}
