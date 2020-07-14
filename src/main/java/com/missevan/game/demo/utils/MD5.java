package com.missevan.game.demo.utils;

import com.missevan.game.sdk.utils.LogUtils;

import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by yangya on 2019-08-22.
 */
public class MD5 {
    private static final char[] HEX_DIGITS = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};

    public MD5() {
    }

    public static String toHexString(byte[] b) {
        StringBuilder sb = new StringBuilder(b.length * 2);

        for(int i = 0; i < b.length; ++i) {
            sb.append(HEX_DIGITS[(b[i] & 240) >>> 4]);
            sb.append(HEX_DIGITS[b[i] & 15]);
        }

        return sb.toString();
    }

    public static String hash(String s) {
        try {
            MessageDigest digest = MessageDigest.getInstance("MD5");
            digest.update(s.getBytes(Charset.defaultCharset()));
            byte[] messageDigest = digest.digest();
            return toHexString(messageDigest);
        } catch (NoSuchAlgorithmException var3) {
            LogUtils.printExceptionStackTrace(var3);
            return "";
        }
    }

    public static String sign(String s, String key) {
        try {
            String text = s + key;
            MessageDigest digest = MessageDigest.getInstance("MD5");
            digest.update(text.getBytes(Charset.defaultCharset()));
            byte[] messageDigest = digest.digest();
            return toHexString(messageDigest);
        } catch (NoSuchAlgorithmException var5) {
            LogUtils.printExceptionStackTrace(var5);
            return "";
        }
    }
}
