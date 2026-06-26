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

import me.n1ar4.jar.obfuscator.asm.CompileInfoVisitor;
import me.n1ar4.jar.obfuscator.core.ObfEnv;
import me.n1ar4.jar.obfuscator.loader.CustomClassLoader;
import me.n1ar4.log.LogManager;
import me.n1ar4.log.Logger;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

public class DeleteInfoTransformer {
    private static final Logger logger = LogManager.getLogger();

    public static void transform(CustomClassLoader loader) {
        for (Map.Entry<String, String> entry : ObfEnv.classNameObfMapping.entrySet()) {
            String originalName = entry.getKey();
            Path classPath = TransformerUtil.classPath(originalName);

            logger.debug("删除编译信息进行中 {} -> {}", classPath.toAbsolutePath());

            if (!Files.exists(classPath)) {
                logger.debug("class not exist: {}", classPath.toString());
                continue;
            }
            try {
                TransformerUtil.transformClass(classPath, loader, CompileInfoVisitor::new);
            } catch (Exception ex) {
                logger.error("transform error: {}", ex.toString());
                throw new IllegalStateException("delete info transform failed: " + originalName, ex);
            }
        }
        logger.info("delete compile info finish");
    }
}
