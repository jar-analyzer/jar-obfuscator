package me.n1ar4.jar.obfuscator.core;

import me.n1ar4.jar.obfuscator.Const;
import me.n1ar4.jar.obfuscator.analyze.DiscoveryClassVisitor;
import me.n1ar4.jar.obfuscator.base.ClassFileEntity;
import me.n1ar4.jar.obfuscator.base.ClassReference;
import me.n1ar4.jar.obfuscator.base.MethodReference;
import me.n1ar4.log.LogManager;
import me.n1ar4.log.Logger;
import org.objectweb.asm.ClassReader;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class DiscoveryRunner {
    private static final Logger logger = LogManager.getLogger();

    public static void start(Set<ClassFileEntity> classFileList,
                             Set<ClassReference> discoveredClasses,
                             Set<MethodReference> discoveredMethods,
                             Map<ClassReference.Handle, ClassReference> classMap,
                             Map<MethodReference.Handle, MethodReference> methodMap,
                             Map<String, List<String>> fieldsInClassMap) {
        logger.info("start class analyze");
        for (ClassFileEntity file : classFileList) {
            try {
                DiscoveryClassVisitor dcv = new DiscoveryClassVisitor(discoveredClasses,
                        discoveredMethods, fieldsInClassMap, file.getJarName());
                ClassReader cr = new ClassReader(file.getFile());
                cr.accept(dcv, Const.ReaderASMOptions);
            } catch (Exception e) {
                logger.error("discovery error: {}", e.toString());
            }
        }
        for (ClassReference clazz : discoveredClasses) {
            classMap.put(clazz.getHandle(), clazz);
        }
        for (MethodReference method : discoveredMethods) {
            methodMap.put(method.getHandle(), method);
        }
    }
}
