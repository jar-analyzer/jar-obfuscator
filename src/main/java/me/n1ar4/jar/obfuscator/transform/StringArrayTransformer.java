package me.n1ar4.jar.obfuscator.transform;

import me.n1ar4.jar.obfuscator.Const;
import me.n1ar4.jar.obfuscator.asm.StringArrayVisitor;
import me.n1ar4.jar.obfuscator.core.ObfEnv;
import me.n1ar4.log.LogManager;
import me.n1ar4.log.Logger;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

@SuppressWarnings("all")
public class StringArrayTransformer {
    private static final Logger logger = LogManager.getLogger();
    public static int INDEX = 0;

    public static void transform() {
        for (Map.Entry<String, String> entry : ObfEnv.classNameObfMapping.entrySet()) {
            String newName = entry.getValue();
            Path tempDir = Paths.get(Const.TEMP_DIR);

            if (ObfEnv.config.isUseSpringBoot()) {
                tempDir = tempDir.resolve("BOOT-INF/classes/");
            }
            if (ObfEnv.config.isUseWebWar()) {
                tempDir = tempDir.resolve("WEB-INF/classes/");
            }

            Path newClassPath = tempDir.resolve(Paths.get(newName + ".class"));

            logger.debug("字符串提取混淆进行中 {} -> {}", newClassPath.toAbsolutePath());

            if (!Files.exists(newClassPath)) {
                logger.debug("class not exist: {}", newClassPath.toString());
                continue;
            }
            try {
                INDEX = 0;
                ClassReader classReader = new ClassReader(Files.readAllBytes(newClassPath));
                ClassWriter classWriter = new ClassWriter(classReader, ClassWriter.COMPUTE_MAXS);
                StringArrayVisitor changer = new StringArrayVisitor(classWriter);
                classReader.accept(changer, ClassReader.EXPAND_FRAMES);
                Files.delete(newClassPath);
                Files.write(newClassPath, classWriter.toByteArray());
            } catch (Exception ex) {
                ex.printStackTrace();
                logger.error("transform error: {}", ex.toString());
            }
        }
        logger.info("advance encrypt string finish");
    }
}
