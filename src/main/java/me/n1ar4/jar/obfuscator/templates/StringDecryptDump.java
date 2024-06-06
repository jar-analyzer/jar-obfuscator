package me.n1ar4.jar.obfuscator.templates;

import me.n1ar4.log.LogManager;
import me.n1ar4.log.Logger;
import org.objectweb.asm.*;

public class StringDecryptDump implements Opcodes {
    private static final Logger logger = LogManager.getLogger();
    public static String AES_KEY = null;
    public static String className = null;
    public static String methodName = null;
    private static String keyName = null;

    public static void changeKEY(String key) {
        if (key != null && key.length() == 16) {
            AES_KEY = key;
            logger.info("change decrypt aes key to: {}", key);
            return;
        }
        AES_KEY = "Y4SuperSecretKey";
        logger.warn("aes decrypt key length muse be 16");
        logger.info("change decrypt aes key to: {}", key);
    }

    public static void initName(String c, String m, String k) {
        String defaultClassName = "org/apache/commons/collections/list/AbstractHashMap";
        String defaultMethodName = "newMap";
        String defaultKeyName = "LiLiLLLiiiLLiiLLi";
        if (c == null || m == null || k == null) {
            className = defaultClassName;
            methodName = defaultMethodName;
            keyName = defaultKeyName;
            return;
        }
        c = c.replace(".", "/");
        if (c.isEmpty()) {
            className = defaultClassName;
        } else {
            className = c;
        }
        if (m.isEmpty()) {
            methodName = defaultMethodName;
        } else {
            methodName = m;
        }
        if (k.isEmpty()) {
            keyName = defaultKeyName;
        } else {
            keyName = k;
        }
    }

