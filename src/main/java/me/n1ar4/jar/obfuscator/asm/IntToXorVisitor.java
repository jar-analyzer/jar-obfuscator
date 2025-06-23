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

package me.n1ar4.jar.obfuscator.asm;

import me.n1ar4.jar.obfuscator.Const;
import me.n1ar4.jrandom.core.JRandom;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

public class IntToXorVisitor extends ClassVisitor {
    public IntToXorVisitor(ClassVisitor cv) {
        super(Opcodes.ASM9, cv);
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
        MethodVisitor mv = cv.visitMethod(access, name, descriptor, signature, exceptions);
        return new XORMethodAdapter(mv);
    }

    private static class XORMethodAdapter extends MethodVisitor {
        XORMethodAdapter(MethodVisitor mv) {
            super(Const.ASMVersion, mv);
        }

        @Override
        public void visitInsn(int opcode) {
            if (opcode >= Opcodes.ICONST_M1 && opcode <= Opcodes.ICONST_5) {
                int value = opcode - Opcodes.ICONST_0;
                replaceIntWithXor(value);
            } else {
                super.visitInsn(opcode);
            }
        }

        @Override
        public void visitIntInsn(int opcode, int operand) {
            if (opcode == Opcodes.BIPUSH || opcode == Opcodes.SIPUSH) {
                replaceIntWithXor(operand);
            } else {
                super.visitIntInsn(opcode, operand);
            }
        }

        @Override
        public void visitLdcInsn(Object cst) {
            if (cst instanceof Integer) {
                replaceIntWithXor((Integer) cst);
            } else {
                super.visitLdcInsn(cst);
            }
        }

        private void replaceIntWithXor(int value) {
            JRandom rand = JRandom.getInstance();
            int partA = 10000000 + rand.getInt(0, 90000000);
            int partB = partA ^ value;
            super.visitLdcInsn(partA);
            super.visitLdcInsn(partB);
            super.visitInsn(Opcodes.IXOR);
        }
    }
}