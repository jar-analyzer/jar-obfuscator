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

import me.n1ar4.jar.obfuscator.asm.ParameterVisitor;
import me.n1ar4.jar.obfuscator.core.ObfEnv;
import me.n1ar4.jar.obfuscator.loader.CustomClassLoader;
import me.n1ar4.log.LogManager;
import me.n1ar4.log.Logger;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

@SuppressWarnings("all")
public class ParameterTransformer {
    private static final Logger logger = LogManager.getLogger();

    public static void transform(CustomClassLoader loader) {
        for (Map.Entry<String, String> entry : ObfEnv.classNameObfMapping.entrySet()) {
            String newName = entry.getValue();
            Path newClassPath = TransformerUtil.classPath(newName);

            logger.debug("方法参数混淆进行中 {} -> {}", newClassPath.toAbsolutePath());

            if (!Files.exists(newClassPath)) {
                logger.debug("class not exist: {}", newClassPath.toString());
                continue;
            }
            try {
                TransformerUtil.transformClass(newClassPath, loader, ParameterVisitor::new);
            } catch (Exception ex) {
                logger.error("transform error: {}", ex.toString());
                throw new IllegalStateException("parameter transform failed: " + newName, ex);
            }
        }
        logger.info("rename parameter name finish");
    }
}
