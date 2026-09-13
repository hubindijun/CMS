package com.cms.common.util;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * AES 加密工具类
 * <p>密钥格式：Base64 编码</p>
 */
public class AesUtils {

    private static final String AES_ALGORITHM = "AES";
    private static final String ECB_TRANSFORMATION = "AES/ECB/PKCS5Padding";
    private static final String CBC_TRANSFORMATION = "AES/CBC/PKCS5Padding";
    private static final int KEY_SIZE = 256;

    private AesUtils() {}

    /**
     * 生成 AES 密钥（256 位）
     *
     * @return 密钥
     */
    public static SecretKey generateKey() {
        try {
            KeyGenerator keyGenerator = KeyGenerator.getInstance(AES_ALGORITHM);
            keyGenerator.init(KEY_SIZE);
            return keyGenerator.generateKey();
        } catch (Exception e) {
            throw new RuntimeException("生成 AES 密钥失败", e);
        }
    }

    /**
     * 生成 Base64 编码的 AES 密钥
     *
     * @return Base64 编码的密钥字符串
     */
    public static String generateKeyBase64() {
        return Base64.getEncoder().encodeToString(generateKey().getEncoded());
    }

    // ==================== ECB 模式 ====================

    /**
     * AES-ECB 加密
     *
     * @param content  明文
     * @param keyBase64 Base64 编码的密钥
     * @return Base64 编码的密文
     */
    public static String encrypt(String content, String keyBase64) {
        try {
            SecretKey secretKey = parseKey(keyBase64);
            Cipher cipher = Cipher.getInstance(ECB_TRANSFORMATION);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            byte[] encrypted = cipher.doFinal(content.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(encrypted);
        } catch (Exception e) {
            throw new RuntimeException("AES 加密失败", e);
        }
    }

    /**
     * AES-ECB 解密
     *
     * @param contentBase64 Base64 编码的密文
     * @param keyBase64     Base64 编码的密钥
     * @return 明文
     */
    public static String decrypt(String contentBase64, String keyBase64) {
        try {
            SecretKey secretKey = parseKey(keyBase64);
            Cipher cipher = Cipher.getInstance(ECB_TRANSFORMATION);
            cipher.init(Cipher.DECRYPT_MODE, secretKey);
            byte[] decrypted = cipher.doFinal(Base64.getDecoder().decode(contentBase64));
            return new String(decrypted, StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new RuntimeException("AES 解密失败", e);
        }
    }

    // ==================== CBC 模式 ====================

    /**
     * 生成随机 IV（16 字节），用于 CBC 模式
     *
     * @return Base64 编码的 IV
     */
    public static String generateIvBase64() {
        byte[] iv = new byte[16];
        new SecureRandom().nextBytes(iv);
        return Base64.getEncoder().encodeToString(iv);
    }

    /**
     * AES-CBC 加密
     *
     * @param content   明文
     * @param keyBase64 Base64 编码的密钥
     * @param ivBase64  Base64 编码的 IV（16 字节）
     * @return Base64 编码的密文
     */
    public static String encryptCbc(String content, String keyBase64, String ivBase64) {
        try {
            SecretKey secretKey = parseKey(keyBase64);
            IvParameterSpec iv = new IvParameterSpec(Base64.getDecoder().decode(ivBase64));
            Cipher cipher = Cipher.getInstance(CBC_TRANSFORMATION);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, iv);
            byte[] encrypted = cipher.doFinal(content.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(encrypted);
        } catch (Exception e) {
            throw new RuntimeException("AES-CBC 加密失败", e);
        }
    }

    /**
     * AES-CBC 解密
     *
     * @param contentBase64 Base64 编码的密文
     * @param keyBase64     Base64 编码的密钥
     * @param ivBase64      Base64 编码的 IV（16 字节）
     * @return 明文
     */
    public static String decryptCbc(String contentBase64, String keyBase64, String ivBase64) {
        try {
            SecretKey secretKey = parseKey(keyBase64);
            IvParameterSpec iv = new IvParameterSpec(Base64.getDecoder().decode(ivBase64));
            Cipher cipher = Cipher.getInstance(CBC_TRANSFORMATION);
            cipher.init(Cipher.DECRYPT_MODE, secretKey, iv);
            byte[] decrypted = cipher.doFinal(Base64.getDecoder().decode(contentBase64));
            return new String(decrypted, StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new RuntimeException("AES-CBC 解密失败", e);
        }
    }

    /**
     * 解析 Base64 密钥为 SecretKey
     */
    public static SecretKey parseKey(String keyBase64) {
        byte[] keyBytes = Base64.getDecoder().decode(keyBase64);
        return new SecretKeySpec(keyBytes, AES_ALGORITHM);
    }
}
