package me.n1ar4.jar.obfuscator.templates;

import me.n1ar4.log.LogManager;
import me.n1ar4.log.Logger;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class StringDecrypt {
    private static final Logger logger = LogManager.getLogger();
    private static String KEY = null;
    private static final String ALGORITHM = "AES";
    private static final Charset CHARSET = StandardCharsets.UTF_8;

    public static void changeKEY(String key) {
        if (key != null && key.length() == 16) {
            KEY = key;
            logger.info("change encrypt aes key to: {}", key);
            return;
        }
        key = "Y4SuperSecretKey";
        logger.warn("aes encrypt key length muse be 16");
        logger.info("change encrypt aes key to: {}", key);
    }

    public static String encrypt(String input) {
        try {
            SecretKeySpec key = new SecretKeySpec(KEY.getBytes(CHARSET), ALGORITHM);
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, key);
            byte[] encrypted = cipher.doFinal(input.getBytes(CHARSET));
            return new String(Base64.getEncoder().encode(encrypted), CHARSET);
        } catch (Exception e) {
            return null;
        }
    }

    @SuppressWarnings("unused")
    public static String decrypt(String encrypted) {
        try {
            SecretKeySpec key = new SecretKeySpec(KEY.getBytes(CHARSET), ALGORITHM);
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, key);
            byte[] data = encrypted.getBytes(CHARSET);
            byte[] original = cipher.doFinal(Base64.getDecoder().decode(data));
            return new String(original, CHARSET);
        } catch (Exception e) {
            return null;
        }
    }
}
