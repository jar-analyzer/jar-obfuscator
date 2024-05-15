package me.n1ar4.jar.obfuscator.templates;

import me.n1ar4.log.LogManager;
import me.n1ar4.log.Logger;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

public class StringDecrypt {
    private static final Logger logger = LogManager.getLogger();
    private static String KEY = null;
    private static final String ALGORITHM = "AES";

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
            SecretKeySpec key = new SecretKeySpec(KEY.getBytes(), ALGORITHM);
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, key);

            byte[] encrypted = cipher.doFinal(input.getBytes());
            return Base64.getEncoder().encodeToString(encrypted);
        } catch (Exception e) {
            return null;
        }
    }

    @SuppressWarnings("unused")
    public static String decrypt(String encrypted) {
        try {
            SecretKeySpec key = new SecretKeySpec(KEY.getBytes(), ALGORITHM);
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, key);

            byte[] original = cipher.doFinal(Base64.getDecoder().decode(encrypted));
            return new String(original);
        } catch (Exception e) {
            return null;
        }
    }
}
