package me.n1ar4.jar.obfuscator.transform;

import me.n1ar4.jar.obfuscator.Const;
import me.n1ar4.jar.obfuscator.asm.JunkCodeVisitor;
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

            if (ObfEnv.config.isUseSpringBoot()) {
                tempDir = tempDir.resolve("BOOT-INF/classes/");
            }
            if (ObfEnv.config.isUseWebWar()) {
                tempDir = tempDir.resolve("WEB-INF/classes/");
            }

            Path newClassPath = tempDir.resolve(Paths.get(newName + ".class"));
            if (!Files.exists(newClassPath)) {
                logger.debug("class not exist: {}", newClassPath.toString());
                continue;
            }
            try {
                ClassReader classReader = new ClassReader(Files.readAllBytes(newClassPath));
                CustomClassLoader loader = new CustomClassLoader();
                // COMPUTE_FRAMES 需要修改 CLASSLOADER 来计算
                ClassWriter classWriter = new CustomClassWriter(classReader,
                        ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS, loader);
                JunkCodeVisitor changer = new JunkCodeVisitor(classWriter, config);
                try {
                    classReader.accept(changer, Const.AnalyzeASMOptions);
                } catch (Exception ignored) {
                    // CustomClassLoader 可能会找不到类
                    // 这个地方目前做法是跳过这个类的花指令混淆
                    logger.debug("花指令混淆使用自定义类加载器出现错误");
                    continue;
                }
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
