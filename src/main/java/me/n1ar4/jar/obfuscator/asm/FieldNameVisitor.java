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
import me.n1ar4.jar.obfuscator.base.ClassField;
import me.n1ar4.jar.obfuscator.base.ClassReference;
import me.n1ar4.jar.obfuscator.core.AnalyzeEnv;
import me.n1ar4.jar.obfuscator.core.ObfEnv;
import org.objectweb.asm.*;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class FieldNameVisitor extends ClassVisitor {
    private String className;

    public FieldNameVisitor(ClassVisitor classVisitor) {
        super(Const.ASMVersion, classVisitor);
    }

    /**
     * 字段引用 {@code getfield/putfield owner.name} 中的 {@code owner} 可能是继承该字段的子类
     * 而非真正声明该字段的类。字段名混淆映射表以「声明类 + 字段名」为 key，因此当直接查表未命中时，
     * 需要沿继承链向上找到真正声明该字段的类，再用声明类查表。
     *
     * @param owner 字段引用中的 owner（混淆后的新类名）
     * @param name  字段名（尚未重命名的原始名）
     * @return 声明该字段的类的新名，若无法解析则返回原 owner
     */
    private static String resolveDeclaringClass(String owner, String name) {
        if (owner == null || name == null) {
            return owner;
        }
        Set<String> visited = new HashSet<>();
        String current = reverseMapClass(owner);
        while (current != null && visited.add(current)) {
            List<String> fields = AnalyzeEnv.fieldsInClassMap.get(current);
            if (fields != null && fields.contains(name)) {
                return ObfEnv.classNameObfMapping.getOrDefault(current, current);
            }
            ClassReference clazz = AnalyzeEnv.classMap.get(new ClassReference.Handle(current));
            if (clazz == null) {
                return owner;
            }
            current = clazz.getSuperClass();
        }
        return owner;
    }

    /**
     * 由混淆后的新类名反查原始类名。{@code classNameObfMapping} 是 原始名 -> 新名 的映射，
     * 这里需要反向查找。若 owner 本身就是原始名（或不在映射中）则直接返回 owner。
     */
    private static String reverseMapClass(String owner) {
        if (owner == null) {
            return null;
        }
        for (Map.Entry<String, String> entry : ObfEnv.classNameObfMapping.entrySet()) {
            if (owner.equals(entry.getValue())) {
                return entry.getKey();
            }
        }
        return owner;
    }

    @Override
    public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
        this.className = name;
        super.visit(version, access, name, signature, superName, interfaces);
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
        MethodVisitor mv = super.visitMethod(access, name, desc, signature, exceptions);
        return new FieldNameChangerMethodAdapter(mv);
    }

    @Override
    public AnnotationVisitor visitAnnotation(String descriptor, boolean visible) {
        return super.visitAnnotation(descriptor, visible);
    }

    @Override
    public AnnotationVisitor visitTypeAnnotation(int typeRef, TypePath typePath, String descriptor, boolean visible) {
        return super.visitTypeAnnotation(typeRef, typePath, descriptor, visible);
    }

    @Override
    public FieldVisitor visitField(int access, String name, String descriptor, String signature, Object value) {
        ClassField cf = new ClassField();
        cf.setClassName(this.className);
        cf.setFieldName(name);
        ClassField newCF = ObfEnv.fieldNameObfMapping.getOrDefault(cf, cf);
        if (ObfEnv.config.isEnableHideField()) {
            access = access | Opcodes.ACC_SYNTHETIC;
        }
        return super.visitField(access, newCF.getFieldName(), descriptor, signature, value);
    }

    @Override
    public ModuleVisitor visitModule(String name, int access, String version) {
        return super.visitModule(name, access, version);
    }

    @Override
    public RecordComponentVisitor visitRecordComponent(String name, String descriptor, String signature) {
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
        super.visitOuterClass(owner, name, descriptor);
    }

    @Override
    public void visitPermittedSubclass(String permittedSubclass) {
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

    static class FieldNameChangerMethodAdapter extends MethodVisitor {
        FieldNameChangerMethodAdapter(MethodVisitor mv) {
            super(Const.ASMVersion, mv);
        }

        @Override
        public void visitMethodInsn(int opcode, String owner, String name, String descriptor, boolean isInterface) {
            super.visitMethodInsn(opcode, owner, name, descriptor, isInterface);
        }

        @Override
        public void visitFieldInsn(int opcode, String owner, String name, String descriptor) {
            ClassField cf = new ClassField();
            cf.setClassName(owner);
            cf.setFieldName(name);
            ClassField newCF = ObfEnv.fieldNameObfMapping.get(cf);
            if (newCF == null) {
                // 直接以 owner 查表未命中：owner 可能是继承该字段的子类，
                // 沿继承链找到真正声明该字段的类再查一次
                String declaringClass = resolveDeclaringClass(owner, name);
                if (!owner.equals(declaringClass)) {
                    cf.setClassName(declaringClass);
                    newCF = ObfEnv.fieldNameObfMapping.get(cf);
                }
            }
            String resolvedName = newCF == null ? name : newCF.getFieldName();
            super.visitFieldInsn(opcode, owner, resolvedName, descriptor);
        }

        @Override
        public void visitTypeInsn(int opcode, String type) {
            super.visitTypeInsn(opcode, type);
        }

        @Override
        public void visitAttribute(Attribute attribute) {
            super.visitAttribute(attribute);
        }

        @Override
        public AnnotationVisitor visitTypeAnnotation(int typeRef, TypePath typePath, String descriptor, boolean visible) {
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
            return super.visitAnnotation(descriptor, visible);
        }

        @Override
        public AnnotationVisitor visitAnnotationDefault() {
            return super.visitAnnotationDefault();
        }

        @Override
        public AnnotationVisitor visitInsnAnnotation(int typeRef, TypePath typePath, String descriptor, boolean visible) {
            return super.visitInsnAnnotation(typeRef, typePath, descriptor, visible);
        }

        @Override
        public AnnotationVisitor visitLocalVariableAnnotation(int typeRef, TypePath typePath, Label[] start, Label[] end, int[] index, String descriptor, boolean visible) {
            return super.visitLocalVariableAnnotation(typeRef, typePath, start, end, index, descriptor, visible);
        }

        @Override
        public AnnotationVisitor visitParameterAnnotation(int parameter, String descriptor, boolean visible) {
            return super.visitParameterAnnotation(parameter, descriptor, visible);
        }

        @Override
        public AnnotationVisitor visitTryCatchAnnotation(int typeRef, TypePath typePath, String descriptor, boolean visible) {
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
            super.visitInvokeDynamicInsn(name, descriptor, bootstrapMethodHandle, bootstrapMethodArguments);
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
            super.visitLdcInsn(value);
        }

        @Override
        public void visitLineNumber(int line, Label start) {
            super.visitLineNumber(line, start);
        }

        @Override
        public void visitLocalVariable(String name, String descriptor, String signature, Label start, Label end, int index) {
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