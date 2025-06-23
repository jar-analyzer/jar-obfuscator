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

public class LogUtil {
    private static final int STACK_TRACE_INDEX = 5;

    public static String getClassName() {
        String fullClassName = Thread.currentThread()
                .getStackTrace()[STACK_TRACE_INDEX].getClassName();
        int lastDotIndex = fullClassName.lastIndexOf('.');
        if (lastDotIndex != -1) {
            return fullClassName.substring(lastDotIndex + 1);
        } else {
            return fullClassName;
        }
    }

    public static String getMethodName() {
        return Thread.currentThread()
                .getStackTrace()[STACK_TRACE_INDEX].getMethodName();
    }

    public static String getLineNumber() {
        return String.valueOf(Thread.currentThread()
                .getStackTrace()[STACK_TRACE_INDEX].getLineNumber());
    }
}
