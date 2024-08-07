package me.n1ar4.jar.obfuscator.transform;

import me.n1ar4.jar.obfuscator.Const;
import me.n1ar4.jar.obfuscator.asm.ReflectionChanger;
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
public class ReflectionTransformer {
    private static final Logger logger = LogManager.getLogger();

    public static void transform() {
        for (Map.Entry<String, String> entry : ObfEnv.classNameObfMapping.entrySet()) {
            String newName = entry.getValue();
            Path tempDir = Paths.get(Const.TEMP_DIR);
            Path newClassPath = tempDir.resolve(Paths.get(newName + ".class"));
            if (!Files.exists(newClassPath)) {
                logger.error("class not exist: {}", newClassPath.toString());
                continue;
            }
            try {
                ClassReader classReader = new ClassReader(Files.readAllBytes(newClassPath));
                ClassWriter classWriter = new ClassWriter(classReader, ClassWriter.COMPUTE_MAXS);
                ReflectionChanger changer = new ReflectionChanger(classWriter);
                classReader.accept(changer, Const.AnalyzeASMOptions);
                Files.delete(newClassPath);
                Files.write(newClassPath, classWriter.toByteArray());
            } catch (Exception ex) {
                logger.error("transform error: {}", ex.toString());
            }
        }
        logger.info("reflection transform finish");
    }
}
