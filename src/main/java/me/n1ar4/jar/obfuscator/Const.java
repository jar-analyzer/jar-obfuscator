package me.n1ar4.jar.obfuscator;

import org.objectweb.asm.Opcodes;

import java.nio.file.Path;
import java.nio.file.Paths;

public interface Const {
    String VERSION = "0.0.2-beta";
    String PROJECT_URL = "https://github.com/jar-analyzer/jar-obfuscator";
    String TEMP_DIR = "jar-obfuscator-temp";
    Path configPath = Paths.get("config.yaml");
    int ASMVersion = Opcodes.ASM9;
    int AnalyzeASMOptions = 0;
}