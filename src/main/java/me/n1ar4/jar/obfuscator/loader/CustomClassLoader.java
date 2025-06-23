package me.n1ar4.jar.obfuscator.loader;

import me.n1ar4.log.LogManager;
import me.n1ar4.log.Logger;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class CustomClassLoader extends ClassLoader {
    private static final Logger logger = LogManager.getLogger();
    public static final String LIB_DIR = "jar-obf-lib";

    @Override
    public Class<?> findClass(String name) {
        byte[] classData = loadClassData(name);
        if (classData != null) {
            return defineClass(name, classData, 0, classData.length);
        }
        logger.warn("not found class: " + name);
        return null;
    }

    private byte[] loadClassData(String className) {
        File libDir = new File(LIB_DIR);
        if (!libDir.exists() || !libDir.isDirectory()) {
            return null;
        }
        File[] jarFiles = libDir.listFiles((dir, name) -> name.endsWith(".jar"));
        if (jarFiles == null) {
            return null;
        }
        for (File jarFile : jarFiles) {
            try (JarFile jar = new JarFile(jarFile)) {
                Enumeration<JarEntry> entries = jar.entries();
                while (entries.hasMoreElements()) {
                    JarEntry entry = entries.nextElement();
                    if (entry.getName().endsWith(".class")) {
                        String entryClassName = entry.getName().replace("/", ".")
                                .replace(".class", "");
                        if (entryClassName.equals(className)) {
                            try (InputStream inputStream = jar.getInputStream(entry);
                                 ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
                                byte[] buffer = new byte[1024];
                                int bytesRead;
                                while ((bytesRead = inputStream.read(buffer)) != -1) {
                                    outputStream.write(buffer, 0, bytesRead);
                                }
                                return outputStream.toByteArray();
                            }
                        }
                    }
                }
            } catch (IOException e) {
                logger.error("load class error: {}", e.getMessage());
            }
        }
        return null;
    }
}