package me.n1ar4.jar.obfuscator.core;

import me.n1ar4.jar.obfuscator.Const;
import me.n1ar4.jar.obfuscator.base.ClassField;
import me.n1ar4.jar.obfuscator.base.ClassFileEntity;
import me.n1ar4.jar.obfuscator.base.ClassReference;
import me.n1ar4.jar.obfuscator.base.MethodReference;
import me.n1ar4.jar.obfuscator.config.BaseConfig;
import me.n1ar4.jar.obfuscator.jvmti.ExportCommand;
import me.n1ar4.jar.obfuscator.jvmti.PatchCommand;
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
import java.util.List;
import java.util.Map;
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

        // 分析引用
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

        // 处理 class name
        for (ClassReference c : AnalyzeEnv.discoveredClasses) {
            String[] parts = c.getName().split("/");
            String className = parts[parts.length - 1];
            StringBuilder packageName = new StringBuilder();
            for (int i = 0; i < parts.length - 1; i++) {
                if (i > 0) packageName.append("/");
                packageName.append(parts[i]);
            }
            String packageNameS = packageName.toString();

            if (PackageUtil.notInWhiteList(packageNameS, config)) {
                continue;
            }

            String newName;
            if (className.contains("$")) {
                String a = c.getName();
                String sa = a.split("\\$")[0];
                String exist = ObfEnv.classNameObfMapping.get(sa);
                if (exist == null) {
                    continue;
                } else {
                    newName = exist + "$" + NameUtil.genNewName();
                    ObfEnv.classNameObfMapping.put(a, newName);
                }
            } else {
                newName = packageNameS + "/" + NameUtil.genNewName();
                ObfEnv.classNameObfMapping.put(c.getName(), newName);
            }
            if (c.getName().equals(ObfEnv.MAIN_CLASS)) {
                logger.info("new main: {}", newName.replace("/", "."));
            }
        }

        // 处理 method name
        for (Map.Entry<ClassReference.Handle, List<MethodReference>> entry : AnalyzeEnv.methodsInClassMap.entrySet()) {
            ClassReference.Handle key = entry.getKey();
            List<MethodReference> value = entry.getValue();

            if (PackageUtil.notInWhiteList(key.getName(), config)) {
                continue;
            }

            String newClassName = ObfEnv.classNameObfMapping.getOrDefault(key.getName(), key.getName());
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
                String newMethodName = NameUtil.genNewMethod();
                MethodReference.Handle oldHandle = new MethodReference.Handle(
                        new ClassReference.Handle(newClassName),
                        oldMethodName, desc);
                MethodReference.Handle newHandle = new MethodReference.Handle(
                        new ClassReference.Handle(newClassName),
                        newMethodName, desc);
                ObfEnv.methodNameObfMapping.put(oldHandle, newHandle);
            }
        }

        // 处理 field name
        for (ClassReference c : AnalyzeEnv.discoveredClasses) {

            if (PackageUtil.notInWhiteList(c.getName(), config)) {
                continue;
            }

            String newClassName = ObfEnv.classNameObfMapping.getOrDefault(c.getName(), c.getName());
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

        if (config.isEnableClassName()) {
            // 类名重命名
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
            String name = StringDecryptDump.name;
            String[] parts = name.split("/");
            Path codePath = tmpDir.resolve(Paths.get(parts[0]));
            try {
                Files.createDirectories(codePath);
            } catch (Exception ignored) {
            }
            try {
                Files.write(codePath.resolve(parts[1] + ".class"), code);
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
            DirUtil.deleteDirectory(new File(Const.TEMP_DIR));
            logger.info("generate jar file: {}", newFile);
        } catch (Exception e) {
            logger.error("zip file error: {}", e.toString());
        }

        if (config.isEnableSuperObfuscate()) {
            PatchCommand patchCommand = new PatchCommand();
            patchCommand.setKey(config.getSuperObfuscateKey());
            patchCommand.setPackageName(config.getSuperObfuscatePackage());
            patchCommand.setJarPath(newFile);

            patchCommand.execute();

            ExportCommand exportCommand = new ExportCommand();
            exportCommand.setOutputPath(null);

            exportCommand.execute();
        }
    }
}
