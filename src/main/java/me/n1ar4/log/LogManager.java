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

package me.n1ar4.log;

public class LogManager {
    static LogLevel logLevel = LogLevel.INFO;

    public static void setLevel(LogLevel level) {
        logLevel = level;
    }

    public static Logger getLogger() {
        return new Logger();
    }
}
