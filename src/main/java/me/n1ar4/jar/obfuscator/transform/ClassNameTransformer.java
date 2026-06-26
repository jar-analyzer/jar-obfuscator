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

import me.n1ar4.jar.obfuscator.asm.ClassNameVisitor;
import me.n1ar4.jar.obfuscator.core.ObfEnv;
import me.n1ar4.jar.obfuscator.loader.CustomClassLoader;
import me.n1ar4.log.LogManager;
import me.n1ar4.log.Logger;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

public class ClassNameTransformer {
    private static final Logger logger = LogManager.getLogger();

    public static void transform(CustomClassLoader loader) {
        for (Map.Entry<String, String> entry : ObfEnv.classNameObfMapping.entrySet()) {
            String originalName = entry.getKey();
            String newName = entry.getValue();
            Path classPath = TransformerUtil.classPath(originalName);
            Path newClassPath = TransformerUtil.classPath(newName);

            logger.debug("混淆类名进行中 {} -> {}", classPath.toAbsolutePath(), newClassPath.toAbsolutePath());

            try {
                Files.createDirectories(newClassPath.getParent());
            } catch (Exception ex) {
                throw new IllegalStateException("create class output directory failed: " + newClassPath, ex);
            }
            if (!Files.exists(classPath)) {
                logger.debug("class not exist: {}", classPath.toString());
                continue;
            }
            try {
                TransformerUtil.transformClass(classPath, loader, ClassNameVisitor::new);
                if (!classPath.equals(newClassPath)) {
                    Files.createDirectories(newClassPath.getParent());
                    Files.move(classPath, newClassPath, java.nio.file.StandardCopyOption.REPLACE_EXISTING);
                }
            } catch (Exception ex) {
                logger.error("transform error: {}", ex.toString());
                throw new IllegalStateException("class name transform failed: " + originalName, ex);
            }
        }
        logger.info("rename class name finish");
    }
}
