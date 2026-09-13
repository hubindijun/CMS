package com.cms.common.util;

import javax.crypto.Cipher;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

/**
 * RSA 加密工具类
 * <p>密钥格式：Base64 编码的 X.509 公钥 / PKCS#8 私钥</p>
 */
public class RsaUtils {

    private static final String RSA_ALGORITHM = "RSA";
    private static final String RSA_TRANSFORMATION = "RSA/ECB/PKCS1Padding";
    private static final String SIGNATURE_ALGORITHM = "SHA256withRSA";
    private static final int KEY_SIZE = 2048;

    private RsaUtils() {}

    /**
     * 生成 RSA 密钥对
     *
     * @return 密钥对（公钥 + 私钥）
     */
    public static KeyPair generateKeyPair() {
        try {
            KeyPairGenerator generator = KeyPairGenerator.getInstance(RSA_ALGORITHM);
            generator.initialize(KEY_SIZE);
            return generator.generateKeyPair();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("生成 RSA 密钥对失败", e);
        }
    }

    /**
     * 生成 Base64 编码的 RSA 密钥对
     *
     * @return Base64KeyPair(publicKeyBase64, privateKeyBase64)
     */
    public static Base64KeyPair generateKeyPairBase64() {
        KeyPair keyPair = generateKeyPair();
        return new Base64KeyPair(
                Base64.getEncoder().encodeToString(keyPair.getPublic().getEncoded()),
                Base64.getEncoder().encodeToString(keyPair.getPrivate().getEncoded())
        );
    }

    /**
     * 公钥加密
     *
     * @param content        明文内容
     * @param publicKeyBase64 Base64 编码的公钥
     * @return Base64 编码的密文
     */
    public static String encrypt(String content, String publicKeyBase64) {
        try {
            PublicKey publicKey = parsePublicKey(publicKeyBase64);
            Cipher cipher = Cipher.getInstance(RSA_TRANSFORMATION);
            cipher.init(Cipher.ENCRYPT_MODE, publicKey);

            byte[] data = content.getBytes(StandardCharsets.UTF_8);
            byte[] encrypted = doFinal(cipher, data, KEY_SIZE / 8 - 11);
            return Base64.getEncoder().encodeToString(encrypted);
        } catch (Exception e) {
            throw new RuntimeException("RSA 加密失败", e);
        }
    }

    /**
     * 私钥解密
     *
     * @param contentBase64   Base64 编码的密文
     * @param privateKeyBase64 Base64 编码的私钥
     * @return 明文内容
     */
    public static String decrypt(String contentBase64, String privateKeyBase64) {
        try {
            PrivateKey privateKey = parsePrivateKey(privateKeyBase64);
            Cipher cipher = Cipher.getInstance(RSA_TRANSFORMATION);
            cipher.init(Cipher.DECRYPT_MODE, privateKey);

            byte[] data = Base64.getDecoder().decode(contentBase64);
            byte[] decrypted = doFinal(cipher, data, KEY_SIZE / 8);
            return new String(decrypted, StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new RuntimeException("RSA 解密失败", e);
        }
    }

    /**
     * 私钥签名
     *
     * @param content          待签名内容
     * @param privateKeyBase64 Base64 编码的私钥
     * @return Base64 编码的签名
     */
    public static String sign(String content, String privateKeyBase64) {
        try {
            PrivateKey privateKey = parsePrivateKey(privateKeyBase64);
            Signature signature = Signature.getInstance(SIGNATURE_ALGORITHM);
            signature.initSign(privateKey);
            signature.update(content.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(signature.sign());
        } catch (Exception e) {
            throw new RuntimeException("RSA 签名失败", e);
        }
    }

    /**
     * 公钥验签
     *
     * @param content         原始内容
     * @param signBase64      Base64 编码的签名
     * @param publicKeyBase64 Base64 编码的公钥
     * @return true=验签通过 false=验签失败
     */
    public static boolean verify(String content, String signBase64, String publicKeyBase64) {
        try {
            PublicKey publicKey = parsePublicKey(publicKeyBase64);
            Signature signature = Signature.getInstance(SIGNATURE_ALGORITHM);
            signature.initVerify(publicKey);
            signature.update(content.getBytes(StandardCharsets.UTF_8));
            return signature.verify(Base64.getDecoder().decode(signBase64));
        } catch (Exception e) {
            throw new RuntimeException("RSA 验签失败", e);
        }
    }

    /**
     * 解析公钥
     */
    public static PublicKey parsePublicKey(String publicKeyBase64) {
        try {
            byte[] keyBytes = Base64.getDecoder().decode(publicKeyBase64);
            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keyBytes);
            KeyFactory keyFactory = KeyFactory.getInstance(RSA_ALGORITHM);
            return keyFactory.generatePublic(keySpec);
        } catch (Exception e) {
            throw new RuntimeException("解析 RSA 公钥失败", e);
        }
    }

    /**
     * 解析私钥
     */
    public static PrivateKey parsePrivateKey(String privateKeyBase64) {
        try {
            byte[] keyBytes = Base64.getDecoder().decode(privateKeyBase64);
            PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(keyBytes);
            KeyFactory keyFactory = KeyFactory.getInstance(RSA_ALGORITHM);
            return keyFactory.generatePrivate(keySpec);
        } catch (Exception e) {
            throw new RuntimeException("解析 RSA 私钥失败", e);
        }
    }

    /**
     * 分段加解密（RSA 单块有长度限制）
     */
    private static byte[] doFinal(Cipher cipher, byte[] data, int blockSize) throws Exception {
        int totalLen = data.length;
        if (totalLen <= blockSize) {
            return cipher.doFinal(data);
        }
        byte[] result = new byte[0];
        int offset = 0;
        while (offset < totalLen) {
            int len = Math.min(totalLen - offset, blockSize);
            byte[] block = cipher.doFinal(data, offset, len);
            byte[] temp = new byte[result.length + block.length];
            System.arraycopy(result, 0, temp, 0, result.length);
            System.arraycopy(block, 0, temp, result.length, block.length);
            result = temp;
            offset += len;
        }
        return result;
    }

    /**
     * Base64 编码的密钥对
     */
    public static class Base64KeyPair {
        private final String publicKey;
        private final String privateKey;

        public Base64KeyPair(String publicKey, String privateKey) {
            this.publicKey = publicKey;
            this.privateKey = privateKey;
        }

        public String getPublicKey() {
            return publicKey;
        }

        public String getPrivateKey() {
            return privateKey;
        }
    }
}