    public static byte[] dump() {
        ClassWriter classWriter = new ClassWriter(0);
        FieldVisitor fieldVisitor;
        MethodVisitor methodVisitor;
        classWriter.visit(V1_8, ACC_PUBLIC | ACC_SUPER, className, null, "java/lang/Object", null);
        classWriter.visitInnerClass("java/util/Base64$Encoder", "java/util/Base64", "Encoder", ACC_PUBLIC | ACC_STATIC);
        classWriter.visitInnerClass("java/util/Base64$Decoder", "java/util/Base64", "Decoder", ACC_PUBLIC | ACC_STATIC);
        {
            fieldVisitor = classWriter.visitField(ACC_PRIVATE | ACC_STATIC, keyName, "Ljava/lang/String;", null, null);
            fieldVisitor.visitEnd();
        }
        {
            fieldVisitor = classWriter.visitField(ACC_PRIVATE | ACC_FINAL | ACC_STATIC, "ALGORITHM", "Ljava/lang/String;", null, "AES");
            fieldVisitor.visitEnd();
        }
        {
            fieldVisitor = classWriter.visitField(ACC_PRIVATE | ACC_FINAL | ACC_STATIC, "CHARSET", "Ljava/nio/charset/Charset;", null, null);
            fieldVisitor.visitEnd();
        }
        {
            methodVisitor = classWriter.visitMethod(ACC_PUBLIC | ACC_STATIC, methodName, "(Ljava/lang/String;)Ljava/lang/String;", null, null);
            methodVisitor.visitCode();
            Label label0 = new Label();
            Label label1 = new Label();
            Label label2 = new Label();
            methodVisitor.visitTryCatchBlock(label0, label1, label2, "java/lang/Exception");
            methodVisitor.visitLabel(label0);
            methodVisitor.visitTypeInsn(NEW, "javax/crypto/spec/SecretKeySpec");
            methodVisitor.visitInsn(DUP);
            methodVisitor.visitFieldInsn(GETSTATIC, className, keyName, "Ljava/lang/String;");
            methodVisitor.visitFieldInsn(GETSTATIC, className, "CHARSET", "Ljava/nio/charset/Charset;");
            methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "java/lang/String", "getBytes", "(Ljava/nio/charset/Charset;)[B", false);
            methodVisitor.visitLdcInsn("AES");
            methodVisitor.visitMethodInsn(INVOKESPECIAL, "javax/crypto/spec/SecretKeySpec", "<init>", "([BLjava/lang/String;)V", false);
            methodVisitor.visitVarInsn(ASTORE, 1);
            Label label3 = new Label();
            methodVisitor.visitLabel(label3);
            methodVisitor.visitLdcInsn("AES");
            methodVisitor.visitMethodInsn(INVOKESTATIC, "javax/crypto/Cipher", "getInstance", "(Ljava/lang/String;)Ljavax/crypto/Cipher;", false);
            methodVisitor.visitVarInsn(ASTORE, 2);
            Label label4 = new Label();
            methodVisitor.visitLabel(label4);
            methodVisitor.visitVarInsn(ALOAD, 2);
            methodVisitor.visitInsn(ICONST_2);
            methodVisitor.visitVarInsn(ALOAD, 1);
            methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "javax/crypto/Cipher", "init", "(ILjava/security/Key;)V", false);
            Label label5 = new Label();
            methodVisitor.visitLabel(label5);
            methodVisitor.visitVarInsn(ALOAD, 0);
            methodVisitor.visitFieldInsn(GETSTATIC, className, "CHARSET", "Ljava/nio/charset/Charset;");
            methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "java/lang/String", "getBytes", "(Ljava/nio/charset/Charset;)[B", false);
            methodVisitor.visitVarInsn(ASTORE, 3);
            Label label6 = new Label();
            methodVisitor.visitLabel(label6);
            methodVisitor.visitVarInsn(ALOAD, 2);
            methodVisitor.visitMethodInsn(INVOKESTATIC, "java/util/Base64", "getDecoder", "()Ljava/util/Base64$Decoder;", false);
            methodVisitor.visitVarInsn(ALOAD, 3);
            methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "java/util/Base64$Decoder", "decode", "([B)[B", false);
            methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "javax/crypto/Cipher", "doFinal", "([B)[B", false);
            methodVisitor.visitVarInsn(ASTORE, 4);
            Label label7 = new Label();
            methodVisitor.visitLabel(label7);
            methodVisitor.visitTypeInsn(NEW, "java/lang/String");
            methodVisitor.visitInsn(DUP);
            methodVisitor.visitVarInsn(ALOAD, 4);
            methodVisitor.visitFieldInsn(GETSTATIC, className, "CHARSET", "Ljava/nio/charset/Charset;");
            methodVisitor.visitMethodInsn(INVOKESPECIAL, "java/lang/String", "<init>", "([BLjava/nio/charset/Charset;)V", false);
            methodVisitor.visitLabel(label1);
            methodVisitor.visitInsn(ARETURN);
            methodVisitor.visitLabel(label2);
            methodVisitor.visitFrame(Opcodes.F_SAME1, 0, null, 1, new Object[]{"java/lang/Exception"});
            methodVisitor.visitVarInsn(ASTORE, 1);
            Label label8 = new Label();
            methodVisitor.visitLabel(label8);
            methodVisitor.visitInsn(ACONST_NULL);
            methodVisitor.visitInsn(ARETURN);
            Label label9 = new Label();
            methodVisitor.visitLabel(label9);
            methodVisitor.visitLocalVariable("key", "Ljavax/crypto/spec/SecretKeySpec;", null, label3, label2, 1);
            methodVisitor.visitLocalVariable("cipher", "Ljavax/crypto/Cipher;", null, label4, label2, 2);
            methodVisitor.visitLocalVariable("data", "[B", null, label6, label2, 3);
            methodVisitor.visitLocalVariable("original", "[B", null, label7, label2, 4);
            methodVisitor.visitLocalVariable("e", "Ljava/lang/Exception;", null, label8, label9, 1);
            methodVisitor.visitLocalVariable("encrypted", "Ljava/lang/String;", null, label0, label9, 0);
            methodVisitor.visitMaxs(4, 5);
            methodVisitor.visitEnd();
        }
        {
            methodVisitor = classWriter.visitMethod(ACC_STATIC, "<clinit>", "()V", null, null);
            methodVisitor.visitCode();
            Label label0 = new Label();
            methodVisitor.visitLabel(label0);
            methodVisitor.visitLdcInsn(AES_KEY);
            methodVisitor.visitFieldInsn(PUTSTATIC, className, keyName, "Ljava/lang/String;");
            Label label1 = new Label();
            methodVisitor.visitLabel(label1);
            methodVisitor.visitFieldInsn(GETSTATIC, "java/nio/charset/StandardCharsets", "UTF_8", "Ljava/nio/charset/Charset;");
            methodVisitor.visitFieldInsn(PUTSTATIC, className, "CHARSET", "Ljava/nio/charset/Charset;");
            methodVisitor.visitInsn(RETURN);
            methodVisitor.visitMaxs(1, 0);
            methodVisitor.visitEnd();
        }
        classWriter.visitEnd();
        return classWriter.toByteArray();
    }
}
