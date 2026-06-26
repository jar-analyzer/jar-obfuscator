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

import me.n1ar4.jar.obfuscator.asm.StringArrayVisitor;
import me.n1ar4.jar.obfuscator.core.ObfEnv;
import me.n1ar4.jar.obfuscator.loader.CustomClassLoader;
import me.n1ar4.log.LogManager;
import me.n1ar4.log.Logger;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

@SuppressWarnings("all")
public class StringArrayTransformer {
    private static final Logger logger = LogManager.getLogger();
    public static int INDEX = 0;

    public static void transform(CustomClassLoader loader) {
        for (Map.Entry<String, String> entry : ObfEnv.classNameObfMapping.entrySet()) {
            String newName = entry.getValue();
            Path newClassPath = TransformerUtil.classPath(newName);

            logger.debug("字符串提取混淆进行中 {} -> {}", newClassPath.toAbsolutePath());

            if (!Files.exists(newClassPath)) {
                logger.debug("class not exist: {}", newClassPath.toString());
                continue;
            }
            try {
                INDEX = 0;
                TransformerUtil.transformClass(newClassPath, loader, StringArrayVisitor::new);
            } catch (Exception ex) {
                logger.error("transform error: {}", ex.toString());
                throw new IllegalStateException("string array transform failed: " + newName, ex);
            }
        }
        logger.info("advance encrypt string finish");
    }
}
