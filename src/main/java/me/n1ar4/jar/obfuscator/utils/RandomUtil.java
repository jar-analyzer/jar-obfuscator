package me.n1ar4.jar.obfuscator.utils;

import org.objectweb.asm.Opcodes;

import java.util.Random;

public class RandomUtil {
    public static int genInt(int start, int end) {
        if (start >= end) {
            return 0;
        }
        Random random = new Random(System.currentTimeMillis());
        return start + random.nextInt(end - start);
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
