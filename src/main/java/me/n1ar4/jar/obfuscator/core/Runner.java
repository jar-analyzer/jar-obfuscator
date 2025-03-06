package me.n1ar4.jar.obfuscator.core;

import me.n1ar4.jar.obfuscator.Const;
import me.n1ar4.jar.obfuscator.base.ClassField;
import me.n1ar4.jar.obfuscator.base.ClassFileEntity;
import me.n1ar4.jar.obfuscator.base.ClassReference;
import me.n1ar4.jar.obfuscator.base.MethodReference;
import me.n1ar4.jar.obfuscator.config.BaseConfig;
import me.n1ar4.jar.obfuscator.templates.StringDecrypt;
import me.n1ar4.jar.obfuscator.templates.StringDecryptDump;
import me.n1ar4.jar.obfuscator.transform.*;
import me.n1ar4.jar.obfuscator.utils.*;
import me.n1ar4.log.LogManager;
import me.n1ar4.log.Logger;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public class Runner {
    private static final Logger logger = LogManager.getLogger();
    private static String jarName;

    private static void addClass(Path path) {
        ClassFileEntity cf = new ClassFileEntity();
        cf.setPath(path);
        cf.setJarName(jarName);
        AnalyzeEnv.classFileList.add(cf);
    }

    public static void run(Path path, BaseConfig config) {
        ObfEnv.config = config;
        logger.info("start obfuscator");
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
                AnalyzeEnv.fieldsInClassMap);
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

        // 处理 class name
        for (ClassReference c : AnalyzeEnv.discoveredClasses) {
            if (c.isEnum()) {
                continue;
            }
            // 不处理这个 CLASS
            // 常见于高版本的 JAR 中
            if (c.getName().contains("module-info")) {
                continue;
            }
            String[] parts = c.getName().split("/");
            String className = parts[parts.length - 1];
            StringBuilder packageName = new StringBuilder();
            StringBuilder newPackageName = new StringBuilder();
            for (int i = 0; i < parts.length - 1; i++) {
                if (i > 0) {
                    packageName.append("/");
                    newPackageName.append("/");
                }
                packageName.append(parts[i]);
                newPackageName.append(NameUtil.genPackage());
            }
            String packageNameS = packageName.toString();

            String newPackageNameS;
            if (config.isEnablePackageName()) {
                String an = packageNameMap.get(packageNameS);
                newPackageNameS = newPackageName.toString();
                if (an == null) {
                    packageNameMap.put(packageNameS, newPackageNameS);
                    newPackageNameS = newPackageName.toString();
                } else {
                    newPackageNameS = an;
                }
            } else {
                newPackageNameS = packageNameS;
            }

            String originalName = c.getName();
            String finalName;

            boolean inBlackClass = PackageUtil.inBlackClass(originalName, config);
            if (!inBlackClass) {
                String result = ObfEnv.classNameObfMapping.putIfAbsent(originalName, originalName);
                if (result == null) {
                    boolean isEnablePackageName = config.isEnablePackageName();
                    boolean isEnableClassName = config.isEnableClassName();
                    if (isEnablePackageName || isEnableClassName) {
                        String finalPackageName = packageNameS;
                        if (isEnablePackageName) {
                            finalPackageName = newPackageNameS;
                        }
                        if (isEnableClassName) {
                            if (className.contains("$")) {
                                String outerClassName = originalName.split("\\$")[0];
                                String exist = ObfEnv.classNameObfMapping.get(outerClassName);
                                if (exist == null) {
                                    exist = finalPackageName + "/" + NameUtil.genNewName();
                                    ObfEnv.classNameObfMapping.put(outerClassName, exist);
                                }
                                finalName = exist + "$" + NameUtil.genNewName();
                            } else {
                                finalName = finalPackageName + "/" + NameUtil.genNewName();
                            }
                        } else {
                            finalName = finalPackageName + "/" + className;
                        }
                        ObfEnv.classNameObfMapping.put(originalName, finalName);
                    }
                }
            }else{
                // 如果是黑名单类 也需要记录
                ObfEnv.classNameObfMapping.put(originalName, originalName);
            }
        }

        // 处理 method name
        for (Map.Entry<ClassReference.Handle, List<MethodReference>> entry : AnalyzeEnv.methodsInClassMap.entrySet()) {
            ClassReference.Handle key = entry.getKey();
            List<MethodReference> value = entry.getValue();

            if (AnalyzeEnv.classMap.get(key).isEnum()) {
                continue;
            }

            String newClassName = ObfEnv.classNameObfMapping.getOrDefault(key.getName(), key.getName());
            if(newClassName.equals(key.getName())){
                // 如果无需混淆那么不做 method 混淆
                continue;
            }

            for (MethodReference mr : value) {
                String desc = mr.getDesc();
                List<String> s = DescUtil.extractClassNames(desc);
                for (String c : s) {
                    String co = ObfEnv.classNameObfMapping.getOrDefault(c, c);
                    desc = desc.replace(c, co);
                }
                String oldMethodName = mr.getName();
                if (oldMethodName.startsWith("lambda$") ||
                        oldMethodName.startsWith("access$") ||
                        oldMethodName.equals("<init>") ||
                        oldMethodName.equals("<clinit>")) {
                    continue;
                }

                MethodReference.Handle oldHandle = new MethodReference.Handle(
                        new ClassReference.Handle(newClassName),
                        oldMethodName, desc);

                String newMethodName = NameUtil.genNewMethod();
                MethodReference.Handle newHandle = new MethodReference.Handle(
                        new ClassReference.Handle(newClassName),
                        newMethodName, desc);
                ObfEnv.methodNameObfMapping.put(oldHandle, newHandle);
            }
        }

        // 处理 method mapping 中的 black method 问题
        Map<MethodReference.Handle, MethodReference.Handle>
                methodNameObfMapping = new HashMap<>(ObfEnv.methodNameObfMapping);
        for (Map.Entry<MethodReference.Handle, MethodReference.Handle> en : ObfEnv.methodNameObfMapping.entrySet()) {
            String oldClassName = en.getKey().getName();
            for (String s : ObfEnv.config.getMethodBlackList()) {
                if (s.equals(oldClassName)) {
                    methodNameObfMapping.remove(en.getKey());
                    methodNameObfMapping.put(en.getKey(), en.getKey());
                    break;
                }
                Pattern pattern = Pattern.compile(s, Pattern.DOTALL);
                Matcher matcher = pattern.matcher(oldClassName);
                if (matcher.matches()) {
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

            String newClassName = ObfEnv.classNameObfMapping.getOrDefault(c.getName(), c.getName());
            if(newClassName.equals(c.getName())){
                // 如果无需混淆那么不做 field 混淆
                continue;
            }

            for (String s : AnalyzeEnv.fieldsInClassMap.get(c.getName())) {
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
            DeleteInfoTransformer.transform();
        }

        if (config.isEnablePackageName() || config.isEnableClassName()) {
            // 包名或类名重命名
            ClassNameTransformer.transform();
        }

        if (config.isEnableMethodName()) {
            // 方法名重命名
            MethodNameTransformer.transform();
        }

        if (config.isEnableFieldName()) {
            // 属性重命名
            FieldNameTransformer.transform();
        }

        if (config.isEnableParamName()) {
            // 方法内参数混淆
            ParameterTransformer.transform();
        }

        if (config.isEnableXOR()) {
            // 异或混淆常数
            XORTransformer.transform();
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
                Files.write(dir.resolve(parts[parts.length - 1] + ".class"), code);
            } catch (Exception ignored) {
            }

            // 字符串加密和解密
            StringTransformer.transform();

            if (config.isEnableAdvanceString()) {
                // 字符串提取处理
                for (Map.Entry<String, String> entry : ObfEnv.classNameObfMapping.entrySet()) {
                    ArrayList<String> t = ObfEnv.stringInClass.get(new ClassReference.Handle(entry.getKey()));
                    if (t == null) {
                        continue;
                    }
                    ArrayList<String> newRes = new ArrayList<>();
                    for (String s : t) {
                        newRes.add(StringDecrypt.encrypt(s));
                    }
                    ObfEnv.newStringInClass.put(entry.getValue(), newRes);
                }

                // 字符串提取
                StringArrayTransformer.transform();

                if (config.isEnableXOR()) {
                    // 提取后再次异或处理
                    XORTransformer.transform();
                }
            }
        }

        if (config.isEnableJunk()) {
            // 花指令混淆
            JunkCodeTransformer.transform(config);
        }

        // 生成混淆后目标
        try {
            DirUtil.zip(Const.TEMP_DIR, newFile);
            if (!config.isKeepTempFile()) {
                DirUtil.deleteDirectory(new File(Const.TEMP_DIR));
            }
            logger.info("generate jar file: {}", newFile);
        } catch (Exception e) {
            logger.error("zip file error: {}", e.toString());
        }
    }
}
