package me.n1ar4.jar.obfuscator.config;

import com.beust.jcommander.Parameter;

public class BaseCmd {
    @Parameter(names = {"-j", "--jar"}, description = "jar file path")
    private String path;
    @Parameter(names = {"-c", "--config"}, description = "config yaml file")
    private String config;
    @Parameter(names = {"-g", "--generate"}, description = "generate config file")
    private boolean generate;
    @Parameter(names = {"--check-update"}, description = "check update")
    private boolean check;
    @Parameter(names = {"-v", "--version"}, description = "version")
    private boolean version;
    @Parameter(names = {"-gui","--gui"},description = "use GUI")
    private boolean gui;

    public boolean isGui() {
        return gui;
    }

    public void setGui(boolean gui) {
        this.gui = gui;
    }

    public boolean isVersion() {
        return version;
    }

    public void setVersion(boolean version) {
        this.version = version;
    }

    public boolean isCheck() {
        return check;
    }

    public void setCheck(boolean check) {
        this.check = check;
    }

    public boolean isGenerate() {
        return generate;
    }

    public void setGenerate(boolean generate) {
        this.generate = generate;
    }

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
