/*
 * MIT License
 *
 * Project URL: https://github.com/jar-analyzer/jar-obfuscator
 *
 * Copyright (c) 2024-2026 4ra1n (https://github.com/4ra1n)
 *
 * This project is distributed under the MIT license.
 *
 * https://opensource.org/license/mit
 */

package me.n1ar4.jar.obfuscator.transform;

import me.n1ar4.jar.obfuscator.Const;
import me.n1ar4.jar.obfuscator.core.ObfEnv;
import me.n1ar4.jar.obfuscator.loader.CustomClassLoader;
import me.n1ar4.jar.obfuscator.loader.CustomClassWriter;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

public class TransformerUtil {
    public interface VisitorFactory {
        ClassVisitor create(ClassWriter classWriter);
    }

    private TransformerUtil() {
    }

    public static Path classRoot() {
        Path tempDir = Paths.get(Const.TEMP_DIR);
        if (ObfEnv.config.isUseSpringBoot()) {
            tempDir = tempDir.resolve("BOOT-INF/classes/");
        }
        if (ObfEnv.config.isUseWebWar()) {
            tempDir = tempDir.resolve("WEB-INF/classes/");
        }
        return tempDir;
    }

    public static Path classPath(String internalName) {
        return classRoot().resolve(Paths.get(internalName + ".class"));
    }

    public static void transformClass(Path classPath, CustomClassLoader loader,
                                      VisitorFactory visitorFactory) throws IOException {
        ClassReader classReader = new ClassReader(Files.readAllBytes(classPath));
        ClassWriter classWriter = new CustomClassWriter(classReader,
                ObfEnv.config.isAsmAutoCompute() ? Const.WriterASMOptions : 0, loader);
        classReader.accept(visitorFactory.create(classWriter), Const.ReaderASMOptions);
        writeAtomically(classPath, classWriter.toByteArray());
    }

    public static void writeAtomically(Path path, byte[] data) throws IOException {
        ensureUnderTempDir(path);
        Path parent = path.getParent();
        if (parent != null) {
            Files.createDirectories(parent);
        }
        Path tempFile = Files.createTempFile(parent, path.getFileName().toString(), ".tmp");
        try {
            Files.write(tempFile, data);
            try {
                Files.move(tempFile, path, StandardCopyOption.REPLACE_EXISTING,
                        StandardCopyOption.ATOMIC_MOVE);
            } catch (IOException ignored) {
                Files.move(tempFile, path, StandardCopyOption.REPLACE_EXISTING);
            }
        } finally {
            Files.deleteIfExists(tempFile);
        }
    }

    private static void ensureUnderTempDir(Path path) throws IOException {
        Path tempRoot = Paths.get(Const.TEMP_DIR).toRealPath();
        Path parent = path.toAbsolutePath().normalize().getParent();
        if (parent == null) {
            throw new IOException("path has no parent: " + path);
        }
        Files.createDirectories(parent);
        Path realParent = parent.toRealPath();
        if (!realParent.startsWith(tempRoot)) {
            throw new IOException("refuse to write temp file outside " + Const.TEMP_DIR + ": " + path);
        }
    }
}
