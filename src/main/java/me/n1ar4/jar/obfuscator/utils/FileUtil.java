package me.n1ar4.jar.obfuscator.utils;

import java.nio.file.Path;

public class FileUtil {
    public static String getFileNameWithoutExt(Path path) {
        String fileName = path.getFileName().toString();
        int dotIndex = fileName.lastIndexOf('.');
        if (dotIndex > 0) {
            fileName = fileName.substring(0, dotIndex);
        }
        return fileName;
    }
}
