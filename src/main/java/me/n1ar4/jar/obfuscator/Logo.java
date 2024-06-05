package me.n1ar4.jar.obfuscator;

public class Logo {
    public static void printLogo() {
        System.out.println("     ____.________ ___.    _____                           __                \n" +
                "    |    |\\_____  \\\\_ |___/ ____\\_ __  ______ ____ _____ _/  |_  ___________ \n" +
                "    |    | /   |   \\| __ \\   __\\  |  \\/  ___// ___\\\\__  \\\\   __\\/  _ \\_  __ \\\n" +
                "/\\__|    |/    |    \\ \\_\\ \\  | |  |  /\\___ \\\\  \\___ / __ \\|  | (  <_> )  | \\/\n" +
                "\\________|\\_______  /___  /__| |____//____  >\\___  >____  /__|  \\____/|__|   \n" +
                "                  \\/    \\/                \\/     \\/     \\/                   ");
        System.out.println("Jar Obfuscator - An Open-Source Java Bytecode Obfuscation Tool");
        System.out.println("Version: " + Const.VERSION + " URL: " + Const.PROJECT_URL + "\n");
        System.out.println("注意：当前版本的方法名混淆 enableMethodName 不稳定可能报错\n" +
                "建议暂时关闭 enableMethodName 配置替换为组合使用其他各种混淆搭配");
        System.out.println("感谢使用！有问题和建议欢迎在 GITHUB ISSUE 反馈");
        System.out.println();
    }
}
