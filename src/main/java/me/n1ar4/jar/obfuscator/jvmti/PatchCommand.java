package me.n1ar4.jar.obfuscator.jvmti;

import me.n1ar4.jar.obfuscator.utils.JNIUtil;
import me.n1ar4.jar.obfuscator.utils.OSUtil;
import me.n1ar4.log.LogManager;
import me.n1ar4.log.Logger;

import java.nio.file.Path;
import java.nio.file.Paths;

public class PatchCommand implements Constants {
    private static final Logger logger = LogManager.getLogger();
    private String jarPath;
    private String key;
    private String packageName;

    public String getJarPath() {
        return jarPath;
    }

    public void setJarPath(String jarPath) {
        this.jarPath = jarPath;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public void execute() {
        logger.info("patch jar: {}", jarPath);
        Path path = Paths.get(jarPath);

        if (!OSUtil.isArch64()) {
            System.out.println("ONLY SUPPORT ARCH 64");
            return;
        }

        if (key.length() != 16) {
            System.out.println("KEY LENGTH MUST BE 16");
            return;
        }

        Path libPath;
        Path tmp = Paths.get(TempDir);
        // extract encrypt dll and load it
        if (OSUtil.isWin()) {
            JNIUtil.extractDllSo(EncryptorDLL, null, false);
            libPath = tmp.resolve(EncryptorDLL);
        } else if (OSUtil.isMac()) {
            JNIUtil.extractDllSo(EncryptorDylib, null, false);
            libPath = tmp.resolve(EncryptorDylib);
        } else {
            JNIUtil.extractDllSo(EncryptorSO, null, false);
            libPath = tmp.resolve(EncryptorSO);
        }
        if (packageName == null || packageName.isEmpty()) {
            logger.error("need package name");
            return;
        }

        // do patch
        PatchHelper.patchJar(path, libPath, packageName, key.getBytes());
    }
}
