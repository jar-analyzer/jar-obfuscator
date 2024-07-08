package me.n1ar4.jar.obfuscator.asm;

import me.n1ar4.jar.obfuscator.Const;
import me.n1ar4.jar.obfuscator.config.BaseConfig;
import me.n1ar4.jar.obfuscator.utils.JunkUtil;
import me.n1ar4.jar.obfuscator.utils.RandomUtil;
import me.n1ar4.jrandom.core.JRandom;
import me.n1ar4.log.LogManager;
import me.n1ar4.log.Logger;
import org.objectweb.asm.*;


public class JunkCodeChanger extends ClassVisitor {
    private static final Logger logger = LogManager.getLogger();
    public static int MAX_JUNK_NUM = 1000;
    public static int JUNK_NUM = 0;
    private final BaseConfig config;
    private boolean shouldSkip;

    public JunkCodeChanger(ClassVisitor classVisitor, BaseConfig config) {
        super(Const.ASMVersion, classVisitor);
        JUNK_NUM = 0;
        this.config = config;
        this.shouldSkip = false;
    }

    @Override
    public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
        super.visit(version, access, name, signature, superName, interfaces);
        boolean isAbstract = (access & Opcodes.ACC_ABSTRACT) != 0;
        boolean isInterface = (access & Opcodes.ACC_INTERFACE) != 0;
        boolean isEnum = (access & Opcodes.ACC_ENUM) != 0;
        if (isAbstract || isInterface || isEnum) {
            shouldSkip = true;
        }
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
        MethodVisitor mv = super.visitMethod(access, name, desc, signature, exceptions);
        if (shouldSkip) {
            return mv;
        } else {
            return new JunkChangerMethodAdapter(mv, this.config);
        }
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
        // 添加无意义的代码
        if (!shouldSkip && config.getJunkLevel() > 2) {
            JunkUtil.addHttpCode(cv);
            JunkUtil.addPrintMethod(cv);
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

    static class JunkChangerMethodAdapter extends MethodVisitor {
        private final BaseConfig config;

        JunkChangerMethodAdapter(MethodVisitor mv, BaseConfig config) {
            super(Const.ASMVersion, mv);
            this.config = config;
        }

        @Override
        public void visitMethodInsn(int opcode, String owner, String name, String descriptor, boolean isInterface) {
            // LEVEL 1
            if (config.getJunkLevel() > 0) {
                JUNK_NUM++;
                if (JUNK_NUM > MAX_JUNK_NUM) {
                    logger.debug("max junk code");
                    super.visitMethodInsn(opcode, owner, name, descriptor, isInterface);
                    return;
                }

                mv.visitTypeInsn(Opcodes.NEW, "java/lang/String");
                mv.visitInsn(Opcodes.DUP);
                mv.visitLdcInsn(JRandom.getInstance().randomString(16));
                mv.visitMethodInsn(Opcodes.INVOKESPECIAL, "java/lang/String", "<init>",
                        "(Ljava/lang/String;)V", false);
                mv.visitInsn(Opcodes.POP);

                Label ifLabel = new Label();
                Label endLabel = new Label();

                mv.visitInsn(Opcodes.ICONST_1);
                mv.visitJumpInsn(Opcodes.IFNE, endLabel);

                mv.visitLabel(ifLabel);
                mv.visitFieldInsn(Opcodes.GETSTATIC, "java/lang/System",
                        "out", "Ljava/io/PrintStream;");
                mv.visitLdcInsn(JRandom.getInstance().randomString(16));
                mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/io/PrintStream",
                        "println", "(Ljava/lang/String;)V", false);
                mv.visitJumpInsn(Opcodes.GOTO, endLabel);
                mv.visitLabel(endLabel);
                mv.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
            }
            super.visitMethodInsn(opcode, owner, name, descriptor, isInterface);
        }

        @Override
        public void visitFieldInsn(int opcode, String owner, String name, String descriptor) {
            // LEVEL 2
            if (config.getJunkLevel() > 1) {
                JUNK_NUM++;
                if (JUNK_NUM > MAX_JUNK_NUM) {
                    logger.debug("max junk code");
                    super.visitFieldInsn(opcode, owner, name, descriptor);
                    return;
                }

                Label startLoop = new Label();
                Label endLoop = new Label();
                mv.visitLabel(startLoop);
                mv.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
                mv.visitInsn(Opcodes.NOP);
                mv.visitJumpInsn(Opcodes.GOTO, endLoop);
                mv.visitLabel(endLoop);
                mv.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
            }
            super.visitFieldInsn(opcode, owner, name, descriptor);
        }

        @Override
        public void visitTypeInsn(int opcode, String type) {
            // LEVEL 3
            if (config.getJunkLevel() > 2) {
                JUNK_NUM++;
                if (JUNK_NUM > MAX_JUNK_NUM) {
                    logger.debug("max junk code");
                    super.visitTypeInsn(opcode, type);
                    return;
                }
                mv.visitInsn(Opcodes.NOP);
                mv.visitInsn(Opcodes.NOP);
                mv.visitTypeInsn(Opcodes.NEW, "java/util/ArrayList");
                mv.visitInsn(Opcodes.DUP);
                mv.visitMethodInsn(Opcodes.INVOKESPECIAL, "java/util/ArrayList",
                        "<init>", "()V", false);
                mv.visitInsn(Opcodes.POP);
                mv.visitInsn(Opcodes.NOP);
            }
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
            // LEVEL 4
            if (config.getJunkLevel() > 3) {
                JUNK_NUM++;
                if (JUNK_NUM > MAX_JUNK_NUM) {
                    logger.debug("max junk code");
                    super.visitIincInsn(varIndex, increment);
                    return;
                }
                mv.visitInsn(RandomUtil.genICONSTOpcode());
                mv.visitInsn(Opcodes.POP);
                mv.visitInsn(RandomUtil.genICONSTOpcode());
                mv.visitInsn(Opcodes.NOP);
                mv.visitInsn(Opcodes.POP);
            }
            super.visitIincInsn(varIndex, increment);
        }

        @Override
        public void visitInsn(int opcode) {
            // LEVEL 5
            if (config.getJunkLevel() > 4) {
                JUNK_NUM++;
                if (JUNK_NUM > MAX_JUNK_NUM) {
                    logger.debug("max junk code");
                    super.visitInsn(opcode);
                    return;
                }
                mv.visitInsn(RandomUtil.genICONSTOpcode());
                mv.visitInsn(RandomUtil.genICONSTOpcode());
                mv.visitInsn(Opcodes.IADD);
                mv.visitInsn(Opcodes.POP);
            }
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
            // LEVEL 5
            if (config.getJunkLevel() > 4) {
                JUNK_NUM++;
                if (JUNK_NUM > MAX_JUNK_NUM) {
                    logger.debug("max junk code");
                    super.visitLdcInsn(value);
                    return;
                }
                mv.visitInsn(RandomUtil.genICONSTOpcode());
                mv.visitInsn(Opcodes.POP);
                mv.visitInsn(RandomUtil.genICONSTOpcode());
                mv.visitInsn(Opcodes.POP);
                super.visitLdcInsn(value);
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