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
import me.n1ar4.log.LogManager;
import me.n1ar4.log.Logger;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Stream;

public class ResourceTransformer {
    private static final Logger logger = LogManager.getLogger();

    public static void transform() {
        Path root = Paths.get(Const.TEMP_DIR);
        try (Stream<Path> stream = Files.walk(root)) {
            stream.filter(Files::isRegularFile)
                    .filter(path -> !path.toString().endsWith(".class"))
                    .forEach(path -> transformResource(root, path));
        } catch (IOException ex) {
            throw new IllegalStateException("resource transform failed", ex);
        }
        logger.info("resource transform finish");
    }

    private static void transformResource(Path root, Path path) {
        try {
            if (isTextResource(root, path)) {
                String content = new String(Files.readAllBytes(path), StandardCharsets.UTF_8);
                String newContent = replaceClassNames(content);
                if (!content.equals(newContent)) {
                    TransformerUtil.writeAtomically(path, newContent.getBytes(StandardCharsets.UTF_8));
                }
            }
            renameServiceFile(root, path);
        } catch (Exception ex) {
            throw new IllegalStateException("resource transform failed: " + path, ex);
        }
    }

    private static boolean isTextResource(Path root, Path path) {
        String normalized = root.relativize(path).toString().replace('\\', '/');
        String lower = normalized.toLowerCase(Locale.ROOT);
        return serviceIndex(lower) >= 0 ||
                lower.equals("meta-inf/manifest.mf") ||
                lower.equals("meta-inf/spring.factories") ||
                lower.equals("meta-inf/spring/org.springframework.boot.autoconfigure.autoconfiguration.imports") ||
                lower.endsWith(".properties") ||
                lower.endsWith(".xml") ||
                lower.endsWith(".yml") ||
                lower.endsWith(".yaml") ||
                lower.endsWith(".json") ||
                lower.endsWith(".txt") ||
                lower.endsWith(".imports") ||
                lower.endsWith(".factories") ||
                lower.endsWith(".handlers") ||
                lower.endsWith(".schemas") ||
                lower.endsWith(".tooling");
    }

    private static String replaceClassNames(String value) {
        for (Map.Entry<String, String> entry : ObfEnv.classNameObfMapping.entrySet()) {
            String oldInternal = entry.getKey();
            String newInternal = entry.getValue();
            value = value.replace(oldInternal, newInternal);
            value = value.replace(oldInternal.replace('/', '.'), newInternal.replace('/', '.'));
        }
        return value;
    }

    private static void renameServiceFile(Path root, Path path) throws IOException {
        String normalized = root.relativize(path).toString().replace('\\', '/');
        String lower = normalized.toLowerCase(Locale.ROOT);
        int serviceIndex = serviceIndex(lower);
        if (serviceIndex < 0) {
            return;
        }
        String servicePrefix = normalized.substring(0, serviceIndex) + "META-INF/services/";
        String serviceName = normalized.substring(servicePrefix.length());
        String mapped = ObfEnv.classNameObfMapping.get(serviceName.replace('.', '/'));
        if (mapped == null) {
            return;
        }
        Path newPath = root.resolve(Paths.get(servicePrefix + mapped.replace('/', '.')));
        if (!path.equals(newPath)) {
            Files.createDirectories(newPath.getParent());
            Files.move(path, newPath, StandardCopyOption.REPLACE_EXISTING);
        }
    }

    private static int serviceIndex(String normalizedLower) {
        String services = "meta-inf/services/";
        if (normalizedLower.startsWith(services)) {
            return 0;
        }
        int index = normalizedLower.indexOf("/" + services);
        if (index < 0) {
            return -1;
        }
        return index + 1;
    }
}
