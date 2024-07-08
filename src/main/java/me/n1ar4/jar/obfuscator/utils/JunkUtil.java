package me.n1ar4.jar.obfuscator.utils;

import me.n1ar4.jrandom.core.JRandom;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

public class JunkUtil {
    public static void addHttpCode(ClassVisitor cv) {
        MethodVisitor newMethod = cv.visitMethod(
                Opcodes.ACC_PUBLIC | Opcodes.ACC_STATIC,
                JRandom.getInstance().randomString(8),
                "()V", null, null);
        newMethod.visitCode();
        newMethod.visitTypeInsn(Opcodes.NEW, "java/net/URL");
        newMethod.visitInsn(Opcodes.DUP);
        newMethod.visitLdcInsn("https://" + JRandom.getInstance().randomString(16));
        newMethod.visitMethodInsn(Opcodes.INVOKESPECIAL, "java/net/URL", "<init>",
                "(Ljava/lang/String;)V", false);
        newMethod.visitVarInsn(Opcodes.ASTORE, 0);
        newMethod.visitVarInsn(Opcodes.ALOAD, 0);
        newMethod.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/net/URL", "openConnection",
                "()Ljava/net/URLConnection;", false);
        newMethod.visitTypeInsn(Opcodes.CHECKCAST, "java/net/HttpURLConnection");
        newMethod.visitVarInsn(Opcodes.ASTORE, 1);
        newMethod.visitVarInsn(Opcodes.ALOAD, 1);
        newMethod.visitLdcInsn("GET");
        newMethod.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/net/HttpURLConnection",
                "setRequestMethod", "(Ljava/lang/String;)V", false);
        newMethod.visitVarInsn(Opcodes.ALOAD, 1);
        newMethod.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/net/HttpURLConnection",
                "getResponseCode", "()I", false);
        newMethod.visitInsn(Opcodes.POP);
        newMethod.visitInsn(Opcodes.RETURN);
        newMethod.visitMaxs(3, 2);
        newMethod.visitEnd();
    }
    public static void addPrintMethod(ClassVisitor cv) {
        MethodVisitor newMethod = cv.visitMethod(Opcodes.ACC_PUBLIC | Opcodes.ACC_STATIC,
                JRandom.getInstance().randomString(8), "()V", null, null);
        newMethod.visitCode();
        newMethod.visitFieldInsn(Opcodes.GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
        newMethod.visitLdcInsn(JRandom.getInstance().randomString(32));
        newMethod.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/io/PrintStream", "println",
                "(Ljava/lang/String;)V", false);
        newMethod.visitInsn(Opcodes.RETURN);
        newMethod.visitMaxs(2, 1);
        newMethod.visitEnd();
    }
}
