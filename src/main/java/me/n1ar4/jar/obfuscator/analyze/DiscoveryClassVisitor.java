/*
 * MIT License
 *
 * Project URL: https://github.com/jar-analyzer/jar-obfuscator
 *
 * Copyright (c) 2024-2026 4ra1n (https://github.com/4ra1n)
 *
 * This project is distributed under the MIT license.
 *
 * https://opensource.org/license/mit
 */

package me.n1ar4.jar.obfuscator.analyze;

import me.n1ar4.jar.obfuscator.Const;
import me.n1ar4.jar.obfuscator.base.ClassField;
import me.n1ar4.jar.obfuscator.base.ClassReference;
import me.n1ar4.jar.obfuscator.base.MethodReference;
import org.objectweb.asm.*;

import java.util.*;

public class DiscoveryClassVisitor extends ClassVisitor {
    private String name;
    private String superName;
    private String[] interfaces;
    private boolean isInterface;
    private boolean isEnum;
    private List<ClassReference.Member> members;
    private ClassReference.Handle classHandle;
    private Set<String> annotations;
    private final Set<ClassReference> discoveredClasses;
    private final Set<MethodReference> discoveredMethods;
    private final Map<String, List<String>> fieldsInClassMap;
    private final Map<ClassField, Set<String>> fieldAnnotationsMap;
    private final List<String> fieldsList = new ArrayList<>();
    private final String jar;

    public DiscoveryClassVisitor(Set<ClassReference> discoveredClasses,
                                 Set<MethodReference> discoveredMethods,
                                 Map<String, List<String>> fieldsInClassMap,
                                 Map<ClassField, Set<String>> fieldAnnotationsMap,
                                 String jarName) {
        super(Const.ASMVersion);
        this.fieldsInClassMap = fieldsInClassMap;
        this.fieldAnnotationsMap = fieldAnnotationsMap;
        this.discoveredClasses = discoveredClasses;
        this.discoveredMethods = discoveredMethods;
        this.jar = jarName;
    }

    @Override
    public void visit(int version, int access, String name,
                      String signature, String superName, String[] interfaces) {
        this.name = name;
        this.superName = superName;
        this.interfaces = interfaces;
        this.isInterface = (access & Opcodes.ACC_INTERFACE) != 0;
        this.isEnum = (access & Opcodes.ACC_ENUM) != 0;
        this.members = new ArrayList<>();
        this.classHandle = new ClassReference.Handle(name);
        annotations = new HashSet<>();
        super.visit(version, access, name, signature, superName, interfaces);
    }

    @Override
    public AnnotationVisitor visitAnnotation(String descriptor, boolean visible) {
        annotations.add(descriptor);
        return super.visitAnnotation(descriptor, visible);
    }

    public FieldVisitor visitField(int access, String name, String desc,
                                   String signature, Object value) {
        if ((access & Opcodes.ACC_STATIC) == 0) {
            Type type = Type.getType(desc);
            String typeName;
            if (type.getSort() == Type.OBJECT || type.getSort() == Type.ARRAY) {
                typeName = type.getInternalName();
            } else {
                typeName = type.getDescriptor();
            }
            members.add(new ClassReference.Member(name, access, new ClassReference.Handle(typeName)));
        }
        fieldsList.add(name);
        ClassField field = new ClassField();
        field.setClassName(this.name);
        field.setFieldName(name);
        Set<String> fieldAnnotations = new HashSet<>();
        fieldAnnotationsMap.put(field, fieldAnnotations);

        FieldVisitor fv = super.visitField(access, name, desc, signature, value);
        return new FieldVisitor(Const.ASMVersion, fv) {
            @Override
            public AnnotationVisitor visitAnnotation(String descriptor, boolean visible) {
                fieldAnnotations.add(descriptor);
                return super.visitAnnotation(descriptor, visible);
            }

            @Override
            public AnnotationVisitor visitTypeAnnotation(int typeRef, TypePath typePath,
                                                         String descriptor, boolean visible) {
                fieldAnnotations.add(descriptor);
                return super.visitTypeAnnotation(typeRef, typePath, descriptor, visible);
            }
        };
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String desc,
                                     String signature, String[] exceptions) {
        boolean isStatic = (access & Opcodes.ACC_STATIC) != 0;
        Set<String> mAnno = new HashSet<>();
        discoveredMethods.add(new MethodReference(
                classHandle,
                name,
                desc,
                isStatic,
                mAnno, access));
        MethodVisitor mv = super.visitMethod(access, name, desc, signature, exceptions);
        return new DiscoveryMethodAdapter(Const.ASMVersion, mv, mAnno, this.name);
    }

    @Override
    public void visitEnd() {
        ClassReference classReference = new ClassReference(
                name,
                superName,
                Arrays.asList(interfaces),
                isInterface,
                isEnum,
                members,
                annotations,
                jar);
        discoveredClasses.add(classReference);
        fieldsInClassMap.put(name, fieldsList);
        super.visitEnd();
    }
}
