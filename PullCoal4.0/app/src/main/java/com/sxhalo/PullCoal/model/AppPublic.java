package com.sxhalo.PullCoal.model;

import com.sxhalo.PullCoal.retrofithttp.api.RSAEncrypt;

/**
 * Created by amoldZhang on 2019/3/26.
 */

public class AppPublic {
    private String error; //  错误
    private String publicKey; //  RSA 公钥
    private String secretKey; //  RSA 加密后的 aes密钥
    private String accessKey; //RSA 加密后的accessKey

    public String getPublicKey() {
        return publicKey;
    }

    public void setPublicKey(String publicKey) {
        this.publicKey = publicKey;
    }

    public String getSecretKey() {
        try {
            secretKey = RSAEncrypt.decryptByPublicKey(secretKey, publicKey);
            return secretKey;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }

    public String getAccessKey() {
        try {
            // RSA 公钥解密
            accessKey = RSAEncrypt.decryptByPublicKey(accessKey,publicKey);
            return accessKey;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public void setAccessKey(String accessKey) {
        this.accessKey = accessKey;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }
}
