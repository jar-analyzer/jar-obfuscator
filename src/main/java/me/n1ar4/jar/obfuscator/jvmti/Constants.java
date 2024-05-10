package me.n1ar4.jar.obfuscator.jvmti;

/**
 * Constants
 */
@SuppressWarnings("all")
public interface Constants {
    String DecrypterDLL = "libdecrypter.dll";
    String DecrypterSo = "libdecrypter.so";
    String EncryptorDLL = "libencryptor.dll";
    String EncryptorSO = "libencryptor.so";
    String TempDir = "code-encryptor-temp";
    String NewFileSuffix = "encrypted";
    String DllFile = ".dll";
    String SOFile = ".so";
    String ClassFile = ".class";
    String WindowsOS = "windows";
    Integer BufSize = 16384;
}
