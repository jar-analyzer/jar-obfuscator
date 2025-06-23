/*
 * MIT License
 *
 * Project URL: https://github.com/jar-analyzer/jar-obfuscator
 *
 * Copyright (c) 2024-2025 4ra1n (https://github.com/4ra1n)
 *
 * This project is distributed under the MIT license.
 *
 * https://opensource.org/license/mit
 */

package me.n1ar4.jar.obfuscator.config;

import com.beust.jcommander.Parameter;

public class BaseCmd {
    @Parameter(names = {"-j", "--jar"}, description = "jar file path")
    private String path;
    @Parameter(names = {"-c", "--config"}, description = "config yaml file")
    private String config;
    @Parameter(names = {"-g", "--generate"}, description = "generate config file")
    private boolean generate;

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
