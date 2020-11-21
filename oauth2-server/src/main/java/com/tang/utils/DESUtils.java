package com.tang.utils;


import com.baomidou.mybatisplus.core.toolkit.StringUtils;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.IvParameterSpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

/**
 * DES 加密
 */
public class DESUtils {

    public static String encryptString(String keySrc, String passWord) throws Exception {
        if (StringUtils.isBlank(passWord)) {
            return "";
        }
        // 长度最少要8个字符
        byte[] key = keySrc.getBytes();
        String encBase64Content;
        byte[] encContent = encrypt(key, passWord);
        encBase64Content = Base64.getEncoder().encodeToString(encContent);
        return encBase64Content;
    }

    public static byte[] encrypt(byte[] rawKeyData, String str) throws Exception {
        byte[] iv = {1, 2, 3, 4, 5, 6, 7, 8};
        IvParameterSpec zeroIv = new IvParameterSpec(iv);
        DESKeySpec dks = new DESKeySpec(rawKeyData);
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
        SecretKey key = keyFactory.generateSecret(dks);
        Cipher cipher = Cipher.getInstance("DES/CBC/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, key, zeroIv);
        byte[] data = str.getBytes();
        return cipher.doFinal(data);
    }


    public static String decryptString(String keySrc, String passWord) throws Exception {
        if (StringUtils.isBlank(passWord)) {
            return "";
        }
        // 长度最少要8个字符
        byte[] key = Base64.getDecoder().decode(passWord);
        byte[] encContent = decrypt(key, keySrc);
        return new String(encContent, StandardCharsets.UTF_8);
    }

    public static byte[] decrypt(byte[] rawKeyData, String password) throws Exception {
        byte[] iv = {1, 2, 3, 4, 5, 6, 7, 8};
        IvParameterSpec zeroIv = new IvParameterSpec(iv);
        // 创建一个DESKeySpec对象
        DESKeySpec desKey = new DESKeySpec(password.getBytes());
        // 创建一个密匙工厂
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
        // 将DESKeySpec对象转换成SecretKey对象
        SecretKey securekey = keyFactory.generateSecret(desKey);
        // Cipher对象实际完成解密操作
        Cipher cipher = Cipher.getInstance("DES/CBC/PKCS5Padding");
        // 用密匙初始化Cipher对象
        cipher.init(Cipher.DECRYPT_MODE, securekey, zeroIv);
        // 真正开始解密操作
        return cipher.doFinal(rawKeyData);
    }

    public static void main(String[] args) throws Exception {
        String secretKey = "secretKey";
        String password = "123456";
        System.out.println(DESUtils.encryptString(secretKey, password));

        System.out.println(Base64.getEncoder().encodeToString("test:123456".getBytes()));
    }
}
