package me.n1ar4.jar.obfuscator.config;

import com.beust.jcommander.Parameter;

public class BaseCmd {
    @Parameter(names = {"-j", "--jar"}, description = "jar file path")
    private String path;

    @Parameter(names = {"-c", "--config"}, description = "config yaml file")
    private String config;

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getConfig() {
        return config;
    }

    public void setConfig(String config) {
        this.config = config;
    }
}
