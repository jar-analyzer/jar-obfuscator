package me.n1ar4.jar.obfuscator.transform;

import me.n1ar4.jar.obfuscator.Const;
import me.n1ar4.jar.obfuscator.asm.MainMethodChecker;
import me.n1ar4.jar.obfuscator.core.ObfEnv;
import me.n1ar4.log.LogManager;
import me.n1ar4.log.Logger;
import org.objectweb.asm.ClassReader;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

public class MainClassTransformer {
    private static final Logger logger = LogManager.getLogger();

    public static void transform() {
        for (Map.Entry<String, String> entry : ObfEnv.classNameObfMapping.entrySet()) {
            try {
                String originalName = entry.getKey();
                Path tempDir = Paths.get(Const.TEMP_DIR);
                Path classPath = tempDir.resolve(Paths.get(originalName + ".class"));
                byte[] classBytes = Files.readAllBytes(classPath);
                ClassReader classReader = new ClassReader(classBytes);
                MainMethodChecker checker = new MainMethodChecker();
                classReader.accept(checker, 0);
                if (checker.hasMainMethod()) {
                    logger.info("find main class: {}", originalName);
                }
            } catch (Exception ex) {
                logger.error("transform error: {}", ex.toString());
            }
        }
    }
}
