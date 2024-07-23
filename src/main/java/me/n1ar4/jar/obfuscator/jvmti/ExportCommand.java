package me.n1ar4.jar.obfuscator.jvmti;

import me.n1ar4.jar.obfuscator.utils.JNIUtil;
import me.n1ar4.jar.obfuscator.utils.OSUtil;
import me.n1ar4.log.LogManager;
import me.n1ar4.log.Logger;

public class ExportCommand implements Constants {
    private static final Logger logger = LogManager.getLogger();
    private String outputPath;

    public String getOutputPath() {
        return outputPath;
    }

    public void setOutputPath(String outputPath) {
        this.outputPath = outputPath;
    }

    public void execute() {
        logger.info("execute export command");

        if (!OSUtil.isArch64()) {
            System.out.println("ONLY SUPPORT ARCH 64");
            return;
        }

        if (OSUtil.isWin()) {
            JNIUtil.extractDllSo(DecrypterDLL, outputPath, false);
            System.out.println("----------- ADD VM OPTIONS (WINDOWS) -----------");
            System.out.println("java -XX:+DisableAttachMechanism " +
                    "-agentpath:/path/to/libdecrypter.dll=PACKAGE_NAME=xxx,KEY=YOUR-KEY [other-params]");
        } else if (OSUtil.isMac()) {
            JNIUtil.extractDllSo(DecrypterDylib, outputPath, false);
            System.out.println("----------- ADD VM OPTIONS (MacOS) -----------");
            System.out.println("java -XX:+DisableAttachMechanism " +
                    "-agentpath:/path/to/libdecrypter.dylib=PACKAGE_NAME=xxx,KEY=YOUR-KEY [other-params]");
        } else {
            JNIUtil.extractDllSo(DecrypterSo, outputPath, false);
            System.out.println("----------- ADD VM OPTIONS (LINUX) -----------");
            System.out.println("java -XX:+DisableAttachMechanism " +
                    "-agentpath:/path/to/libdecrypter.so=PACKAGE_NAME=xxx,KEY=YOUR-KEY [other-params]");
        }
    }
}