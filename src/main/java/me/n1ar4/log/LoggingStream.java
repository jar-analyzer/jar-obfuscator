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

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;

public class LoggingStream extends PrintStream {
    private final Logger logger;
    private final OutputStream originalOut;

    public LoggingStream(OutputStream out, Logger logger) {
        super(out);
        this.logger = logger;
        this.originalOut = out;
    }

    @Override
    public void println(String x) {
        if (!isLoggerCall()) {
            logger.info(x);
        } else {
            directPrintln(x);
        }
    }

    private boolean isLoggerCall() {
        for (StackTraceElement element : Thread.currentThread().getStackTrace()) {
            if (element.getClassName().equals("me.n1ar4.log.Logger")) {
                return true;
            }
        }
        return false;
    }

    private void directPrintln(String x) {
        synchronized (this) {
            byte[] bytes = (x + System.lineSeparator()).getBytes();
            try {
                originalOut.write(bytes);
                originalOut.flush();
            } catch (IOException e) {
                setError();
            }
        }
    }
}