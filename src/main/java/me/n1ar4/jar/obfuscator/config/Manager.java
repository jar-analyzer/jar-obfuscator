package me.n1ar4.jar.obfuscator.config;

import me.n1ar4.jar.obfuscator.asm.JunkCodeChanger;
import me.n1ar4.jar.obfuscator.core.ObfEnv;
import me.n1ar4.jar.obfuscator.utils.NameUtil;
import me.n1ar4.log.LogLevel;
import me.n1ar4.log.LogManager;
import me.n1ar4.log.Logger;
import me.n1ar4.log.LoggingStream;

public class Manager {
    private static final Logger logger = LogManager.getLogger();

    public static boolean initConfig(BaseConfig config) {
        // LOG LEVEL
        String logLevel = config.getLogLevel();
        switch (logLevel) {
            case "debug":
                LogManager.setLevel(LogLevel.DEBUG);
                break;
            case "info":
                LogManager.setLevel(LogLevel.INFO);
                break;
            case "warn":
                LogManager.setLevel(LogLevel.WARN);
                break;
            case "error":
                LogManager.setLevel(LogLevel.ERROR);
                break;
            default:
                logger.error("error log level");
                return false;
        }
        System.setOut(new LoggingStream(System.out, logger));
        System.out.println("set log io-streams");
        System.setErr(new LoggingStream(System.err, logger));
        System.err.println("set log err-streams");

        // CHARS
        if (config.getObfuscateChars() == null ||
                config.getObfuscateChars().length < 3) {
            NameUtil.CHAR_POOL = new char[]{'i', 'l', 'L', '1', 'I'};
        } else {
            char[] data = new char[config.getObfuscateChars().length];
            for (int i = 0; i < config.getObfuscateChars().length; i++) {
                String s = config.getObfuscateChars()[i];
                if (s == null || s.isEmpty()) {
                    logger.error("null in obfuscate chars");
                    return false;
                }
                data[i] = s.charAt(0);
            }
            NameUtil.CHAR_POOL = data;
        }

        // OTHERS
        config.setMainClass(config.getMainClass().replace(".", "/"));
        ObfEnv.MAIN_CLASS = config.getMainClass();
        String[] newData = new String[config.getObfuscatePackage().length];
        for (int i = 0; i < config.getObfuscatePackage().length; i++) {
            newData[i] = config.getObfuscatePackage()[i].replace(".", "/");
        }
        config.setObfuscatePackage(newData);

        JunkCodeChanger.MAX_JUNK_NUM = config.getMaxJunkOneClass();
        ObfEnv.ADVANCE_STRING_NAME = config.getAdvanceStringName();

        return true;
    }
}
