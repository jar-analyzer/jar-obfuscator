package me.n1ar4.jar.obfuscator.utils;

import me.n1ar4.jar.obfuscator.base.ClassReference;
import me.n1ar4.jar.obfuscator.base.MethodReference;
import me.n1ar4.jar.obfuscator.config.BaseConfig;
import me.n1ar4.jar.obfuscator.core.AnalyzeEnv;
import org.objectweb.asm.Opcodes;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class PackageUtil {
    private static final List<String> internalList = new ArrayList<>();
    public static void buildInternalBlackList() {
        // JNI 的 CLASS 默认加到黑名单里面
        for (Map.Entry<ClassReference.Handle, List<MethodReference>> entry :
                AnalyzeEnv.methodsInClassMap.entrySet()) {
            String className = entry.getKey().getName();
            List<MethodReference> ref = entry.getValue();
            for (MethodReference m : ref) {
                int access = m.getAccess();
                if ((access & Opcodes.ACC_NATIVE) != 0) {
                    internalList.add(className);
                    break;
                }
            }
        }
    }

    public static boolean notInWhiteList(String c, BaseConfig config) {
        boolean inWhiteList = false;
        for (String s : config.getObfuscatePackage()) {
            if (c.startsWith(s)) {
                inWhiteList = true;
                break;
            }
        }
        return !inWhiteList;
    }

    public static boolean inBlackClass(String className, BaseConfig config) {
        className = className.replace(".", "/");
        for (String s : config.getClassBlackList()) {
            s = s.replace(".", "/");
            if (className.equals(s)) {
                return true;
            }
        }
        for (String s : internalList) {
            s = s.replace(".", "/");
            if (className.equals(s)) {
                return true;
            }
        }
        return false;
    }

    public static boolean inRootPackage(String className, BaseConfig config) {
        className = className.replace("/", ".");
        for (String s : config.getRootPackages()) {
            s = s.replace("/", ".");
            if (className.startsWith(s)) {
                return true;
            }
        }
        return false;
    }
}
