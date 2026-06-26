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

package me.n1ar4.jar.obfuscator.utils;

import me.n1ar4.jar.obfuscator.base.ClassField;
import me.n1ar4.jar.obfuscator.base.ClassReference;
import me.n1ar4.jar.obfuscator.base.MethodReference;
import me.n1ar4.jar.obfuscator.core.AnalyzeEnv;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class FrameworkRuleUtil {
    private static final Set<String> CLASS_NAME_KEEP_ANNOTATIONS = setOf(
            "org/springframework/boot/autoconfigure/SpringBootApplication",
            "org/springframework/boot/SpringBootConfiguration",
            "jakarta/persistence/Entity",
            "javax/persistence/Entity",
            "jakarta/persistence/MappedSuperclass",
            "javax/persistence/MappedSuperclass",
            "jakarta/persistence/Embeddable",
            "javax/persistence/Embeddable",
            "jakarta/servlet/annotation/WebServlet",
            "javax/servlet/annotation/WebServlet",
            "jakarta/servlet/annotation/WebFilter",
            "javax/servlet/annotation/WebFilter",
            "jakarta/servlet/annotation/WebListener",
            "javax/servlet/annotation/WebListener",
            "jakarta/ws/rs/Path",
            "javax/ws/rs/Path",
            "org/apache/ibatis/annotations/Mapper"
    );

    private static final Set<String> FRAMEWORK_CLASS_ANNOTATIONS = setOf(
            "org/springframework/stereotype/Component",
            "org/springframework/stereotype/Service",
            "org/springframework/stereotype/Repository",
            "org/springframework/stereotype/Controller",
            "org/springframework/web/bind/annotation/RestController",
            "org/springframework/web/bind/annotation/ControllerAdvice",
            "org/springframework/boot/autoconfigure/SpringBootApplication",
            "org/springframework/boot/SpringBootConfiguration",
            "org/springframework/boot/context/properties/ConfigurationProperties",
            "org/springframework/cloud/openfeign/FeignClient",
            "org/apache/ibatis/annotations/Mapper",
            "jakarta/persistence/Entity",
            "javax/persistence/Entity",
            "jakarta/persistence/MappedSuperclass",
            "javax/persistence/MappedSuperclass",
            "jakarta/persistence/Embeddable",
            "javax/persistence/Embeddable",
            "jakarta/ws/rs/Path",
            "javax/ws/rs/Path"
    );

    private static final Set<String> MAPPER_CLASS_ANNOTATIONS = setOf(
            "org/apache/ibatis/annotations/Mapper",
            "org/springframework/cloud/openfeign/FeignClient"
    );

    private static final Set<String> FIELD_OWNER_KEEP_ANNOTATIONS = setOf(
            "jakarta/persistence/Entity",
            "javax/persistence/Entity",
            "jakarta/persistence/MappedSuperclass",
            "javax/persistence/MappedSuperclass",
            "jakarta/persistence/Embeddable",
            "javax/persistence/Embeddable",
            "org/springframework/boot/context/properties/ConfigurationProperties"
    );

    private static final Set<String> METHOD_KEEP_ANNOTATIONS = setOf(
            "org/springframework/web/bind/annotation/RequestMapping",
            "org/springframework/web/bind/annotation/GetMapping",
            "org/springframework/web/bind/annotation/PostMapping",
            "org/springframework/web/bind/annotation/PutMapping",
            "org/springframework/web/bind/annotation/DeleteMapping",
            "org/springframework/web/bind/annotation/PatchMapping",
            "org/springframework/context/annotation/Bean",
            "org/springframework/context/event/EventListener",
            "org/springframework/scheduling/annotation/Scheduled",
            "org/springframework/scheduling/annotation/Async",
            "org/springframework/cache/annotation/Cacheable",
            "org/springframework/cache/annotation/CacheEvict",
            "org/springframework/cache/annotation/CachePut",
            "org/springframework/transaction/annotation/Transactional",
            "org/apache/ibatis/annotations/Select",
            "org/apache/ibatis/annotations/Insert",
            "org/apache/ibatis/annotations/Update",
            "org/apache/ibatis/annotations/Delete",
            "org/apache/ibatis/annotations/SelectProvider",
            "org/apache/ibatis/annotations/InsertProvider",
            "org/apache/ibatis/annotations/UpdateProvider",
            "org/apache/ibatis/annotations/DeleteProvider",
            "org/apache/ibatis/annotations/Results",
            "org/apache/ibatis/annotations/ResultMap",
            "jakarta/annotation/PostConstruct",
            "javax/annotation/PostConstruct",
            "jakarta/annotation/PreDestroy",
            "javax/annotation/PreDestroy",
            "jakarta/persistence/PrePersist",
            "javax/persistence/PrePersist",
            "jakarta/persistence/PostPersist",
            "javax/persistence/PostPersist",
            "jakarta/persistence/PreUpdate",
            "javax/persistence/PreUpdate",
            "jakarta/persistence/PostUpdate",
            "javax/persistence/PostUpdate",
            "jakarta/persistence/PreRemove",
            "javax/persistence/PreRemove",
            "jakarta/persistence/PostRemove",
            "javax/persistence/PostRemove",
            "jakarta/persistence/PostLoad",
            "javax/persistence/PostLoad",
            "jakarta/ws/rs/GET",
            "javax/ws/rs/GET",
            "jakarta/ws/rs/POST",
            "javax/ws/rs/POST",
            "jakarta/ws/rs/PUT",
            "javax/ws/rs/PUT",
            "jakarta/ws/rs/DELETE",
            "javax/ws/rs/DELETE",
            "jakarta/ws/rs/PATCH",
            "javax/ws/rs/PATCH",
            "jakarta/ws/rs/Path",
            "javax/ws/rs/Path",
            "com/fasterxml/jackson/annotation/JsonCreator",
            "com/fasterxml/jackson/annotation/JsonGetter",
            "com/fasterxml/jackson/annotation/JsonSetter",
            "com/fasterxml/jackson/annotation/JsonProperty",
            "com/fasterxml/jackson/annotation/JsonValue",
            "com/fasterxml/jackson/annotation/JsonAnyGetter",
            "com/fasterxml/jackson/annotation/JsonAnySetter"
    );

    private static final Set<String> FIELD_KEEP_ANNOTATIONS = setOf(
            "org/springframework/beans/factory/annotation/Autowired",
            "org/springframework/beans/factory/annotation/Value",
            "javax/annotation/Resource",
            "jakarta/annotation/Resource",
            "javax/inject/Inject",
            "jakarta/inject/Inject",
            "com/fasterxml/jackson/annotation/JsonProperty",
            "com/fasterxml/jackson/annotation/JsonAlias",
            "com/fasterxml/jackson/annotation/JsonIgnore",
            "com/fasterxml/jackson/annotation/JsonFormat",
            "com/fasterxml/jackson/annotation/JsonInclude",
            "com/fasterxml/jackson/annotation/JsonView",
            "com/fasterxml/jackson/annotation/JsonUnwrapped",
            "com/fasterxml/jackson/annotation/JsonManagedReference",
            "com/fasterxml/jackson/annotation/JsonBackReference",
            "com/fasterxml/jackson/databind/annotation/JsonSerialize",
            "com/fasterxml/jackson/databind/annotation/JsonDeserialize",
            "jakarta/persistence/Id",
            "javax/persistence/Id",
            "jakarta/persistence/EmbeddedId",
            "javax/persistence/EmbeddedId",
            "jakarta/persistence/Column",
            "javax/persistence/Column",
            "jakarta/persistence/JoinColumn",
            "javax/persistence/JoinColumn",
            "jakarta/persistence/JoinColumns",
            "javax/persistence/JoinColumns",
            "jakarta/persistence/OneToOne",
            "javax/persistence/OneToOne",
            "jakarta/persistence/OneToMany",
            "javax/persistence/OneToMany",
            "jakarta/persistence/ManyToOne",
            "javax/persistence/ManyToOne",
            "jakarta/persistence/ManyToMany",
            "javax/persistence/ManyToMany",
            "jakarta/persistence/Transient",
            "javax/persistence/Transient"
    );

    private FrameworkRuleUtil() {
    }

    public static boolean shouldKeepClassName(ClassReference clazz) {
        return hasAnnotation(clazz.getAnnotations(), CLASS_NAME_KEEP_ANNOTATIONS);
    }

    public static boolean shouldKeepMethodName(ClassReference owner, MethodReference method) {
        if (hasAnnotation(method.getAnnotations(), METHOD_KEEP_ANNOTATIONS)) {
            return true;
        }
        if (owner.isInterface() && hasAnnotation(owner.getAnnotations(), MAPPER_CLASS_ANNOTATIONS)) {
            return true;
        }
        return isFrameworkClass(owner) && isBeanAccessor(method);
    }

    public static boolean shouldKeepFieldName(ClassReference owner, String fieldName) {
        Set<String> fieldAnnotations = AnalyzeEnv.fieldAnnotationsMap.get(toClassField(owner.getName(), fieldName));
        if (hasAnnotation(fieldAnnotations, FIELD_KEEP_ANNOTATIONS)) {
            return true;
        }
        return hasAnnotation(owner.getAnnotations(), FIELD_OWNER_KEEP_ANNOTATIONS);
    }

    private static boolean isFrameworkClass(ClassReference clazz) {
        return hasAnnotation(clazz.getAnnotations(), FRAMEWORK_CLASS_ANNOTATIONS);
    }

    private static boolean isBeanAccessor(MethodReference method) {
        int access = method.getAccess();
        if ((access & Opcodes.ACC_STATIC) != 0 || (access & Opcodes.ACC_PRIVATE) != 0) {
            return false;
        }
        if ((access & (Opcodes.ACC_PUBLIC | Opcodes.ACC_PROTECTED)) == 0) {
            return false;
        }

        String name = method.getName();
        String desc = method.getDesc();
        Type[] args = Type.getArgumentTypes(desc);
        Type returnType = Type.getReturnType(desc);
        if (name.startsWith("get") && name.length() > 3 && args.length == 0 && returnType.getSort() != Type.VOID) {
            return true;
        }
        if (name.startsWith("is") && name.length() > 2 && args.length == 0 && returnType.getSort() == Type.BOOLEAN) {
            return true;
        }
        return name.startsWith("set") && name.length() > 3 && args.length == 1 && returnType.getSort() == Type.VOID;
    }

    private static boolean hasAnnotation(Collection<String> annotations, Set<String> expected) {
        if (annotations == null || annotations.isEmpty()) {
            return false;
        }
        for (String annotation : annotations) {
            if (expected.contains(normalizeAnnotation(annotation))) {
                return true;
            }
        }
        return false;
    }

    private static String normalizeAnnotation(String annotation) {
        if (annotation == null) {
            return "";
        }
        String normalized = annotation;
        if (normalized.startsWith("L") && normalized.endsWith(";")) {
            normalized = normalized.substring(1, normalized.length() - 1);
        }
        return normalized.replace('.', '/');
    }

    private static ClassField toClassField(String className, String fieldName) {
        ClassField field = new ClassField();
        field.setClassName(className);
        field.setFieldName(fieldName);
        return field;
    }

    private static Set<String> setOf(String... values) {
        return new HashSet<>(Arrays.asList(values));
    }
}
