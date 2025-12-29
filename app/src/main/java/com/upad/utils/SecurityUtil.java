package com.upad.utils;

import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class SecurityUtil {
    private static final String KEY_ALGORITHM = "AES";
    private static final String HEX = "0123456789ABCDEF";
    /**
     * 加密
     */
    public static String encrypt(String seed, String cleartext) throws Exception {
        byte[] rawKey = getRawKey(seed.getBytes());
        byte[] result = encrypt(rawKey, cleartext.getBytes());
        return toHex(result);
    }

    /**
     * 解密
     */
    public static String decrypt(String seed, String encrypted) throws Exception {
        byte[] rawKey = getRawKey(seed.getBytes());
        byte[] enc = toByte(encrypted);
        byte[] result = decrypt(rawKey, enc);
        return new String(result);
    }

    private static byte[] getRawKey(byte[] seed) throws Exception {
        SecureRandom sr = SecureRandom.getInstance("SHA1PRNG", new CryptoProvider());
        //SecureRandom sr = SecureRandom.getInstance("SHA1PRNG", "Crypto");
        sr.setSeed(seed);
        KeyGenerator kGen = KeyGenerator.getInstance(KEY_ALGORITHM);
        //192 and 256 bits may not be available
        kGen.init(128, sr);
        SecretKey sKey = kGen.generateKey();
        return sKey.getEncoded();
    }

    private static byte[] encrypt(byte[] raw, byte[] clear) throws Exception {
        SecretKeySpec sKeySpec = new SecretKeySpec(raw, KEY_ALGORITHM);
        Cipher cipher = Cipher.getInstance(KEY_ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, sKeySpec, new IvParameterSpec(new byte[cipher.getBlockSize()]));
        return cipher.doFinal(clear);
    }

    private static byte[] decrypt(byte[] raw, byte[] encrypted) throws Exception {
        SecretKeySpec sKeySpec = new SecretKeySpec(raw, KEY_ALGORITHM);
        Cipher cipher = Cipher.getInstance(KEY_ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, sKeySpec, new IvParameterSpec(new byte[cipher.getBlockSize()]));
        return cipher.doFinal(encrypted);
    }

    private static String toHex(byte[] buf) {
        if (buf == null)
            return "";
        StringBuffer result = new StringBuffer(2 * buf.length);
        for (byte aBuf : buf) {
            appendHex(result, aBuf);
        }
        return result.toString();
    }

    private static byte[] toByte(String hexString) {
        int len = hexString.length() / 2;
        byte[] result = new byte[len];
        for (int i = 0; i < len; i++) {
            result[i] = Integer.valueOf(hexString.substring(2 * i, 2 * i + 2), 16).byteValue();
        }
        return result;
    }

    private static void appendHex(StringBuffer sb, byte b) {
        sb.append(HEX.charAt((b >> 4) & 0x0f)).append(HEX.charAt(b & 0x0f));
    }
}
