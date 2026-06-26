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
import me.n1ar4.jar.obfuscator.core.ObfEnv;
import me.n1ar4.jar.obfuscator.utils.DescUtil;
import org.objectweb.asm.*;

import java.util.List;

@SuppressWarnings("all")
public class ClassNameVisitor extends ClassVisitor {
    public ClassNameVisitor(ClassVisitor classVisitor) {
        super(Const.ASMVersion, classVisitor);
    }

    private static String remapClassName(String name) {
        if (name == null) {
            return null;
        }
        return ObfEnv.classNameObfMapping.getOrDefault(name, name);
    }

    private static String remapDesc(String descriptor) {
        if (descriptor == null) {
            return null;
        }
        List<String> classes = DescUtil.extractClassNames(descriptor);
        for (String c : classes) {
            descriptor = descriptor.replace(c, remapClassName(c));
        }
        return descriptor;
    }

    private static String remapInnerName(String name, String innerName) {
        if (name == null || innerName == null) {
            return innerName;
        }
        int index = name.lastIndexOf('$');
        if (index < 0 || index == name.length() - 1) {
            return innerName;
        }
        return name.substring(index + 1);
    }

    private static Type remapType(Type type) {
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

    private static Handle remapHandle(Handle handle) {
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

    @Override
    public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
        name = remapClassName(name);
        superName = remapClassName(superName);
        for (int i = 0; i < interfaces.length; i++) {
            interfaces[i] = remapClassName(interfaces[i]);
        }
        signature = remapDesc(signature);
        super.visit(version, access, name, signature, superName, interfaces);
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
        desc = remapDesc(desc);
        signature = remapDesc(signature);
        if (exceptions != null) {
            for (int i = 0; i < exceptions.length; i++) {
                exceptions[i] = remapClassName(exceptions[i]);
            }
        }
        MethodVisitor mv = super.visitMethod(access, name, desc, signature, exceptions);
        return new ClassNameChangerMethodAdapter(mv);
    }

    @Override
    public AnnotationVisitor visitAnnotation(String descriptor, boolean visible) {
        String newDescriptor = remapDesc(descriptor);
        return new AnnotationRemapVisitor(super.visitAnnotation(newDescriptor, visible), newDescriptor, false);
    }

    @Override
    public AnnotationVisitor visitTypeAnnotation(int typeRef, TypePath typePath, String descriptor, boolean visible) {
        String newDescriptor = remapDesc(descriptor);
        return new AnnotationRemapVisitor(super.visitTypeAnnotation(typeRef, typePath, newDescriptor, visible),
                newDescriptor, false);
    }

    @Override
    public FieldVisitor visitField(int access, String name, String descriptor, String signature, Object value) {
        FieldVisitor fv = super.visitField(access, name, remapDesc(descriptor), remapDesc(signature), value);
        return new ClassNameFieldAdapter(fv);
    }

    @Override
    public ModuleVisitor visitModule(String name, int access, String version) {
        return super.visitModule(name, access, version);
    }

    @Override
    public RecordComponentVisitor visitRecordComponent(String name, String descriptor, String signature) {
        RecordComponentVisitor rv = super.visitRecordComponent(name, remapDesc(descriptor), remapDesc(signature));
        return new ClassNameRecordComponentAdapter(rv);
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
        name = remapClassName(name);
        outerName = remapClassName(outerName);
        innerName = remapInnerName(name, innerName);
        super.visitInnerClass(name, outerName, innerName, access);
    }

    @Override
    public void visitNestHost(String nestHost) {
        super.visitNestHost(remapClassName(nestHost));
    }

    @Override
    public void visitNestMember(String nestMember) {
        super.visitNestMember(remapClassName(nestMember));
    }

    @Override
    public void visitOuterClass(String owner, String name, String descriptor) {
        super.visitOuterClass(remapClassName(owner), name, remapDesc(descriptor));
    }

    @Override
    public void visitPermittedSubclass(String permittedSubclass) {
        super.visitPermittedSubclass(remapClassName(permittedSubclass));
    }

    @Override
    public void visitSource(String source, String debug) {
        super.visitSource(source, debug);
    }

    @Override
    public ClassVisitor getDelegate() {
        return super.getDelegate();
    }

    static class ClassNameFieldAdapter extends FieldVisitor {
        ClassNameFieldAdapter(FieldVisitor fieldVisitor) {
            super(Const.ASMVersion, fieldVisitor);
        }

        @Override
        public AnnotationVisitor visitAnnotation(String descriptor, boolean visible) {
            String newDescriptor = remapDesc(descriptor);
            return new AnnotationRemapVisitor(super.visitAnnotation(newDescriptor, visible),
                    newDescriptor, false);
        }

        @Override
        public AnnotationVisitor visitTypeAnnotation(int typeRef, TypePath typePath, String descriptor, boolean visible) {
            String newDescriptor = remapDesc(descriptor);
            return new AnnotationRemapVisitor(super.visitTypeAnnotation(typeRef, typePath, newDescriptor, visible),
                    newDescriptor, false);
        }
    }

    static class ClassNameRecordComponentAdapter extends RecordComponentVisitor {
        ClassNameRecordComponentAdapter(RecordComponentVisitor recordComponentVisitor) {
            super(Const.ASMVersion, recordComponentVisitor);
        }

        @Override
        public AnnotationVisitor visitAnnotation(String descriptor, boolean visible) {
            String newDescriptor = remapDesc(descriptor);
            return new AnnotationRemapVisitor(super.visitAnnotation(newDescriptor, visible),
                    newDescriptor, false);
        }

        @Override
        public AnnotationVisitor visitTypeAnnotation(int typeRef, TypePath typePath, String descriptor, boolean visible) {
            String newDescriptor = remapDesc(descriptor);
            return new AnnotationRemapVisitor(super.visitTypeAnnotation(typeRef, typePath, newDescriptor, visible),
                    newDescriptor, false);
        }
    }

    static class ClassNameChangerMethodAdapter extends MethodVisitor {
        ClassNameChangerMethodAdapter(MethodVisitor mv) {
            super(Const.ASMVersion, mv);
        }

        @Override
        public void visitMethodInsn(int opcode, String owner, String name, String descriptor, boolean isInterface) {
            super.visitMethodInsn(opcode, remapClassName(owner), name, remapDesc(descriptor), isInterface);
        }

        @Override
        public void visitFieldInsn(int opcode, String owner, String name, String descriptor) {
            super.visitFieldInsn(opcode, remapClassName(owner), name, remapDesc(descriptor));
        }

        @Override
        public void visitTypeInsn(int opcode, String type) {
            super.visitTypeInsn(opcode, remapClassName(type));
        }

        @Override
        public void visitAttribute(Attribute attribute) {
            super.visitAttribute(attribute);
        }

        @Override
        public AnnotationVisitor visitTypeAnnotation(int typeRef, TypePath typePath, String descriptor, boolean visible) {
            String newDescriptor = remapDesc(descriptor);
            return new AnnotationRemapVisitor(super.visitTypeAnnotation(typeRef, typePath, newDescriptor, visible),
                    newDescriptor, false);
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
            String newDescriptor = remapDesc(descriptor);
            return new AnnotationRemapVisitor(super.visitAnnotation(newDescriptor, visible),
                    newDescriptor, false);
        }

        @Override
        public AnnotationVisitor visitAnnotationDefault() {
            return new AnnotationRemapVisitor(super.visitAnnotationDefault(), null, false);
        }

        @Override
        public AnnotationVisitor visitInsnAnnotation(int typeRef, TypePath typePath, String descriptor, boolean visible) {
            String newDescriptor = remapDesc(descriptor);
            return new AnnotationRemapVisitor(super.visitInsnAnnotation(typeRef, typePath, newDescriptor, visible),
                    newDescriptor, false);
        }

        @Override
        public AnnotationVisitor visitLocalVariableAnnotation(int typeRef, TypePath typePath, Label[] start, Label[] end, int[] index, String descriptor, boolean visible) {
            String newDescriptor = remapDesc(descriptor);
            return new AnnotationRemapVisitor(super.visitLocalVariableAnnotation(typeRef, typePath, start, end, index,
                    newDescriptor, visible), newDescriptor, false);
        }

        @Override
        public AnnotationVisitor visitParameterAnnotation(int parameter, String descriptor, boolean visible) {
            String newDescriptor = remapDesc(descriptor);
            return new AnnotationRemapVisitor(super.visitParameterAnnotation(parameter, newDescriptor, visible),
                    newDescriptor, false);
        }

        @Override
        public AnnotationVisitor visitTryCatchAnnotation(int typeRef, TypePath typePath, String descriptor, boolean visible) {
            String newDescriptor = remapDesc(descriptor);
            return new AnnotationRemapVisitor(super.visitTryCatchAnnotation(typeRef, typePath, newDescriptor, visible),
                    newDescriptor, false);
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
                    local[i] = remapClassName(s);
                }
            }
            for (int i = 0; i < stack.length; i++) {
                Object obj = stack[i];
                if (obj instanceof String) {
                    String s = (String) obj;
                    stack[i] = remapClassName(s);
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
            for (int i = 0; i < bootstrapMethodArguments.length; i++) {
                Object obj = bootstrapMethodArguments[i];
                if (obj instanceof Handle) {
                    bootstrapMethodArguments[i] = remapHandle((Handle) obj);
                } else if (obj instanceof Type) {
                    bootstrapMethodArguments[i] = remapType((Type) obj);
                }
            }

            super.visitInvokeDynamicInsn(name, remapDesc(descriptor), remapHandle(bootstrapMethodHandle), bootstrapMethodArguments);
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
                super.visitLdcInsn(remapType(valueType));
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
            super.visitLocalVariable(name, remapDesc(descriptor), remapDesc(signature), start, end, index);
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
            super.visitMultiANewArrayInsn(remapDesc(descriptor), numDimensions);
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
            super.visitTryCatchBlock(start, end, handler, remapClassName(type));
        }

        @Override
        public void visitVarInsn(int opcode, int varIndex) {
            super.visitVarInsn(opcode, varIndex);
        }
    }
}
