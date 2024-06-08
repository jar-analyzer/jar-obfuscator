package me.n1ar4.jar.obfuscator.loader;

import me.n1ar4.log.LogManager;
import me.n1ar4.log.Logger;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;

public class CustomClassWriter extends ClassWriter {
    private static final String defaultRet = "java/lang/Object";
    private static final Logger logger = LogManager.getLogger();
    private final ClassLoader classLoader;

    public CustomClassWriter(ClassReader classReader, int flags, ClassLoader classLoader) {
        super(classReader, flags);
        this.classLoader = classLoader;
    }

    @Override
    protected String getCommonSuperClass(final String type1, final String type2) {
        if (type1 == null || type2 == null) {
            return defaultRet;
        }
        if (type1.trim().isEmpty() || type2.trim().isEmpty()) {
            return defaultRet;
        }
        Class<?> c, d;
        try {
            c = Class.forName(type1.replace('/', '.'), false, classLoader);
            d = Class.forName(type2.replace('/', '.'), false, classLoader);
        } catch (Exception e) {
            logger.debug("junk code obfuscate warn: {}", e.toString());
            return defaultRet;
        }
        if (c.isAssignableFrom(d)) {
            return type1;
        }
        if (d.isAssignableFrom(c)) {
            return type2;
        }
        if (c.isInterface() || d.isInterface()) {
            return defaultRet;
        } else {
            do {
                c = c.getSuperclass();
            } while (!c.isAssignableFrom(d));
            return c.getName().replace('.', '/');
        }
    }
}