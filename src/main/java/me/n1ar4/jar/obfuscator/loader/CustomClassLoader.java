package me.n1ar4.jar.obfuscator.loader;

import me.n1ar4.log.LogManager;
import me.n1ar4.log.Logger;

import java.io.*;
import java.nio.file.Path;
import java.util.Enumeration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.JarInputStream;

public class CustomClassLoader extends ClassLoader {
    private static final Logger logger = LogManager.getLogger();
    public static final String LIB_DIR = "jar-obf-lib";
    // 缓冲处理
    private final Map<String, byte[]> classCache = new ConcurrentHashMap<>();

    private final File baseJar;

    public CustomClassLoader(Path baseJar) {
        this.baseJar = baseJar.toFile();
    }

    @Override
    public Class<?> findClass(String name) throws ClassNotFoundException {
        byte[] classData = loadClassData(name);
        if (classData != null) {
            return defineClass(name, classData, 0, classData.length);
        }
        logger.warn("not found class: " + name);
        throw new ClassNotFoundException(name);
    }

    private byte[] loadClassData(String className) {
        // 先检查缓存
        byte[] cached = classCache.get(className);
        if (cached != null) {
            return cached;
        }
        // 首先尝试从基础JAR加载
        byte[] classData = loadClassFromJar(this.baseJar, className);
        if (classData != null) {
            classCache.put(className, classData);
            return classData;
        }
        // 然后尝试从库目录中的其他JAR文件加载
        File libDir = new File(LIB_DIR);
        if (libDir.exists() && libDir.isDirectory()) {
            File[] jarFiles = libDir.listFiles((dir, name) -> name.toLowerCase().endsWith(".jar"));
            if (jarFiles != null) {
                for (File jarFile : jarFiles) {
                    classData = loadClassFromJar(jarFile, className);
                    if (classData != null) {
                        classCache.put(className, classData);
                        return classData;
                    }
                }
            }
        }
        return null;
    }

    private byte[] loadClassFromJar(File jarFile, String className) {
        try (JarFile jar = new JarFile(jarFile)) {
            return loadClassFromJarFile(jar, className);
        } catch (IOException e) {
            logger.error("无法读取 JAR 文件: " + jarFile.getName() + ", 错误: " + e.getMessage());
            return null;
        }
    }

    private byte[] loadClassFromJarFile(JarFile jar, String className) {
        String classPath = className.replace('.', '/') + ".class";
        try {
            JarEntry entry = jar.getJarEntry(classPath);
            if (entry != null) {
                return readEntryBytes(jar, entry);
            }
            Enumeration<JarEntry> entries = jar.entries();
            while (entries.hasMoreElements()) {
                JarEntry jarEntry = entries.nextElement();
                if (jarEntry.getName().toLowerCase().endsWith(".jar")) {
                    byte[] classData = loadClassFromNestedJarInMemory(jar, jarEntry, className);
                    if (classData != null) {
                        return classData;
                    }
                }
            }
        } catch (IOException e) {
            logger.error("搜索类文件时出错: " + e.getMessage());
        }

        return null;
    }

    private byte[] loadClassFromNestedJarInMemory(JarFile parentJar, JarEntry nestedJarEntry, String className) {
        try (InputStream nestedJarStream = parentJar.getInputStream(nestedJarEntry);
             ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            byte[] buffer = new byte[8192];
            int bytesRead;
            while ((bytesRead = nestedJarStream.read(buffer)) != -1) {
                baos.write(buffer, 0, bytesRead);
            }
            try (ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
                 JarInputStream jarInputStream = new JarInputStream(bais)) {
                String classPath = className.replace('.', '/') + ".class";
                JarEntry entry;
                while ((entry = jarInputStream.getNextJarEntry()) != null) {
                    if (entry.getName().equals(classPath)) {
                        return readStreamBytes(jarInputStream);
                    }
                    if (entry.getName().toLowerCase().endsWith(".jar")) {
                        byte[] nestedClassData = loadClassFromNestedJarStream(jarInputStream, className);
                        if (nestedClassData != null) {
                            return nestedClassData;
                        }
                    }
                }
            }
        } catch (IOException e) {
            logger.error("处理嵌套 JAR 时出错: " + nestedJarEntry.getName() + ", 错误: " + e.getMessage());
        }

        return null;
    }

    private byte[] loadClassFromNestedJarStream(JarInputStream parentStream, String className) {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            byte[] buffer = new byte[8192];
            int bytesRead;
            while ((bytesRead = parentStream.read(buffer)) != -1) {
                baos.write(buffer, 0, bytesRead);
            }
            try (ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
                 JarInputStream jarInputStream = new JarInputStream(bais)) {
                String classPath = className.replace('.', '/') + ".class";
                JarEntry entry;
                while ((entry = jarInputStream.getNextJarEntry()) != null) {
                    if (entry.getName().equals(classPath)) {
                        return readStreamBytes(jarInputStream);
                    }
                }
            }
        } catch (IOException e) {
            logger.error("处理深层嵌套 JAR 时出错: " + e.getMessage());
        }

        return null;
    }

    private byte[] readEntryBytes(JarFile jar, JarEntry entry) throws IOException {
        try (InputStream inputStream = jar.getInputStream(entry)) {
            return readStreamBytes(inputStream);
        }
    }

    private byte[] readStreamBytes(InputStream inputStream) throws IOException {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            byte[] buffer = new byte[8192];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                baos.write(buffer, 0, bytesRead);
            }
            return baos.toByteArray();
        }
    }
}