package me.n1ar4.jar.obfuscator.jvmti;

/**
 * Code Encryptor use JNI
 */
public class CodeEncryptor {
    /**
     * Native Code Encrypt Method
     *
     * @param text ByteCode
     * @return Encrypted BytesCode
     */
    public native static byte[] encrypt(byte[] text, int length, byte[] key);
}
