package me.n1ar4.jar.obfuscator.utils;

import me.n1ar4.jrandom.core.JRandom;
import org.objectweb.asm.Opcodes;

public class RandomUtil {
    public static int genInt(int start, int end) {
        return JRandom.getInstance().getInt(start, end);
    }

    public static int genICONSTOpcode() {
        switch (RandomUtil.genInt(0, 6)) {
            case 0:
                return Opcodes.ICONST_0;
            case 1:
                return Opcodes.ICONST_1;
            case 2:
                return Opcodes.ICONST_2;
            case 3:
                return Opcodes.ICONST_3;
            case 4:
                return Opcodes.ICONST_4;
            case 5:
                return Opcodes.ICONST_5;
            default:
                return 0;
        }
    }
}
