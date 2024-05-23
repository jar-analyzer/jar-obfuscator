package me.n1ar4.jar.obfuscator.utils;

import me.n1ar4.jar.obfuscator.config.BaseConfig;

public class PackageUtil {
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

    public static boolean InBlackClass(String className, BaseConfig config) {
        className = className.replace(".", "/");
        for (String s : config.getClassBlackList()) {
            s = s.replace(".", "/");
            if (className.equals(s)) {
                return true;
            }
        }
        return false;
    }
}
