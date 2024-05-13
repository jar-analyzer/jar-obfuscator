package me.n1ar4.jar.obfuscator.transform;

import me.n1ar4.jar.obfuscator.Const;
import me.n1ar4.jar.obfuscator.asm.ClassNameChanger;
import me.n1ar4.jar.obfuscator.core.ObfEnv;
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

    public static void transform() {
        for (Map.Entry<String, String> entry : ObfEnv.classNameObfMapping.entrySet()) {
            String originalName = entry.getKey();
            String newName = entry.getValue();
            Path tempDir = Paths.get(Const.TEMP_DIR);
            Path classPath = tempDir.resolve(Paths.get(originalName + ".class"));
            Path newClassPath = tempDir.resolve(Paths.get(newName + ".class"));
            try {
                Files.createDirectories(newClassPath.getParent());
            } catch (Exception ignored) {
            }
            if (!Files.exists(classPath)) {
                logger.error("class not exist: {}", classPath.toString());
                continue;
            }
            try {
                ClassReader classReader = new ClassReader(Files.readAllBytes(classPath));
                ClassWriter classWriter = new ClassWriter(classReader, 0);
                ClassNameChanger changer = new ClassNameChanger(classWriter);
                classReader.accept(changer, 0);
                Files.delete(classPath);
                Files.write(newClassPath, classWriter.toByteArray());
            } catch (Exception ex) {
                logger.error("transform error: {}", ex.toString());
            }
        }
        logger.info("rename class name finish");
    }
}
