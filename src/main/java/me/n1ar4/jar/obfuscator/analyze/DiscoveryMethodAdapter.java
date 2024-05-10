package me.n1ar4.jar.obfuscator.analyze;

import me.n1ar4.jar.obfuscator.base.ClassReference;
import me.n1ar4.jar.obfuscator.core.ObfEnv;
import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.MethodVisitor;

import java.util.ArrayList;
import java.util.Set;

public class DiscoveryMethodAdapter extends MethodVisitor {
    private final Set<String> anno;
    private final String className;
    private final ArrayList<String> strings = new ArrayList<>();

    protected DiscoveryMethodAdapter(int api, MethodVisitor methodVisitor,
                                     Set<String> anno, String className) {
        super(api, methodVisitor);
        this.anno = anno;
        this.className = className;
    }

    @Override
    public void visitLdcInsn(Object value) {
        if (value instanceof String) {
            String valS = (String) value;
            strings.add(valS);
        }
        super.visitLdcInsn(value);
    }

    @Override
    public AnnotationVisitor visitAnnotation(String descriptor, boolean visible) {
        anno.add(descriptor);
        return super.visitAnnotation(descriptor, visible);
    }

    @Override
    public AnnotationVisitor visitParameterAnnotation(int parameter, String descriptor, boolean visible) {
        anno.add(descriptor);
        return super.visitParameterAnnotation(parameter, descriptor, visible);
    }

    @Override
    public void visitEnd() {
        ClassReference.Handle key = new ClassReference.Handle(this.className);
        ArrayList<String> list = ObfEnv.stringInClass.get(key);
        if (list == null) {
            ObfEnv.stringInClass.put(new ClassReference.Handle(this.className), this.strings);
        } else {
            list.addAll(this.strings);
            ObfEnv.stringInClass.put(new ClassReference.Handle(this.className), list);
        }
        super.visitEnd();
    }
}
