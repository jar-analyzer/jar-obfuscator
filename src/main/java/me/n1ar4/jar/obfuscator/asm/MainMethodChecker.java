package me.n1ar4.jar.obfuscator.asm;

import me.n1ar4.jar.obfuscator.Const;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;

public class MainMethodChecker extends ClassVisitor {
    private boolean hasMainMethod = false;

    public MainMethodChecker() {
        super(Const.ASMVersion);
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
        if ("main".equals(name) && descriptor.equals("([Ljava/lang/String;)V") && access == 9) {
            hasMainMethod = true;
        }
        return super.visitMethod(access, name, descriptor, signature, exceptions);
    }

    public boolean hasMainMethod() {
        return hasMainMethod;
    }
}