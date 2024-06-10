package dot.help.api.utils;

import java.security.SecureRandom;

public class SecretKeyGenerator {
    public static byte[] generateRandomKey(int length) {
        SecureRandom secureRandom = new SecureRandom();
        byte[] key = new byte[length];
        secureRandom.nextBytes(key);
        return key;
    }

    public static String bytesToHex(byte[] bytes) {
        StringBuilder hexStringBuilder = new StringBuilder(2 * bytes.length);
        for (byte b : bytes) {
            hexStringBuilder.append(String.format("%02x", b & 0xff));
        }
        return hexStringBuilder.toString();
    }

    public static String generateRandomKeyHex(int length) {
        return bytesToHex(generateRandomKey(length));
    }
}

