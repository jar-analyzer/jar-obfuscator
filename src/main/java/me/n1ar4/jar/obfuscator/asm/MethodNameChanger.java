package me.n1ar4.jar.obfuscator.asm;

import me.n1ar4.jar.obfuscator.Const;
import me.n1ar4.jar.obfuscator.base.ClassReference;
import me.n1ar4.jar.obfuscator.base.MethodReference;
import me.n1ar4.jar.obfuscator.core.AnalyzeEnv;
import me.n1ar4.jar.obfuscator.core.ObfEnv;
import org.objectweb.asm.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@SuppressWarnings("all")
public class MethodNameChanger extends ClassVisitor {
    private String owner;
    private final List<MethodReference> ignoreMethods = new ArrayList<>();
    private final List<String> ignoreMethodString = new ArrayList<>();

    public MethodNameChanger(ClassVisitor classVisitor) {
        super(Const.ASMVersion, classVisitor);
    }

    @Override
    public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
        this.owner = name;
        // 查接口 不改接口方法
        for (String in : interfaces) {
            List<MethodReference> mList = AnalyzeEnv.methodsInClassMap.get(new ClassReference.Handle(in));
            if (mList == null) {
                continue;
            }
            ignoreMethods.addAll(mList);
        }

        // 检查内置黑名单
        String key = null;
        for (Map.Entry<String, String> entry : ObfEnv.classNameObfMapping.entrySet()) {
            if (entry.getValue().equals(name)) {
                key = entry.getKey();
                break;
            }
        }
        if (key != null) {
            List<String> methodNames = ObfEnv.ignoredClassMethodsMapping.get(key);
            if (methodNames != null) {
                ignoreMethodString.addAll(methodNames);
            }
        }
        super.visit(version, access, name, signature, superName, interfaces);
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
        MethodVisitor mv;

        for (MethodReference mr : ignoreMethods) {
            if (mr.getName().equals(name) && mr.getDesc().equals(desc)) {
                return super.visitMethod(access, name, desc, signature, exceptions);
            }
        }

        for (String method : this.ignoreMethodString) {
            if (method.equals(name)) {
                return super.visitMethod(access, name, desc, signature, exceptions);
            }
        }

        if ("main".equals(name) && desc.equals("([Ljava/lang/String;)V") && access == 9) {
            mv = super.visitMethod(access, name, desc, signature, exceptions);
        } else if (name.equals("<init>") || name.equals("<clinit>")) {
            mv = super.visitMethod(access, name, desc, signature, exceptions);
        } else {
            MethodReference.Handle m = ObfEnv.methodNameObfMapping.get(new MethodReference.Handle(
                    new ClassReference.Handle(owner),
                    name,
                    desc
            ));
            if (ObfEnv.config.isEnableHideMethod()) {
                access = access | Opcodes.ACC_SYNTHETIC;
            }
            if (m == null) {
                mv = super.visitMethod(access, name, desc, signature, exceptions);
                return new MethodNameChangerMethodAdapter(mv);
            } else {
                mv = super.visitMethod(access, m.getName(), m.getDesc(), signature, exceptions);
                return new MethodNameChangerMethodAdapter(mv);
            }
        }
        return new MethodNameChangerMethodAdapter(mv);
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

    static class MethodNameChangerMethodAdapter extends MethodVisitor {
        MethodNameChangerMethodAdapter(MethodVisitor mv) {
            super(Const.ASMVersion, mv);
        }

        @Override
        public void visitMethodInsn(int opcode, String owner, String name, String descriptor, boolean isInterface) {
            MethodReference.Handle m = ObfEnv.methodNameObfMapping.get(new MethodReference.Handle(
                    new ClassReference.Handle(owner),
                    name,
                    descriptor
            ));
            if (m != null) {
                super.visitMethodInsn(opcode, m.getClassReference().getName(), m.getName(), m.getDesc(), isInterface);
                return;
            }
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
            MethodReference.Handle m = ObfEnv.methodNameObfMapping.get(new MethodReference.Handle(
                    new ClassReference.Handle(bootstrapMethodHandle.getOwner()),
                    bootstrapMethodHandle.getName(),
                    bootstrapMethodHandle.getDesc()
            ));
            Handle handle;
            if (m != null) {
                handle = new Handle(
                        bootstrapMethodHandle.getTag(),
                        m.getClassReference().getName(),
                        m.getName(),
                        m.getDesc(),
                        bootstrapMethodHandle.isInterface());
            } else {
                handle = bootstrapMethodHandle;
            }

            for (int i = 0; i < bootstrapMethodArguments.length; i++) {
                Object obj = bootstrapMethodArguments[i];
                if (obj instanceof Handle) {
                    Handle ho = (Handle) obj;
                    MethodReference.Handle mo = ObfEnv.methodNameObfMapping.get(new MethodReference.Handle(
                            new ClassReference.Handle(ho.getOwner()),
                            ho.getName(),
                            ho.getDesc()
                    ));
                    Handle tempHandle;
                    if (mo != null) {
                        tempHandle = new Handle(
                                ho.getTag(),
                                mo.getClassReference().getName(),
                                mo.getName(),
                                mo.getDesc(),
                                ho.isInterface());
                    } else {
                        tempHandle = ho;
                    }
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