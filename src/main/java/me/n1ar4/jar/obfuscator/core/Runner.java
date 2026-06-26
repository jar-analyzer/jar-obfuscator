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

package me.n1ar4.jar.obfuscator.core;

import me.n1ar4.jar.obfuscator.Const;
import me.n1ar4.jar.obfuscator.base.ClassField;
import me.n1ar4.jar.obfuscator.base.ClassFileEntity;
import me.n1ar4.jar.obfuscator.base.ClassReference;
import me.n1ar4.jar.obfuscator.base.MethodReference;
import me.n1ar4.jar.obfuscator.config.BaseConfig;
import me.n1ar4.jar.obfuscator.loader.CustomClassLoader;
import me.n1ar4.jar.obfuscator.templates.StringDecrypt;
import me.n1ar4.jar.obfuscator.templates.StringDecryptDump;
import me.n1ar4.jar.obfuscator.transform.*;
import me.n1ar4.jar.obfuscator.utils.*;
import me.n1ar4.log.LogManager;
import me.n1ar4.log.Logger;
import org.objectweb.asm.Type;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public class Runner {
    private static final Logger logger = LogManager.getLogger();
    private static final List<String> FRAMEWORK_INTERFACE_PREFIXES = Arrays.asList(
            "org/springframework/data/repository/",
            "org/springframework/data/jpa/repository/",
            "org/springframework/data/mongodb/repository/",
            "org/springframework/data/redis/repository/"
    );
    private static String jarName;

    private static void addClass(Path path) {
        if (ObfEnv.config.isUseSpringBoot()) {
            if (!path.toString().contains("BOOT-INF")) {
                return;
            }
        }
        if (ObfEnv.config.isUseWebWar()) {
            if (!path.toString().contains("WEB-INF")) {
                return;
            }
        }
        ClassFileEntity cf = new ClassFileEntity();
        cf.setPath(path);
        cf.setJarName(jarName);
        AnalyzeEnv.classFileList.add(cf);
    }

    private static String getPackageName(String className) {
        int index = className.lastIndexOf('/');
        if (index < 0) {
            return "";
        }
        return className.substring(0, index);
    }

    private static String getSimpleName(String className) {
        int index = className.lastIndexOf('/');
        if (index < 0) {
            return className;
        }
        return className.substring(index + 1);
    }

    private static String getMappedPackageName(String packageName, Map<String, String> packageNameMap) {
        if (!ObfEnv.config.isEnablePackageName()) {
            return packageName;
        }
        return packageNameMap.computeIfAbsent(packageName, name -> {
            if (name.isEmpty()) {
                return "";
            }
            String[] parts = name.split("/");
            StringBuilder builder = new StringBuilder();
            for (int i = 0; i < parts.length; i++) {
                if (i > 0) {
                    builder.append("/");
                }
                builder.append(NameUtil.genPackage());
            }
            return builder.toString();
        });
    }

    private static String buildClassNameMapping(String originalName, Map<String, String> packageNameMap) {
        String exist = ObfEnv.classNameObfMapping.get(originalName);
        if (exist != null) {
            return exist;
        }
        if (PackageUtil.inBlackClass(originalName, ObfEnv.config)) {
            ObfEnv.classNameObfMapping.put(originalName, originalName);
            return originalName;
        }

        boolean isEnablePackageName = ObfEnv.config.isEnablePackageName();
        boolean isEnableClassName = ObfEnv.config.isEnableClassName();
        if (!isEnablePackageName && !isEnableClassName) {
            ObfEnv.classNameObfMapping.put(originalName, originalName);
            return originalName;
        }

        String packageName = getPackageName(originalName);
        String finalPackageName = getMappedPackageName(packageName, packageNameMap);
        String simpleName = getSimpleName(originalName);
        String finalName;

        if (isEnableClassName) {
            int innerIndex = originalName.lastIndexOf('$');
            int packageIndex = originalName.lastIndexOf('/');
            if (innerIndex > packageIndex) {
                String parentName = originalName.substring(0, innerIndex);
                finalName = buildClassNameMapping(parentName, packageNameMap) + "$" + NameUtil.genNewName();
            } else if (finalPackageName.isEmpty()) {
                finalName = NameUtil.genNewName();
            } else {
                finalName = finalPackageName + "/" + NameUtil.genNewName();
            }
        } else if (finalPackageName.isEmpty()) {
            finalName = simpleName;
        } else {
            finalName = finalPackageName + "/" + simpleName;
        }

        ObfEnv.classNameObfMapping.put(originalName, finalName);
        return finalName;
    }

    private static boolean hasMethod(String className, String methodName, String desc) {
        List<MethodReference> methods = AnalyzeEnv.methodsInClassMap.get(new ClassReference.Handle(className));
        if (methods == null) {
            return false;
        }
        for (MethodReference method : methods) {
            if (method.getName().equals(methodName) && method.getDesc().equals(desc)) {
                return true;
            }
        }
        return false;
    }

    private static String findMethodGroupOwner(String className, String methodName, String desc) {
        ClassReference clazz = AnalyzeEnv.classMap.get(new ClassReference.Handle(className));
        if (clazz == null) {
            return className;
        }

        String superClass = clazz.getSuperClass();
        if (superClass != null && hasMethod(superClass, methodName, desc)) {
            return findMethodGroupOwner(superClass, methodName, desc);
        }

        List<String> interfaces = new ArrayList<>(clazz.getInterfaces());
        Collections.sort(interfaces);
        for (String interfaceName : interfaces) {
            if (hasMethod(interfaceName, methodName, desc)) {
                return findMethodGroupOwner(interfaceName, methodName, desc);
            }
        }
        return className;
    }

    private static boolean shouldSkipMethod(MethodReference mr) {
        String methodName = mr.getName();
        return methodName.startsWith("lambda$") ||
                methodName.startsWith("access$") ||
                methodName.equals("<init>") ||
                methodName.equals("<clinit>") ||
                isSerializationHook(mr) ||
                ("main".equals(methodName) && "([Ljava/lang/String;)V".equals(mr.getDesc()) &&
                        (mr.getAccess() & Modifier.PUBLIC) != 0 &&
                        (mr.getAccess() & Modifier.STATIC) != 0);
    }

    private static boolean isSerializationHook(MethodReference method) {
        String name = method.getName();
        String desc = method.getDesc();
        return ("writeObject".equals(name) && "(Ljava/io/ObjectOutputStream;)V".equals(desc)) ||
                ("readObject".equals(name) && "(Ljava/io/ObjectInputStream;)V".equals(desc)) ||
                ("readObjectNoData".equals(name) && "()V".equals(desc)) ||
                ("writeReplace".equals(name) && "()Ljava/lang/Object;".equals(desc)) ||
                ("readResolve".equals(name) && "()Ljava/lang/Object;".equals(desc));
    }

    private static boolean extendsFrameworkInterface(ClassReference owner) {
        if (!owner.isInterface()) {
            return false;
        }
        List<String> interfaces = owner.getInterfaces();
        if (interfaces == null) {
            return false;
        }
        Set<String> visited = new HashSet<>();
        for (String interfaceName : interfaces) {
            if (isFrameworkInterface(interfaceName, visited)) {
                return true;
            }
        }
        return false;
    }

    private static boolean isFrameworkInterface(String className, Set<String> visited) {
        if (className == null || !visited.add(className)) {
            return false;
        }
        for (String prefix : FRAMEWORK_INTERFACE_PREFIXES) {
            if (className.startsWith(prefix)) {
                return true;
            }
        }
        ClassReference internalClass = AnalyzeEnv.classMap.get(new ClassReference.Handle(className));
        if (internalClass == null) {
            return false;
        }
        List<String> interfaces = internalClass.getInterfaces();
        if (interfaces == null) {
            return false;
        }
        for (String interfaceName : interfaces) {
            if (isFrameworkInterface(interfaceName, visited)) {
                return true;
            }
        }
        return false;
    }

    private static boolean overridesExternalApi(CustomClassLoader loader,
                                                ClassReference owner,
                                                MethodReference method) {
        int access = method.getAccess();
        if ((access & (Modifier.PRIVATE | Modifier.STATIC)) != 0) {
            return false;
        }
        Set<String> visited = new HashSet<>();
        if (methodExistsInExternalHierarchy(loader, owner.getSuperClass(),
                method.getName(), method.getDesc(), visited)) {
            return true;
        }
        List<String> interfaces = owner.getInterfaces();
        if (interfaces == null) {
            return false;
        }
        for (String interfaceName : interfaces) {
            if (methodExistsInExternalHierarchy(loader, interfaceName,
                    method.getName(), method.getDesc(), visited)) {
                return true;
            }
        }
        return false;
    }

    private static boolean methodExistsInExternalHierarchy(CustomClassLoader loader,
                                                           String className,
                                                           String methodName,
                                                           String desc,
                                                           Set<String> visited) {
        if (className == null || !visited.add(className)) {
            return false;
        }

        ClassReference internalClass = AnalyzeEnv.classMap.get(new ClassReference.Handle(className));
        if (internalClass != null) {
            if (methodExistsInExternalHierarchy(loader, internalClass.getSuperClass(), methodName, desc, visited)) {
                return true;
            }
            List<String> interfaces = internalClass.getInterfaces();
            if (interfaces == null) {
                return false;
            }
            for (String interfaceName : interfaces) {
                if (methodExistsInExternalHierarchy(loader, interfaceName, methodName, desc, visited)) {
                    return true;
                }
            }
            return false;
        }

        return methodExistsInLoadedHierarchy(loader, className, methodName, desc, new HashSet<Class<?>>());
    }

    private static boolean methodExistsInLoadedHierarchy(CustomClassLoader loader,
                                                         String className,
                                                         String methodName,
                                                         String desc,
                                                         Set<Class<?>> visited) {
        try {
            Class<?> clazz = Class.forName(className.replace('/', '.'), false, loader);
            return methodExistsInLoadedHierarchy(clazz, methodName, desc, visited);
        } catch (Throwable ignored) {
            return false;
        }
    }

    private static boolean methodExistsInLoadedHierarchy(Class<?> clazz,
                                                         String methodName,
                                                         String desc,
                                                         Set<Class<?>> visited) {
        if (clazz == null || !visited.add(clazz)) {
            return false;
        }
        for (Method method : clazz.getDeclaredMethods()) {
            int modifiers = method.getModifiers();
            if ((modifiers & (Modifier.PRIVATE | Modifier.STATIC)) != 0) {
                continue;
            }
            if (method.getName().equals(methodName) && Type.getMethodDescriptor(method).equals(desc)) {
                return true;
            }
        }
        if (methodExistsInLoadedHierarchy(clazz.getSuperclass(), methodName, desc, visited)) {
            return true;
        }
        for (Class<?> interfaceClass : clazz.getInterfaces()) {
            if (methodExistsInLoadedHierarchy(interfaceClass, methodName, desc, visited)) {
                return true;
            }
        }
        return false;
    }

    public static void run(Path path, BaseConfig config) {
        AnalyzeEnv.reset();
        PackageUtil.reset();
        ObfEnv.reset(config);
        logger.info("start obfuscator");

        if (config.isUseSpringBoot() && config.isUseWebWar()) {
            logger.error("注意 useSpringBoot 和 useWebWar 只能开启一项");
            return;
        }

        String fileName = FileUtil.getFileNameWithoutExt(path);
        jarName = fileName + ".jar";
        String newFile = fileName + "_obf.jar";

        try {
            DirUtil.deleteDirectory(new File(Const.TEMP_DIR));
            DirUtil.unzip(path.toAbsolutePath().toString(), Const.TEMP_DIR);
            logger.info("unzip jar file success");
        } catch (IOException e) {
            logger.error("run error: {}", e.toString());
        }

        // 2025/06/23 处理某些情况下找不到依赖的问题
        Path dirPath = Paths.get(CustomClassLoader.LIB_DIR);
        try {
            Files.createDirectory(dirPath);
            logger.info("已成功创建 {} 目录", CustomClassLoader.LIB_DIR);
            Files.write(dirPath.resolve(Paths.get("README.md")), ("# README\n" +
                    "\n" +
                    "一些情况下混淆报错可能需要依赖库\n" +
                    "\n" +
                    "请将依赖放在 `jar-obf-lib` 目录中").getBytes(StandardCharsets.UTF_8));
        } catch (Exception ignored) {
            logger.warn("无法创建 {} 目录", CustomClassLoader.LIB_DIR);
        }

        Path tmpDir = Paths.get(Const.TEMP_DIR);
        try (Stream<Path> stream = Files.walk(tmpDir)) {
            stream.filter(Files::isRegularFile)
                    .filter(pa -> pa.toString().endsWith(".class"))
                    .forEach(Runner::addClass);
        } catch (IOException e) {
            logger.error("error reading the directory: " + e.getMessage());
        }

        DiscoveryRunner.start(AnalyzeEnv.classFileList, AnalyzeEnv.discoveredClasses,
                AnalyzeEnv.discoveredMethods, AnalyzeEnv.classMap, AnalyzeEnv.methodMap,
                AnalyzeEnv.fieldsInClassMap, AnalyzeEnv.fieldAnnotationsMap);
        logger.info("all classes: {}", AnalyzeEnv.discoveredClasses.size());
        logger.info("all methods: {}", AnalyzeEnv.discoveredMethods.size());
        for (MethodReference mr : AnalyzeEnv.discoveredMethods) {
            ClassReference.Handle ch = mr.getClassReference();
            if (AnalyzeEnv.methodsInClassMap.get(ch) == null) {
                List<MethodReference> ml = new ArrayList<>();
                ml.add(mr);
                AnalyzeEnv.methodsInClassMap.put(ch, ml);
            } else {
                List<MethodReference> ml = AnalyzeEnv.methodsInClassMap.get(ch);
                ml.add(mr);
                AnalyzeEnv.methodsInClassMap.put(ch, ml);
            }
        }
        logger.info("build methods in class map finish");
        MethodCallRunner.start(AnalyzeEnv.classFileList, AnalyzeEnv.methodCalls);
        logger.info("method calls: {}", AnalyzeEnv.methodCalls.size());

        PackageUtil.buildInternalBlackList();

        Map<String, String> packageNameMap = new HashMap<>();
        CustomClassLoader loader = new CustomClassLoader(path.toAbsolutePath());

        // 处理 class name
        for (ClassReference c : AnalyzeEnv.discoveredClasses) {
            // 枚举类的类名/字段名/方法名本身不混淆，但不能简单跳过：
            // 枚举类的方法体（如 <clinit>、构造方法）可能引用其他被混淆类的字段/方法，
            // 需要让它以 identity 映射进入 classNameObfMapping，使各 transformer 仍会处理
            // 它的方法体中的引用。枚举自身的成员名因构建映射时跳过枚举而保持不变。
            if (c.getName().contains("module-info")) {
                continue;
            }
            String originalName = c.getName();

            if (c.isEnum()) {
                ObfEnv.classNameObfMapping.put(originalName, originalName);
                continue;
            }

            boolean inBlackClass = PackageUtil.inBlackClass(originalName, config) ||
                    FrameworkRuleUtil.shouldKeepClassName(c);
            if (!inBlackClass) {
                buildClassNameMapping(originalName, packageNameMap);
            } else {
                // 如果是黑名单类 也需要记录
                ObfEnv.classNameObfMapping.put(originalName, originalName);
            }
        }

        // 处理 method name
        for (Map.Entry<ClassReference.Handle, List<MethodReference>> entry : AnalyzeEnv.methodsInClassMap.entrySet()) {
            ClassReference.Handle key = entry.getKey();
            List<MethodReference> value = entry.getValue();

            ClassReference classReference = AnalyzeEnv.classMap.get(key);
            if (classReference == null || classReference.isEnum()) {
                continue;
            }

            // 黑名单类的方法名不混淆：黑名单类（如第三方库）的成员应整体保持不变，
            // 否则其方法定义被改名后，其他类对其方法的引用无法被一致更新会导致 NoSuchMethodError
            if (PackageUtil.inBlackClass(key.getName(), config)) {
                continue;
            }

            String newClassName = ObfEnv.classNameObfMapping.getOrDefault(key.getName(), key.getName());
            boolean keepFrameworkInterfaceMethods = extendsFrameworkInterface(classReference);

            for (MethodReference mr : value) {
                String originalDesc = mr.getDesc();
                String oldMethodName = mr.getName();
                if (shouldSkipMethod(mr) ||
                        FrameworkRuleUtil.shouldKeepMethodName(classReference, mr) ||
                        keepFrameworkInterfaceMethods ||
                        overridesExternalApi(loader, classReference, mr)) {
                    continue;
                }
                String desc = BytecodeRemapUtil.remapDesc(originalDesc);
                String groupOwner = findMethodGroupOwner(key.getName(), oldMethodName, originalDesc);
                String newGroupOwner = ObfEnv.classNameObfMapping.getOrDefault(groupOwner, groupOwner);
                MethodReference.Handle groupHandle = new MethodReference.Handle(
                        new ClassReference.Handle(newGroupOwner), oldMethodName, desc);
                MethodReference.Handle existGroup = ObfEnv.methodNameObfMapping.get(groupHandle);

                MethodReference.Handle oldHandle = new MethodReference.Handle(
                        new ClassReference.Handle(newClassName),
                        oldMethodName, desc);

                String newMethodName = existGroup == null ? NameUtil.genNewMethod() : existGroup.getName();
                MethodReference.Handle newHandle = new MethodReference.Handle(
                        new ClassReference.Handle(newClassName),
                        newMethodName, desc);
                if (existGroup == null) {
                    ObfEnv.methodNameObfMapping.put(groupHandle, new MethodReference.Handle(
                            new ClassReference.Handle(newGroupOwner), newMethodName, desc));
                }
                ObfEnv.methodNameObfMapping.put(oldHandle, newHandle);
            }
        }

        // 处理 method mapping 中的 black method 问题
        Map<MethodReference.Handle, MethodReference.Handle>
                methodNameObfMapping = new HashMap<>(ObfEnv.methodNameObfMapping);
        for (Map.Entry<MethodReference.Handle, MethodReference.Handle> en : ObfEnv.methodNameObfMapping.entrySet()) {
            String oldClassName = en.getKey().getName();
            String originalClassName = oldClassName;
            for (Map.Entry<String, String> mapping : ObfEnv.classNameObfMapping.entrySet()) {
                if (mapping.getValue().equals(oldClassName)) {
                    originalClassName = mapping.getKey();
                    break;
                }
            }
            List<String> methodBlackList = ObfEnv.config.getMethodBlackList();
            if (methodBlackList == null) {
                continue;
            }
            for (String s : methodBlackList) {
                String normalized = s.replace(".", "/");
                if (normalized.equals(oldClassName) || normalized.equals(originalClassName)) {
                    methodNameObfMapping.remove(en.getKey());
                    methodNameObfMapping.put(en.getKey(), en.getKey());
                    break;
                }
                Pattern pattern = Pattern.compile(normalized, Pattern.DOTALL);
                Matcher newMatcher = pattern.matcher(oldClassName);
                Matcher oldMatcher = pattern.matcher(originalClassName);
                if (newMatcher.matches() || oldMatcher.matches()) {
                    methodNameObfMapping.remove(en.getKey());
                    methodNameObfMapping.put(en.getKey(), en.getKey());
                    break;
                }
            }
        }
        ObfEnv.methodNameObfMapping.clear();
        ObfEnv.methodNameObfMapping.putAll(methodNameObfMapping);
        methodNameObfMapping.clear();

        // 处理 field name
        for (ClassReference c : AnalyzeEnv.discoveredClasses) {

            if (c.isEnum()) {
                continue;
            }

            // 黑名单类的字段名不混淆：与方法名同理，黑名单类成员整体保持不变
            if (PackageUtil.inBlackClass(c.getName(), config)) {
                continue;
            }

            String newClassName = ObfEnv.classNameObfMapping.getOrDefault(c.getName(), c.getName());

            List<String> fields = AnalyzeEnv.fieldsInClassMap.get(c.getName());
            if (fields == null) {
                continue;
            }
            for (String s : fields) {
                if (FrameworkRuleUtil.shouldKeepFieldName(c, s)) {
                    continue;
                }
                ClassField oldMember = new ClassField();
                oldMember.setClassName(newClassName);
                oldMember.setFieldName(s);

                ClassField newMember = new ClassField();
                newMember.setClassName(newClassName);
                newMember.setFieldName(NameUtil.genNewFields());
                ObfEnv.fieldNameObfMapping.put(oldMember, newMember);
            }
        }

        if (config.isShowAllMainMethods()) {
            // 向用户提示可能的主类
            MainClassTransformer.transform();
        }

        if (config.isEnableDeleteCompileInfo()) {
            // 删除编译信息
            DeleteInfoTransformer.transform(loader);
        }

        if (config.isEnablePackageName() || config.isEnableClassName()) {
            // 包名或类名重命名
            ClassNameTransformer.transform(loader);
        }

        if (config.isEnableMethodName()) {
            // 方法名重命名
            MethodNameTransformer.transform(loader);
        }

        if (config.isEnableFieldName()) {
            // 属性重命名
            FieldNameTransformer.transform(loader);
        }

        if (config.isEnableParamName()) {
            // 方法内参数混淆
            ParameterTransformer.transform(loader);
        }

        if (config.isEnableXOR()) {
            // 异或混淆常数
            XORTransformer.transform(loader);
        }

        if (config.isEnableEncryptString()) {
            // 创建加密解密类
            byte[] code = StringDecryptDump.dump();
            String name = StringDecryptDump.className;
            String[] parts = name.split("/");
            Path dir = tmpDir;
            for (int i = 0; i < parts.length - 1; i++) {
                dir = dir.resolve(parts[i]);
            }
            try {
                Files.createDirectories(dir);
            } catch (Exception ignored) {
            }
            try {
                TransformerUtil.writeAtomically(dir.resolve(parts[parts.length - 1] + ".class"), code);
            } catch (Exception ex) {
                throw new IllegalStateException("write string decrypt class failed", ex);
            }

            // 字符串加密和解密
            StringTransformer.transform(loader);

            if (config.isEnableAdvanceString()) {
                // 字符串提取处理
                for (Map.Entry<String, String> entry : ObfEnv.classNameObfMapping.entrySet()) {
                    ArrayList<String> t = ObfEnv.stringInClass.get(new ClassReference.Handle(entry.getKey()));
                    if (t == null) {
                        continue;
                    }
                    ArrayList<String> newRes = new ArrayList<>();
                    for (String s : t) {
                        String encrypted = StringDecrypt.encrypt(s);
                        if (BytecodeStringUtil.canStoreAsConstantUtf8(encrypted)) {
                            newRes.add(encrypted);
                        } else {
                            newRes.add(s);
                        }
                    }
                    ObfEnv.newStringInClass.put(entry.getValue(), newRes);
                }

                // 字符串提取
                StringArrayTransformer.transform(loader);

                if (config.isEnableXOR()) {
                    // 提取后再次异或处理
                    XORTransformer.transform(loader);
                }
            }
        }

        if (config.isEnableJunk()) {
            // 花指令混淆
            JunkCodeTransformer.transform(config, loader);
        }

        // 生成混淆后目标
        if (config.isEnablePackageName() || config.isEnableClassName()) {
            ResourceTransformer.transform();
        }

        try {
            DirUtil.zip(Const.TEMP_DIR, newFile);
            if (!config.isKeepTempFile()) {
                DirUtil.deleteDirectory(new File(Const.TEMP_DIR));
            }
            logger.info("generate jar file: {}", newFile);
        } catch (Exception e) {
            logger.error("zip file error: {}", e.toString());
            throw new IllegalStateException("zip output jar failed", e);
        }
    }
}
