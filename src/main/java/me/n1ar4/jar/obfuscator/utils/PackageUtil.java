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
}
