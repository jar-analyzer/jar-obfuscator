package me.n1ar4.jar.obfuscator.transform;

import me.n1ar4.jar.obfuscator.Const;
import me.n1ar4.jar.obfuscator.asm.JunkCodeChanger;
import me.n1ar4.jar.obfuscator.config.BaseConfig;
import me.n1ar4.jar.obfuscator.core.ObfEnv;
import me.n1ar4.jar.obfuscator.loader.CustomClassLoader;
import me.n1ar4.jar.obfuscator.loader.CustomClassWriter;
import me.n1ar4.log.LogManager;
import me.n1ar4.log.Logger;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodTooLargeException;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

@SuppressWarnings("all")
public class JunkCodeTransformer {
    private static final Logger logger = LogManager.getLogger();

    public static void transform(BaseConfig config) {
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
                CustomClassLoader loader = new CustomClassLoader();
                // COMPUTE_FRAMES 需要修改 CLASSLOADER 来计算
                ClassWriter classWriter = new CustomClassWriter(classReader,
                        ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS, loader);
                JunkCodeChanger changer = new JunkCodeChanger(classWriter, config);
                classReader.accept(changer, Const.AnalyzeASMOptions);
                Files.delete(newClassPath);
                Files.write(newClassPath, classWriter.toByteArray());
            } catch (MethodTooLargeException ex) {
                logger.error("method too large");
                logger.error("please check max junk config");
                return;
            } catch (Exception ex) {
                logger.error("transform error: {}", ex.toString());
            }
        }
        logger.info("junk code transform finish");
    }
}
