package com.zhao.common.utils.encrypt;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * AESUtil
 * @Author: zhaolianqi
 * @Date: 2021/6/2 10:52
 * @Version: v1.0
 */
public class AESUtil {

    /**
     * 将Base64编码后的AES秘钥转换成SecretKey对象
     * @param secretKey
     * @return SecretKey对象
     * @throws Exception
     */
    private static SecretKey loadKeyAES(String secretKey) throws NoSuchAlgorithmException {
        // 根据指定的 RNG 算法, 创建安全随机数生成器
        SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
        // 设置 密钥key的字节数组 作为安全随机数生成器的种子
        random.setSeed(secretKey.getBytes());
        // 创建 AES算法生成器
        KeyGenerator gen = KeyGenerator.getInstance("AES");
        // 初始化算法生成器
        gen.init(128, random);
        // 生成 AES密钥对象, 也可以直接创建密钥对象: return new SecretKeySpec(key, ALGORITHM);
        return gen.generateKey();
    }

    /**
     * AES加密
     * @param source 加密内容
     * @param key SecretKey对象
     * @return 加密后的字节数组
     * @throws Exception
     */
    private static byte[] encryptAES(byte[] source, SecretKey key) throws Exception {
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE, key);
        return cipher.doFinal(source);
    }

    /**
     * AES解密
     * @param source 解密内容
     * @param key SecretKey对象
     * @return 解密后的字节数组
     * @throws Exception
     */
    private static byte[] decryptAES(byte[] source, SecretKey key) throws Exception {
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.DECRYPT_MODE, key);
        return cipher.doFinal(source);
    }

    /**
     * 加密内容
     * @param content 需要加密的内容
     * @return 返回String
     * @throws Exception
     */
    public static String encrypt(String aesKey, String content) {
        try {
            SecretKey secretKey = AESUtil.loadKeyAES(aesKey);
            // 用AES秘钥加密请求内容
            byte[] encryptContentRequest = AESUtil.encryptAES(content.getBytes(StandardCharsets.UTF_8), secretKey);
            return Base64.getEncoder().encodeToString(encryptContentRequest);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 解密内容
     * @param content 内容
     * @return
     * @throws Exception
     */
    public static String decrypt(String aesKey, String content) {
        // 用AES秘钥解密请求内容
        try {
            SecretKey secretKey = AESUtil.loadKeyAES(aesKey);
            byte[] response = AESUtil.decryptAES(Base64.getDecoder().decode(content), secretKey);
            return new String(response);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

//    public static void main(String[] args) {
//        String key = "mquery_sys_JZCX_0001";
//        System.out.println("ghId: " + encrypt(key, "gh_8a424dc645fd"));
//        System.out.println("ghAppId: " + encrypt(key, "wx5d6e73bdb3ceaf62"));
//        System.out.println("ghAppSecret: " + encrypt(key, "96a5d1e3254d0afa64a03e01012a5094"));
//        System.out.println("msg token: " + encrypt(key, "weixin"));
//        System.out.println("homePage: " + encrypt(key, "login"));
////        System.out.println(decrypt("123123", "1OOaSJPgpsG1t7Dx88Ko7w=="));
//
////        System.out.println("ghId decrypt: " + decrypt(key, "Myq6f+Pomyl7E96YlyCCeA=="));
//    }

}
