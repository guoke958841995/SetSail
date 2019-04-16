package com.sxhalo.PullCoal.bean;

/**
 * desc
 *
 * @author Xiao_
 * @date 2019/4/4 0004
 */
public class AccessKeyBean {

    /**
     * secretKey :
     * accessKey :
     * publicKey :
     */

    private String secretKey;
    private String accessKey;
    private String publicKey;


    public String getSecretKey() {
        return secretKey;
    }

    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }

    public String getAccessKey() {
        return accessKey;
    }

    public void setAccessKey(String accessKey) {
        this.accessKey = accessKey;
    }

    public String getPublicKey() {
        return publicKey;
    }

    public void setPublicKey(String publicKey) {
        this.publicKey = publicKey;
    }

    @Override
    public String toString() {
        return "AccessKeyBean{" +
                "secretKey='" + secretKey + '\'' +
                ", accessKey='" + accessKey + '\'' +
                ", publicKey='" + publicKey + '\'' +
                '}';
    }
}
