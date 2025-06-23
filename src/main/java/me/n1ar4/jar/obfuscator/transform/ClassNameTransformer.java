package me.n1ar4.jar.obfuscator.transform;

import me.n1ar4.jar.obfuscator.Const;
import me.n1ar4.jar.obfuscator.asm.ClassNameVisitor;
import me.n1ar4.jar.obfuscator.core.ObfEnv;
import me.n1ar4.jar.obfuscator.loader.CustomClassLoader;
import me.n1ar4.jar.obfuscator.loader.CustomClassWriter;
import me.n1ar4.log.LogManager;
import me.n1ar4.log.Logger;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

public class ClassNameTransformer {
    private static final Logger logger = LogManager.getLogger();

    public static void transform(CustomClassLoader loader) {
        for (Map.Entry<String, String> entry : ObfEnv.classNameObfMapping.entrySet()) {
            String originalName = entry.getKey();
            String newName = entry.getValue();
            Path tempDir = Paths.get(Const.TEMP_DIR);

            if (ObfEnv.config.isUseSpringBoot()) {
                tempDir = tempDir.resolve("BOOT-INF/classes/");
            }
            if (ObfEnv.config.isUseWebWar()) {
                tempDir = tempDir.resolve("WEB-INF/classes/");
            }

            Path classPath = tempDir.resolve(Paths.get(originalName + ".class"));
            Path newClassPath = tempDir.resolve(Paths.get(newName + ".class"));

            logger.debug("混淆类名进行中 {} -> {}", classPath.toAbsolutePath(), newClassPath.toAbsolutePath());

            try {
                Files.createDirectories(newClassPath.getParent());
            } catch (Exception ignored) {
            }
            if (!Files.exists(classPath)) {
                logger.debug("class not exist: {}", classPath.toString());
                continue;
            }
            try {
                ClassReader classReader = new ClassReader(Files.readAllBytes(classPath));
                ClassWriter classWriter = new CustomClassWriter(classReader,
                        ObfEnv.config.isAsmAutoCompute() ? Const.WriterASMOptions : 0, loader);
                ClassNameVisitor changer = new ClassNameVisitor(classWriter);
                classReader.accept(changer, Const.ReaderASMOptions);
                Files.delete(classPath);
                Files.write(newClassPath, classWriter.toByteArray());
            } catch (Exception ex) {
                logger.error("transform error: {}", ex.toString());
            }
        }
        logger.info("rename class name finish");
    }
}
