package me.n1ar4.jar.obfuscator.transform;

import me.n1ar4.jar.obfuscator.Const;
import me.n1ar4.jar.obfuscator.asm.CompileInfoVisitor;
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

public class DeleteInfoTransformer {
    private static final Logger logger = LogManager.getLogger();

    public static void transform(CustomClassLoader loader) {
        for (Map.Entry<String, String> entry : ObfEnv.classNameObfMapping.entrySet()) {
            String originalName = entry.getKey();
            Path tempDir = Paths.get(Const.TEMP_DIR);

            if (ObfEnv.config.isUseSpringBoot()) {
                tempDir = tempDir.resolve("BOOT-INF/classes/");
            }
            if (ObfEnv.config.isUseWebWar()) {
                tempDir = tempDir.resolve("WEB-INF/classes/");
            }

            Path classPath = tempDir.resolve(Paths.get(originalName + ".class"));

            logger.debug("删除编译信息进行中 {} -> {}", classPath.toAbsolutePath());

            if (!Files.exists(classPath)) {
                logger.debug("class not exist: {}", classPath.toString());
                continue;
            }
            try {
                ClassReader classReader = new ClassReader(Files.readAllBytes(classPath));
                ClassWriter classWriter = new CustomClassWriter(classReader,
                        ObfEnv.config.isAsmAutoCompute() ? Const.WriterASMOptions : 0, loader);
                CompileInfoVisitor changer = new CompileInfoVisitor(classWriter);
                classReader.accept(changer, Const.ReaderASMOptions);
                Files.delete(classPath);
                Files.write(classPath, classWriter.toByteArray());
            } catch (Exception ex) {
                logger.error("transform error: {}", ex.toString());
            }
        }
        logger.info("delete compile info finish");
    }
}
