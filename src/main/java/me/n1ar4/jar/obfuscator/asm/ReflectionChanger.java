package me.n1ar4.jar.obfuscator.asm;

import me.n1ar4.jar.obfuscator.Const;
import me.n1ar4.jar.obfuscator.core.ObfEnv;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import java.util.Map;

public class ReflectionChanger extends ClassVisitor {
    public ReflectionChanger(ClassVisitor cv) {
        super(Opcodes.ASM9, cv);
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
        MethodVisitor mv = cv.visitMethod(access, name, descriptor, signature, exceptions);
        return new ReflectionAdapter(mv);
    }

    private static class ReflectionAdapter extends MethodVisitor {
        ReflectionAdapter(MethodVisitor mv) {
            super(Const.ASMVersion, mv);
        }

        @Override
        public void visitLdcInsn(Object value) {
            if (value instanceof String) {
                value = ((String) value).replace(".", "/");
                for (Map.Entry<String, String> entry : ObfEnv.classNameObfMapping.entrySet()) {
                    String key = entry.getKey().replace(".", "/");
                    String val = entry.getValue().replace("/", ".");
                    if (key.equals(value)) {
                        super.visitLdcInsn(val);
                        return;
                    }
                }
            }
            super.visitLdcInsn(value);
        }
    }
}