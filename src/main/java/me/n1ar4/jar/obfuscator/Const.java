package me.n1ar4.jar.obfuscator;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;

import java.nio.file.Path;
import java.nio.file.Paths;

public interface Const {
    String VERSION = "2.0.0-RC2";
    String PROJECT_URL = "https://github.com/jar-analyzer/jar-obfuscator";
    String TEMP_DIR = "jar-obfuscator-temp";
    Path configPath = Paths.get("config.yaml");
    int ASMVersion = Opcodes.ASM9;
    int ReaderASMOptions = ClassReader.SKIP_FRAMES;
    int WriterASMOptions = ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS;
}