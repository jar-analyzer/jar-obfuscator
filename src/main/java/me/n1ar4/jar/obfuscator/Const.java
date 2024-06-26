package me.n1ar4.jar.obfuscator;

import org.objectweb.asm.Opcodes;

import java.nio.file.Path;
import java.nio.file.Paths;

public interface Const {
    String VERSION = "0.1.0";
    String PROJECT_URL = "https://github.com/jar-analyzer/jar-obfuscator";
    String UPDATE_URL = "https://jar-analyzer.oss-cn-hangzhou.aliyuncs.com/jar-obfuscator/version.txt";
    String DOWNLOAD_URL = "https://github.com/jar-analyzer/jar-obfuscator/releases/latest";
    String TEMP_DIR = "jar-obfuscator-temp";
    Path configPath = Paths.get("config.yaml");
    int ASMVersion = Opcodes.ASM9;
    int AnalyzeASMOptions = 0;
}