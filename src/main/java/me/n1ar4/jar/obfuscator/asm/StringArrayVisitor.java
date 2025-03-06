package me.n1ar4.jar.obfuscator.asm;

import me.n1ar4.jar.obfuscator.Const;
import me.n1ar4.jar.obfuscator.core.ObfEnv;
import me.n1ar4.jar.obfuscator.transform.StringArrayTransformer;
import org.objectweb.asm.*;
import org.objectweb.asm.commons.GeneratorAdapter;
import org.objectweb.asm.commons.Method;

import java.util.ArrayList;

@SuppressWarnings("all")
public class StringArrayVisitor extends ClassVisitor {
    private String className;
    private boolean isClinitPresent = false;
    private boolean isInterface;

    public StringArrayVisitor(ClassVisitor classVisitor) {
        super(Const.ASMVersion, classVisitor);
    }

    @Override
    public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
        super.visit(version, access, name, signature, superName, interfaces);
        this.className = name;
        isInterface = (access & Opcodes.ACC_INTERFACE) != 0;
        super.visit(version, access, name, signature, superName, interfaces);
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
        MethodVisitor mv = super.visitMethod(access, name, desc, signature, exceptions);
        if (isInterface) {
            return mv;
        }
        if ("<clinit>".equals(name)) {
            isClinitPresent = true;
            String fieldDesc = "Ljava/util/ArrayList;";
            GeneratorAdapter ga = new GeneratorAdapter(mv, access, name, desc);
            ga.visitCode();
            ga.newInstance(Type.getType("Ljava/util/ArrayList;"));
            ga.dup();
            ga.invokeConstructor(Type.getType("Ljava/util/ArrayList;"), new Method("<init>", "()V"));
            ga.putStatic(Type.getObjectType(className), ObfEnv.ADVANCE_STRING_NAME, Type.getType("Ljava/util/ArrayList;"));
            ArrayList<String> list = ObfEnv.newStringInClass.get(className);
            if (list != null && !list.isEmpty()) {
                for (String s : list) {
                    ga.getStatic(Type.getObjectType(className), ObfEnv.ADVANCE_STRING_NAME, Type.getType(fieldDesc));
                    ga.push(s);
                    ga.invokeVirtual(Type.getType("Ljava/util/ArrayList;"),
                            new Method("add", "(Ljava/lang/Object;)Z"));
                    ga.pop();
                }
            }
            // DO NOT RETURN
            ga.endMethod();
            return new StringArrayMethodAdapter(ga, className);
        }
        return new StringArrayMethodAdapter(mv, className);
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
        return super.visitField(access, name, descriptor, signature, value);
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
        if (isInterface) {
            super.visitEnd();
            return;
        }
        String fieldDesc = "Ljava/util/ArrayList;";
        FieldVisitor fv = super.visitField(Opcodes.ACC_PRIVATE | Opcodes.ACC_STATIC,
                ObfEnv.ADVANCE_STRING_NAME, fieldDesc, "Ljava/util/ArrayList<Ljava/lang/String;>;", null);
        if (fv != null) {
            fv.visitEnd();
        }
        if (!isClinitPresent) {
            MethodVisitor mv = super.visitMethod(Opcodes.ACC_STATIC, "<clinit>", "()V", null, null);
            GeneratorAdapter ga = new GeneratorAdapter(mv, Opcodes.ACC_STATIC, "<clinit>", "()V");
            ga.visitCode();

            ga.newInstance(Type.getType("Ljava/util/ArrayList;"));
            ga.dup();
            ga.invokeConstructor(Type.getType("Ljava/util/ArrayList;"), new Method("<init>", "()V"));
            ga.putStatic(Type.getObjectType(className), ObfEnv.ADVANCE_STRING_NAME, Type.getType("Ljava/util/ArrayList;"));

            ArrayList<String> list = ObfEnv.newStringInClass.get(className);
            if (list != null && !list.isEmpty()) {
                for (String s : list) {
                    ga.getStatic(Type.getObjectType(className), ObfEnv.ADVANCE_STRING_NAME, Type.getType(fieldDesc));
                    ga.push(s);
                    ga.invokeVirtual(Type.getType("Ljava/util/ArrayList;"),
                            new Method("add", "(Ljava/lang/Object;)Z"));
                    ga.pop();
                }
            }
            ga.returnValue();
            ga.endMethod();
        }
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

    static class StringArrayMethodAdapter extends MethodVisitor {
        private final String className;

        StringArrayMethodAdapter(MethodVisitor mv, String name) {
            super(Const.ASMVersion, mv);
            this.className = name;
        }

        @Override
        public void visitMethodInsn(int opcode, String owner, String name, String descriptor, boolean isInterface) {
            super.visitMethodInsn(opcode, owner, name, descriptor, isInterface);
        }

        @Override
        public void visitFieldInsn(int opcode, String owner, String name, String descriptor) {
            super.visitFieldInsn(opcode, owner, name, descriptor);
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
            if (value instanceof String) {
                visitFieldInsn(Opcodes.GETSTATIC, className, ObfEnv.ADVANCE_STRING_NAME,
                        "Ljava/util/ArrayList;");
                visitIntInsn(Opcodes.SIPUSH, StringArrayTransformer.INDEX);
                StringArrayTransformer.INDEX++;
                visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/util/ArrayList", "get",
                        "(I)Ljava/lang/Object;", false);
                visitTypeInsn(Opcodes.CHECKCAST, "java/lang/String");
            } else {
                super.visitLdcInsn(value);
            }
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