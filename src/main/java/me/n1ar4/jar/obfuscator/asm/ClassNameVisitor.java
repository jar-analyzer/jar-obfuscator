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
import me.n1ar4.jar.obfuscator.core.ObfEnv;
import me.n1ar4.jar.obfuscator.utils.DescUtil;
import org.objectweb.asm.*;

import java.util.List;

@SuppressWarnings("all")
public class ClassNameVisitor extends ClassVisitor {
    public ClassNameVisitor(ClassVisitor classVisitor) {
        super(Const.ASMVersion, classVisitor);
    }

    @Override
    public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
        name = ObfEnv.classNameObfMapping.getOrDefault(name, name);
        superName = ObfEnv.classNameObfMapping.getOrDefault(superName, superName);
        for (int i = 0; i < interfaces.length; i++) {
            interfaces[i] = ObfEnv.classNameObfMapping.getOrDefault(interfaces[i], interfaces[i]);
        }
        if (signature != null) {
            List<String> sig = DescUtil.extractClassNames(signature);
            for (String c : sig) {
                String co = ObfEnv.classNameObfMapping.getOrDefault(c, c);
                signature = signature.replace(c, co);
            }
        }
        super.visit(version, access, name, signature, superName, interfaces);
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
        List<String> s = DescUtil.extractClassNames(desc);
        for (String c : s) {
            String co = ObfEnv.classNameObfMapping.getOrDefault(c, c);
            desc = desc.replace(c, co);
        }
        if (signature != null) {
            List<String> sig = DescUtil.extractClassNames(signature);
            for (String c : sig) {
                String co = ObfEnv.classNameObfMapping.getOrDefault(c, c);
                signature = signature.replace(c, co);
            }
        }
        MethodVisitor mv = super.visitMethod(access, name, desc, signature, exceptions);
        return new ClassNameChangerMethodAdapter(mv);
    }

    @Override
    public AnnotationVisitor visitAnnotation(String descriptor, boolean visible) {
        List<String> s = DescUtil.extractClassNames(descriptor);
        for (String c : s) {
            String co = ObfEnv.classNameObfMapping.getOrDefault(c, c);
            descriptor = descriptor.replace(c, co);
        }
        return super.visitAnnotation(descriptor, visible);
    }

    @Override
    public AnnotationVisitor visitTypeAnnotation(int typeRef, TypePath typePath, String descriptor, boolean visible) {
        List<String> s = DescUtil.extractClassNames(descriptor);
        for (String c : s) {
            String co = ObfEnv.classNameObfMapping.getOrDefault(c, c);
            descriptor = descriptor.replace(c, co);
        }
        return super.visitTypeAnnotation(typeRef, typePath, descriptor, visible);
    }

    @Override
    public FieldVisitor visitField(int access, String name, String descriptor, String signature, Object value) {
        List<String> s = DescUtil.extractClassNames(descriptor);
        for (String c : s) {
            String co = ObfEnv.classNameObfMapping.getOrDefault(c, c);
            descriptor = descriptor.replace(c, co);
        }
        if (signature != null) {
            List<String> sig = DescUtil.extractClassNames(signature);
            for (String c : sig) {
                String co = ObfEnv.classNameObfMapping.getOrDefault(c, c);
                signature = signature.replace(c, co);
            }
        }
        return super.visitField(access, name, descriptor, signature, value);
    }

    @Override
    public ModuleVisitor visitModule(String name, int access, String version) {
        return super.visitModule(name, access, version);
    }

    @Override
    public RecordComponentVisitor visitRecordComponent(String name, String descriptor, String signature) {
        List<String> s = DescUtil.extractClassNames(descriptor);
        for (String c : s) {
            String co = ObfEnv.classNameObfMapping.getOrDefault(c, c);
            descriptor = descriptor.replace(c, co);
        }
        List<String> sig = DescUtil.extractClassNames(signature);
        for (String c : sig) {
            String co = ObfEnv.classNameObfMapping.getOrDefault(c, c);
            signature = signature.replace(c, co);
        }
        return super.visitRecordComponent(name, descriptor, signature);
    }

    @Override
    public void visitAttribute(Attribute attribute) {
        super.visitAttribute(attribute);
    }

    @Override
    public void visitEnd() {
        super.visitEnd();
    }

    @Override
    public void visitInnerClass(String name, String outerName, String innerName, int access) {
        name = ObfEnv.classNameObfMapping.getOrDefault(name, name);
        outerName = ObfEnv.classNameObfMapping.getOrDefault(outerName, outerName);
        innerName = name.split("\\$")[1];
        super.visitInnerClass(name, outerName, innerName, access);
    }

    @Override
    public void visitNestHost(String nestHost) {
        super.visitNestHost(nestHost);
    }

    @Override
    public void visitNestMember(String nestMember) {
        super.visitNestMember(nestMember);
    }

    @Override
    public void visitOuterClass(String owner, String name, String descriptor) {
        owner = ObfEnv.classNameObfMapping.getOrDefault(owner, owner);
        super.visitOuterClass(owner, name, descriptor);
    }

    @Override
    public void visitPermittedSubclass(String permittedSubclass) {
        permittedSubclass = ObfEnv.classNameObfMapping.getOrDefault(permittedSubclass, permittedSubclass);
        super.visitPermittedSubclass(permittedSubclass);
    }

    @Override
    public void visitSource(String source, String debug) {
        super.visitSource(source, debug);
    }

    @Override
    public ClassVisitor getDelegate() {
        return super.getDelegate();
    }

    static class ClassNameChangerMethodAdapter extends MethodVisitor {
        ClassNameChangerMethodAdapter(MethodVisitor mv) {
            super(Const.ASMVersion, mv);
        }

        @Override
        public void visitMethodInsn(int opcode, String owner, String name, String descriptor, boolean isInterface) {
            owner = ObfEnv.classNameObfMapping.getOrDefault(owner, owner);
            List<String> s = DescUtil.extractClassNames(descriptor);
            for (String c : s) {
                String co = ObfEnv.classNameObfMapping.getOrDefault(c, c);
                descriptor = descriptor.replace(c, co);
            }
            super.visitMethodInsn(opcode, owner, name, descriptor, isInterface);
        }

        @Override
        public void visitFieldInsn(int opcode, String owner, String name, String descriptor) {
            owner = ObfEnv.classNameObfMapping.getOrDefault(owner, owner);
            List<String> s = DescUtil.extractClassNames(descriptor);
            for (String c : s) {
                String co = ObfEnv.classNameObfMapping.getOrDefault(c, c);
                descriptor = descriptor.replace(c, co);
            }
            super.visitFieldInsn(opcode, owner, name, descriptor);
        }

        @Override
        public void visitTypeInsn(int opcode, String type) {
            type = ObfEnv.classNameObfMapping.getOrDefault(type, type);
            super.visitTypeInsn(opcode, type);
        }

        @Override
        public void visitAttribute(Attribute attribute) {
            super.visitAttribute(attribute);
        }

        @Override
        public AnnotationVisitor visitTypeAnnotation(int typeRef, TypePath typePath, String descriptor, boolean visible) {
            List<String> s = DescUtil.extractClassNames(descriptor);
            for (String c : s) {
                String co = ObfEnv.classNameObfMapping.getOrDefault(c, c);
                descriptor = descriptor.replace(c, co);
            }
            return super.visitTypeAnnotation(typeRef, typePath, descriptor, visible);
        }

        @Override
        public MethodVisitor getDelegate() {
            return super.getDelegate();
        }

        @Override
        public void visitEnd() {
            super.visitEnd();
        }

        @Override
        public AnnotationVisitor visitAnnotation(String descriptor, boolean visible) {
            List<String> s = DescUtil.extractClassNames(descriptor);
            for (String c : s) {
                String co = ObfEnv.classNameObfMapping.getOrDefault(c, c);
                descriptor = descriptor.replace(c, co);
            }
            return super.visitAnnotation(descriptor, visible);
        }

        @Override
        public AnnotationVisitor visitAnnotationDefault() {
            return super.visitAnnotationDefault();
        }

        @Override
        public AnnotationVisitor visitInsnAnnotation(int typeRef, TypePath typePath, String descriptor, boolean visible) {
            List<String> s = DescUtil.extractClassNames(descriptor);
            for (String c : s) {
                String co = ObfEnv.classNameObfMapping.getOrDefault(c, c);
                descriptor = descriptor.replace(c, co);
            }
            return super.visitInsnAnnotation(typeRef, typePath, descriptor, visible);
        }

        @Override
        public AnnotationVisitor visitLocalVariableAnnotation(int typeRef, TypePath typePath, Label[] start, Label[] end, int[] index, String descriptor, boolean visible) {
            List<String> s = DescUtil.extractClassNames(descriptor);
            for (String c : s) {
                String co = ObfEnv.classNameObfMapping.getOrDefault(c, c);
                descriptor = descriptor.replace(c, co);
            }
            return super.visitLocalVariableAnnotation(typeRef, typePath, start, end, index, descriptor, visible);
        }

        @Override
        public AnnotationVisitor visitParameterAnnotation(int parameter, String descriptor, boolean visible) {
            List<String> s = DescUtil.extractClassNames(descriptor);
            for (String c : s) {
                String co = ObfEnv.classNameObfMapping.getOrDefault(c, c);
                descriptor = descriptor.replace(c, co);
            }
            return super.visitParameterAnnotation(parameter, descriptor, visible);
        }

        @Override
        public AnnotationVisitor visitTryCatchAnnotation(int typeRef, TypePath typePath, String descriptor, boolean visible) {
            List<String> s = DescUtil.extractClassNames(descriptor);
            for (String c : s) {
                String co = ObfEnv.classNameObfMapping.getOrDefault(c, c);
                descriptor = descriptor.replace(c, co);
            }
            return super.visitTryCatchAnnotation(typeRef, typePath, descriptor, visible);
        }

        @Override
        public void visitAnnotableParameterCount(int parameterCount, boolean visible) {
            super.visitAnnotableParameterCount(parameterCount, visible);
        }

        @Override
        public void visitCode() {
            super.visitCode();
        }

        @Override
        public void visitFrame(int type, int numLocal, Object[] local, int numStack, Object[] stack) {
            for (int i = 0; i < local.length; i++) {
                Object obj = local[i];
                if (obj instanceof String) {
                    String s = (String) obj;
                    s = ObfEnv.classNameObfMapping.getOrDefault(s, s);
                    local[i] = s;
                }
            }
            for (int i = 0; i < stack.length; i++) {
                Object obj = stack[i];
                if (obj instanceof String) {
                    String s = (String) obj;
                    s = ObfEnv.classNameObfMapping.getOrDefault(s, s);
                    stack[i] = s;
                }
            }
            super.visitFrame(type, numLocal, local, numStack, stack);
        }

        @Override
        public void visitIincInsn(int varIndex, int increment) {
            super.visitIincInsn(varIndex, increment);
        }

        @Override
        public void visitInsn(int opcode) {
            super.visitInsn(opcode);
        }

        @Override
        public void visitIntInsn(int opcode, int operand) {
            super.visitIntInsn(opcode, operand);
        }

        @Override
        public void visitInvokeDynamicInsn(String name, String descriptor, Handle bootstrapMethodHandle, Object... bootstrapMethodArguments) {
            List<String> s = DescUtil.extractClassNames(descriptor);
            for (String c : s) {
                String co = ObfEnv.classNameObfMapping.getOrDefault(c, c);
                descriptor = descriptor.replace(c, co);
            }

            String owner = bootstrapMethodHandle.getOwner();
            owner = ObfEnv.classNameObfMapping.getOrDefault(owner, owner);

            String handleDesc = bootstrapMethodHandle.getDesc();
            List<String> handleS = DescUtil.extractClassNames(handleDesc);
            for (String c : handleS) {
                String co = ObfEnv.classNameObfMapping.getOrDefault(c, c);
                handleDesc = handleDesc.replace(c, co);
            }

            Handle handle = new Handle(
                    bootstrapMethodHandle.getTag(),
                    owner,
                    bootstrapMethodHandle.getName(),
                    handleDesc,
                    bootstrapMethodHandle.isInterface());

            for (int i = 0; i < bootstrapMethodArguments.length; i++) {
                Object obj = bootstrapMethodArguments[i];
                if (obj instanceof Handle) {
                    Handle ho = (Handle) obj;
                    String tempOwner = ho.getOwner();
                    tempOwner = ObfEnv.classNameObfMapping.getOrDefault(tempOwner, tempOwner);
                    String tempDesc = ho.getDesc();
                    List<String> tempS = DescUtil.extractClassNames(tempDesc);
                    for (String c : tempS) {
                        String co = ObfEnv.classNameObfMapping.getOrDefault(c, c);
                        tempDesc = tempDesc.replace(c, co);
                    }
                    Handle tempHandle = new Handle(
                            ho.getTag(),
                            tempOwner,
                            ho.getName(),
                            tempDesc,
                            ho.isInterface());
                    bootstrapMethodArguments[i] = tempHandle;
                }
            }

            super.visitInvokeDynamicInsn(name, descriptor, handle, bootstrapMethodArguments);
        }

        @Override
        public void visitJumpInsn(int opcode, Label label) {
            super.visitJumpInsn(opcode, label);
        }

        @Override
        public void visitLabel(Label label) {
            super.visitLabel(label);
        }

        @Override
        public void visitLdcInsn(Object value) {
            if (value instanceof Type) {
                Type valueType = (Type) value;
                String desc = valueType.getDescriptor();
                List<String> s = DescUtil.extractClassNames(desc);
                for (String c : s) {
                    String co = ObfEnv.classNameObfMapping.getOrDefault(c, c);
                    desc = desc.replace(c, co);
                }
                Type newValue = Type.getType(desc);
                super.visitLdcInsn(newValue);
                return;
            }
            super.visitLdcInsn(value);
        }

        @Override
        public void visitLineNumber(int line, Label start) {
            super.visitLineNumber(line, start);
        }

        @Override
        public void visitLocalVariable(String name, String descriptor, String signature, Label start, Label end, int index) {
            List<String> s = DescUtil.extractClassNames(descriptor);
            for (String c : s) {
                String co = ObfEnv.classNameObfMapping.getOrDefault(c, c);
                descriptor = descriptor.replace(c, co);
            }
            if (signature != null) {
                List<String> sig = DescUtil.extractClassNames(signature);
                for (String c : sig) {
                    String co = ObfEnv.classNameObfMapping.getOrDefault(c, c);
                    signature = signature.replace(c, co);
                }
            }
            super.visitLocalVariable(name, descriptor, signature, start, end, index);
        }

        @Override
        public void visitLookupSwitchInsn(Label dflt, int[] keys, Label[] labels) {
            super.visitLookupSwitchInsn(dflt, keys, labels);
        }

        @Override
        public void visitMaxs(int maxStack, int maxLocals) {
            super.visitMaxs(maxStack, maxLocals);
        }

        @Override
        public void visitMultiANewArrayInsn(String descriptor, int numDimensions) {
            List<String> s = DescUtil.extractClassNames(descriptor);
            for (String c : s) {
                String co = ObfEnv.classNameObfMapping.getOrDefault(c, c);
                descriptor = descriptor.replace(c, co);
            }
            super.visitMultiANewArrayInsn(descriptor, numDimensions);
        }

        @Override
        public void visitParameter(String name, int access) {
            super.visitParameter(name, access);
        }

        @Override
        public void visitTableSwitchInsn(int min, int max, Label dflt, Label... labels) {
            super.visitTableSwitchInsn(min, max, dflt, labels);
        }

        @Override
        public void visitTryCatchBlock(Label start, Label end, Label handler, String type) {
            super.visitTryCatchBlock(start, end, handler, type);
        }

        @Override
        public void visitVarInsn(int opcode, int varIndex) {
            super.visitVarInsn(opcode, varIndex);
        }
    }
}