package com.sxhalo.PullCoal.utils;

import org.apache.commons.codec1.binary.Base64;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import java.io.UnsupportedEncodingException;
import java.security.*;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.HashMap;
import java.util.Map;

public class RSAEncrypt {

    //非对称密钥算法
    private static final String KEY_ALGORITHM = "RSA";
    private static final String KEY_ANDROID = "RSA/ECB/PKCS1Padding";
    /**
     * 密钥长度，DH算法的默认密钥长度是1024
     * 密钥长度必须是64的倍数，在512到65536位之间
     */
    private static final int KEY_SIZE = 1024;
    // 公钥
    private static final String PUBLIC_KEY = "RSAPublicKey";
    // 私钥
    private static final String PRIVATE_KEY = "RSAPrivateKey";

    private static Map<String, String> keyMap = new HashMap<String, String>(); // 用于封装随机产生的公钥与私钥

    private RSAEncrypt() {
        try {
            initKey();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static RSAEncrypt single = null;

    // 静态工厂方法
    public static RSAEncrypt getInstance() {
        if (single == null) {
            single = new RSAEncrypt();
        }
        return single;
    }

    public Map<String, String> getKeyMap() {
        return keyMap;
    }

    /**
     * 初始化密钥对
     *
     * @return Map 甲方密钥的Map
     */
    private void initKey() throws Exception {
        // 实例化密钥生成器
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(KEY_ALGORITHM);
        // 初始化密钥生成器
        keyPairGenerator.initialize(KEY_SIZE);
        // 生成密钥对
        KeyPair keyPair = keyPairGenerator.generateKeyPair();
        // 甲方公钥
        RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
        // 甲方私钥
        RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();
        // 将密钥存储在map中
        keyMap.put(PUBLIC_KEY, Base64.encodeBase64String(publicKey.getEncoded()));
        keyMap.put(PRIVATE_KEY, Base64.encodeBase64String(privateKey.getEncoded()));
    }

    /**
     * 私钥加密
     *
     * @param data   待加密数据
     * @param keyStr 密钥
     * @return byte[] 加密数据
     */
    public String encryptByPrivateKey(String data, String keyStr) throws Exception {

        byte[] key = Base64.decodeBase64(keyStr);
        byte[] dataByte = data.getBytes("UTF-8");
        // 取得私钥
        PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(key);
        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
        // 生成私钥
        PrivateKey privateKey = keyFactory.generatePrivate(pkcs8KeySpec);
        // 数据加密
        Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
        cipher.init(Cipher.ENCRYPT_MODE, privateKey);
        return Base64.encodeBase64String(cipher.doFinal(dataByte));
    }

    /**
     * 公钥加密
     *
     * @param data   待加密数据
     * @param keyStr 密钥
     * @return byte[] 加密数据
     */
    public String encryptByPublicKey(String data, String keyStr) throws Exception {

        byte[] key = Base64.decodeBase64(keyStr);
        byte[] dataByte = data.getBytes("UTF-8");
        // 实例化密钥工厂
        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
        // 初始化公钥
        // 密钥材料转换
        X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(key);
        // 产生公钥
        PublicKey pubKey = keyFactory.generatePublic(x509KeySpec);
        // 数据加密
        Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
        cipher.init(Cipher.ENCRYPT_MODE, pubKey);
        return Base64.encodeBase64String(cipher.doFinal(dataByte));
    }

    /**
     * 私钥解密
     *
     * @param data   待解密数据
     * @param keyStr 密钥
     * @return byte[] 解密数据
     */
    public String decryptByPrivateKey(String data, String keyStr) throws Exception {

        byte[] key = Base64.decodeBase64(keyStr);
        byte[] dataByte = Base64.decodeBase64(data);
        // 取得私钥
        PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(key);
        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
        // 生成私钥
        PrivateKey privateKey = keyFactory.generatePrivate(pkcs8KeySpec);
        // 数据解密
        Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        return new String(cipher.doFinal(dataByte));
    }

    /**
     * 公钥解密
     *
     * @param data   待解密数据
     * @param keyStr 密钥
     * @return byte[] 解密数据
     */
    public static String decryptByPublicKey(String data, String keyStr) {
        try {
            //因为RSA加密后的内容经Base64再加密转换了一下，所以先Base64解密回来再给RSA解密
            byte[] key = new Base64().decode(keyStr.getBytes("utf-8"));
            byte[] dataByte = new Base64().decode(data.getBytes("utf-8"));
            PublicKey pubKey = getPublicKey(key);
            // 数据解密
            Cipher cipher = Cipher.getInstance(KEY_ANDROID);
            cipher.init(Cipher.DECRYPT_MODE, pubKey);
            return new String(cipher.doFinal(dataByte));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static PublicKey getPublicKey(byte[] key) {
        try {
            // 实例化密钥工厂
            KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
            // 初始化公钥
            // 密钥材料转换
            X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(key);
            // 产生公钥
            PublicKey pubKey = keyFactory.generatePublic(x509KeySpec);

            return pubKey;
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (InvalidKeySpecException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 取得私钥
     *
     * @return byte[] 私钥
     */
    public String getPrivateKey() {
        return keyMap.get(PRIVATE_KEY);
    }

    /**
     * 取得公钥
     *
     * @return byte[] 公钥
     */
    public String getPublicKey() throws Exception {
        return keyMap.get(PUBLIC_KEY);
    }


    /**
     * @param args
     * @throws Exception
     */
	/*public static void main(String[] args) throws Exception {
		// 初始化密钥
		// 生成密钥对
		RSAEncrypt encrypt = RSAEncrypt.getInstance();
		// 公钥
		String publicKey = encrypt.getPublicKey();

		// 私钥
		String privateKey = encrypt.getPrivateKey();
		System.out.println("公钥：/n" + publicKey);
		System.out.println("私钥：/n" + privateKey);

		System.out.println("================密钥对构造完毕,甲方将公钥公布给乙方，开始进行加密数据的传输=============");
		String str = "RSA密码交换算法";
		System.out.println("/n===========甲方向乙方发送加密数据==============");
		System.out.println("原文:" + str);
		// 甲方进行数据的加密
		String code1 = encrypt.encryptByPrivateKey(str, privateKey);
		System.out.println("加密后的数据：" + code1);
		System.out.println("===========乙方使用甲方提供的公钥对数据进行解密==============");
		// 乙方进行数据的解密
		String decode1 = encrypt.decryptByPublicKey(code1, publicKey);
		System.out.println("乙方解密后的数据：" + decode1);

		System.out.println("===========反向进行操作，乙方向甲方发送数据==============");

		str = "乙方向甲方发送数据RSA算法";

		System.out.println("原文:" + str);

		// 乙方使用公钥对数据进行加密
		String code2 = encrypt.encryptByPublicKey(str, publicKey);
		System.out.println("===========乙方使用公钥对数据进行加密==============");
		System.out.println("加密后的数据：" + code2);

		System.out.println("=============乙方将数据传送给甲方======================");
		System.out.println("===========甲方使用私钥对数据进行解密==============");

		// 甲方使用私钥对数据进行解密
		String decode2 = encrypt.decryptByPrivateKey(code2, privateKey);

		System.out.println("甲方解密后的数据：" + decode2);
	}*/
}
