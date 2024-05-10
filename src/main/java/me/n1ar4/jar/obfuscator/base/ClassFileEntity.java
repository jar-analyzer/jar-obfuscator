package me.n1ar4.jar.obfuscator.base;

import me.n1ar4.log.LogManager;
import me.n1ar4.log.Logger;

import java.nio.file.Files;
import java.nio.file.Path;

public class ClassFileEntity {
    private static final Logger logger = LogManager.getLogger();
    private Path path;
    private String jarName;

    public Path getPath() {
        return path;
    }

    public void setPath(Path path) {
        this.path = path;
    }

    public String getJarName() {
        return jarName;
    }

    public void setJarName(String jarName) {
        this.jarName = jarName;
    }

    public ClassFileEntity() {
    }

    public byte[] getFile() {
        try {
            return Files.readAllBytes(this.path);
        } catch (Exception e) {
            logger.error("get file error: {}", e.toString());
        }
        return null;
    }
}
