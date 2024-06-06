package me.n1ar4.jar.obfuscator;

import me.n1ar4.jar.obfuscator.utils.ColorUtil;
import me.n1ar4.jar.obfuscator.utils.UpdateChecker;

public class Logo {
    public static void printLogo() {
        System.out.println(ColorUtil.green("     ____.________ ___.    _____                           __                \n" +
                "    |    |\\_____  \\\\_ |___/ ____\\_ __  ______ ____ _____ _/  |_  ___________ \n" +
                "    |    | /   |   \\| __ \\   __\\  |  \\/  ___// ___\\\\__  \\\\   __\\/  _ \\_  __ \\\n" +
                "/\\__|    |/    |    \\ \\_\\ \\  | |  |  /\\___ \\\\  \\___ / __ \\|  | (  <_> )  | \\/\n" +
                "\\________|\\_______  /___  /__| |____//____  >\\___  >____  /__|  \\____/|__|   \n" +
                "                  \\/    \\/                \\/     \\/     \\/                   "));
        System.out.println(ColorUtil.blue("Jar Obfuscator - An Open-Source Java Bytecode Obfuscation Tool"));
        System.out.println("Version: " + ColorUtil.red(Const.VERSION) +
                " URL: " + ColorUtil.red(Const.PROJECT_URL) + "\n");

        System.out.println("注意：方法名混淆 " + ColorUtil.yellow("enableMethodName") + " 需要考虑多种情况\n" +
                "例如混淆了某个 " + ColorUtil.yellow("@Override") + " 的方法名会导致方法无法正常执行\n" +
                "如果不是很熟悉目标 JAR 或新手，建议暂时关闭 " + ColorUtil.yellow("enableMethodName") + " 配置\n");
        System.out.println(ColorUtil.green("感谢使用！有问题和建议欢迎在 GITHUB ISSUE 反馈\n"));

        UpdateChecker.check();

        System.out.println();
    }
}
