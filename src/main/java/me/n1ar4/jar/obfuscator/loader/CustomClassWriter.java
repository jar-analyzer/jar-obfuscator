package me.n1ar4.jar.obfuscator.loader;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.ClassReader;

public class CustomClassWriter extends ClassWriter {

    private final ClassLoader classLoader;

    public CustomClassWriter(ClassReader classReader, int flags, ClassLoader classLoader) {
        super(classReader, flags);
        this.classLoader = classLoader;
    }

    @Override
    protected String getCommonSuperClass(final String type1, final String type2) {
        Class<?> c, d;
        try {
            c = Class.forName(type1.replace('/', '.'), false, classLoader);
            d = Class.forName(type2.replace('/', '.'), false, classLoader);
        } catch (Exception e) {
            throw new RuntimeException(e.toString());
        }
        if (c.isAssignableFrom(d)) {
            return type1;
        }
        if (d.isAssignableFrom(c)) {
            return type2;
        }
        if (c.isInterface() || d.isInterface()) {
            return "java/lang/Object";
        } else {
            do {
                c = c.getSuperclass();
            } while (!c.isAssignableFrom(d));
            return c.getName().replace('.', '/');
        }
    }
}