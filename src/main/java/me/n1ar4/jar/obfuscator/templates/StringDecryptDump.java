package me.n1ar4.jar.obfuscator.templates;

import me.n1ar4.jar.obfuscator.utils.NameUtil;
import org.objectweb.asm.*;

public class StringDecryptDump implements Opcodes {
    public static String name;

    public static byte[] dump() {
        ClassWriter classWriter = new ClassWriter(0);
        FieldVisitor fieldVisitor;
        MethodVisitor methodVisitor;
        String newName = NameUtil.genNewName() + "/" + NameUtil.genNewName();
        name = newName;
        classWriter.visit(V1_8, ACC_PUBLIC | ACC_SUPER, newName, null, "java/lang/Object", null);
        classWriter.visitInnerClass("java/util/Base64$Encoder", "java/util/Base64", "Encoder", ACC_PUBLIC | ACC_STATIC);
        classWriter.visitInnerClass("java/util/Base64$Decoder", "java/util/Base64", "Decoder", ACC_PUBLIC | ACC_STATIC);
        {
            fieldVisitor = classWriter.visitField(ACC_PRIVATE | ACC_FINAL | ACC_STATIC, "KEY", "Ljava/lang/String;", null, "Y4SuperSecretKey");
            fieldVisitor.visitEnd();
        }
        {
            fieldVisitor = classWriter.visitField(ACC_PRIVATE | ACC_FINAL | ACC_STATIC, "ALGORITHM", "Ljava/lang/String;", null, "AES");
            fieldVisitor.visitEnd();
        }
        {
            methodVisitor = classWriter.visitMethod(ACC_PUBLIC, "<init>", "()V", null, null);
            methodVisitor.visitCode();
            Label label0 = new Label();
            methodVisitor.visitLabel(label0);
            methodVisitor.visitLineNumber(7, label0);
            methodVisitor.visitVarInsn(ALOAD, 0);
            methodVisitor.visitMethodInsn(INVOKESPECIAL, "java/lang/Object", "<init>", "()V", false);
            methodVisitor.visitInsn(RETURN);
            Label label1 = new Label();
            methodVisitor.visitLabel(label1);
            methodVisitor.visitLocalVariable("this", "L" + newName + ";", null, label0, label1, 0);
            methodVisitor.visitMaxs(1, 1);
            methodVisitor.visitEnd();
        }
        {
            methodVisitor = classWriter.visitMethod(ACC_PUBLIC | ACC_STATIC, "i", "(Ljava/lang/String;)Ljava/lang/String;", null, null);
            methodVisitor.visitCode();
            Label label0 = new Label();
            Label label1 = new Label();
            Label label2 = new Label();
            methodVisitor.visitTryCatchBlock(label0, label1, label2, "java/lang/Exception");
            methodVisitor.visitLabel(label0);
            methodVisitor.visitLineNumber(12, label0);
            methodVisitor.visitTypeInsn(NEW, "javax/crypto/spec/SecretKeySpec");
            methodVisitor.visitInsn(DUP);
            methodVisitor.visitLdcInsn("Y4SuperSecretKey");
            methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "java/lang/String", "getBytes", "()[B", false);
            methodVisitor.visitLdcInsn("AES");
            methodVisitor.visitMethodInsn(INVOKESPECIAL, "javax/crypto/spec/SecretKeySpec", "<init>", "([BLjava/lang/String;)V", false);
            methodVisitor.visitVarInsn(ASTORE, 1);
            Label label3 = new Label();
            methodVisitor.visitLabel(label3);
            methodVisitor.visitLineNumber(13, label3);
            methodVisitor.visitLdcInsn("AES");
            methodVisitor.visitMethodInsn(INVOKESTATIC, "javax/crypto/Cipher", "getInstance", "(Ljava/lang/String;)Ljavax/crypto/Cipher;", false);
            methodVisitor.visitVarInsn(ASTORE, 2);
            Label label4 = new Label();
            methodVisitor.visitLabel(label4);
            methodVisitor.visitLineNumber(14, label4);
            methodVisitor.visitVarInsn(ALOAD, 2);
            methodVisitor.visitInsn(ICONST_1);
            methodVisitor.visitVarInsn(ALOAD, 1);
            methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "javax/crypto/Cipher", "init", "(ILjava/security/Key;)V", false);
            Label label5 = new Label();
            methodVisitor.visitLabel(label5);
            methodVisitor.visitLineNumber(16, label5);
            methodVisitor.visitVarInsn(ALOAD, 2);
            methodVisitor.visitVarInsn(ALOAD, 0);
            methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "java/lang/String", "getBytes", "()[B", false);
            methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "javax/crypto/Cipher", "doFinal", "([B)[B", false);
            methodVisitor.visitVarInsn(ASTORE, 3);
            Label label6 = new Label();
            methodVisitor.visitLabel(label6);
            methodVisitor.visitLineNumber(17, label6);
            methodVisitor.visitMethodInsn(INVOKESTATIC, "java/util/Base64", "getEncoder", "()Ljava/util/Base64$Encoder;", false);
            methodVisitor.visitVarInsn(ALOAD, 3);
            methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "java/util/Base64$Encoder", "encodeToString", "([B)Ljava/lang/String;", false);
            methodVisitor.visitLabel(label1);
            methodVisitor.visitInsn(ARETURN);
            methodVisitor.visitLabel(label2);
            methodVisitor.visitLineNumber(18, label2);
            methodVisitor.visitFrame(Opcodes.F_SAME1, 0, null, 1, new Object[]{"java/lang/Exception"});
            methodVisitor.visitVarInsn(ASTORE, 1);
            Label label7 = new Label();
            methodVisitor.visitLabel(label7);
            methodVisitor.visitLineNumber(19, label7);
            methodVisitor.visitInsn(ACONST_NULL);
            methodVisitor.visitInsn(ARETURN);
            Label label8 = new Label();
            methodVisitor.visitLabel(label8);
            methodVisitor.visitLocalVariable("key", "Ljavax/crypto/spec/SecretKeySpec;", null, label3, label2, 1);
            methodVisitor.visitLocalVariable("cipher", "Ljavax/crypto/Cipher;", null, label4, label2, 2);
            methodVisitor.visitLocalVariable("encrypted", "[B", null, label6, label2, 3);
            methodVisitor.visitLocalVariable("e", "Ljava/lang/Exception;", null, label7, label8, 1);
            methodVisitor.visitLocalVariable("input", "Ljava/lang/String;", null, label0, label8, 0);
            methodVisitor.visitMaxs(4, 4);
            methodVisitor.visitEnd();
        }
        {
            methodVisitor = classWriter.visitMethod(ACC_PUBLIC | ACC_STATIC, "I", "(Ljava/lang/String;)Ljava/lang/String;", null, null);
            methodVisitor.visitCode();
            Label label0 = new Label();
            Label label1 = new Label();
            Label label2 = new Label();
            methodVisitor.visitTryCatchBlock(label0, label1, label2, "java/lang/Exception");
            methodVisitor.visitLabel(label0);
            methodVisitor.visitLineNumber(24, label0);
            methodVisitor.visitTypeInsn(NEW, "javax/crypto/spec/SecretKeySpec");
            methodVisitor.visitInsn(DUP);
            methodVisitor.visitLdcInsn("Y4SuperSecretKey");
            methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "java/lang/String", "getBytes", "()[B", false);
            methodVisitor.visitLdcInsn("AES");
            methodVisitor.visitMethodInsn(INVOKESPECIAL, "javax/crypto/spec/SecretKeySpec", "<init>", "([BLjava/lang/String;)V", false);
            methodVisitor.visitVarInsn(ASTORE, 1);
            Label label3 = new Label();
            methodVisitor.visitLabel(label3);
            methodVisitor.visitLineNumber(25, label3);
            methodVisitor.visitLdcInsn("AES");
            methodVisitor.visitMethodInsn(INVOKESTATIC, "javax/crypto/Cipher", "getInstance", "(Ljava/lang/String;)Ljavax/crypto/Cipher;", false);
            methodVisitor.visitVarInsn(ASTORE, 2);
            Label label4 = new Label();
            methodVisitor.visitLabel(label4);
            methodVisitor.visitLineNumber(26, label4);
            methodVisitor.visitVarInsn(ALOAD, 2);
            methodVisitor.visitInsn(ICONST_2);
            methodVisitor.visitVarInsn(ALOAD, 1);
            methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "javax/crypto/Cipher", "init", "(ILjava/security/Key;)V", false);
            Label label5 = new Label();
            methodVisitor.visitLabel(label5);
            methodVisitor.visitLineNumber(28, label5);
            methodVisitor.visitVarInsn(ALOAD, 2);
            methodVisitor.visitMethodInsn(INVOKESTATIC, "java/util/Base64", "getDecoder", "()Ljava/util/Base64$Decoder;", false);
            methodVisitor.visitVarInsn(ALOAD, 0);
            methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "java/util/Base64$Decoder", "decode", "(Ljava/lang/String;)[B", false);
            methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "javax/crypto/Cipher", "doFinal", "([B)[B", false);
            methodVisitor.visitVarInsn(ASTORE, 3);
            Label label6 = new Label();
            methodVisitor.visitLabel(label6);
            methodVisitor.visitLineNumber(29, label6);
            methodVisitor.visitTypeInsn(NEW, "java/lang/String");
            methodVisitor.visitInsn(DUP);
            methodVisitor.visitVarInsn(ALOAD, 3);
            methodVisitor.visitMethodInsn(INVOKESPECIAL, "java/lang/String", "<init>", "([B)V", false);
            methodVisitor.visitLabel(label1);
            methodVisitor.visitInsn(ARETURN);
            methodVisitor.visitLabel(label2);
            methodVisitor.visitLineNumber(30, label2);
            methodVisitor.visitFrame(Opcodes.F_SAME1, 0, null, 1, new Object[]{"java/lang/Exception"});
            methodVisitor.visitVarInsn(ASTORE, 1);
            Label label7 = new Label();
            methodVisitor.visitLabel(label7);
            methodVisitor.visitLineNumber(31, label7);
            methodVisitor.visitInsn(ACONST_NULL);
            methodVisitor.visitInsn(ARETURN);
            Label label8 = new Label();
            methodVisitor.visitLabel(label8);
            methodVisitor.visitLocalVariable("key", "Ljavax/crypto/spec/SecretKeySpec;", null, label3, label2, 1);
            methodVisitor.visitLocalVariable("cipher", "Ljavax/crypto/Cipher;", null, label4, label2, 2);
            methodVisitor.visitLocalVariable("original", "[B", null, label6, label2, 3);
            methodVisitor.visitLocalVariable("e", "Ljava/lang/Exception;", null, label7, label8, 1);
            methodVisitor.visitLocalVariable("encrypted", "Ljava/lang/String;", null, label0, label8, 0);
            methodVisitor.visitMaxs(4, 4);
            methodVisitor.visitEnd();
        }
        classWriter.visitEnd();
        return classWriter.toByteArray();
    }
}
