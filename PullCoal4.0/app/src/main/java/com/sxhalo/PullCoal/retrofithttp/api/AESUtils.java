package com.sxhalo.PullCoal.retrofithttp.api;

import com.sxhalo.PullCoal.common.base.Config;
import com.sxhalo.PullCoal.utils.GHLog;

import org.apache.commons.codec1.binary.Base64;

import java.io.UnsupportedEncodingException;
import java.security.SecureRandom;
import java.util.Random;
import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 *  AES 加密解密工具类
 * Created by amoldZhang on 2017/4/17.
 */
public class AESUtils {

    // / 获取公钥的key
    public final String PUBLIC_KEY = new Config().password;
    //"算法/模式/补码方式"
    public final String CipherModeEBC = "AES/ECB/PKCS5Padding";

    //"算法/模式/补码方式"
    public final String CipherModeCBC = "AES/CBC/PKCS5Padding";
    //"算法/模式/补码方式"
    public final String IV_CBC = "43C0EC5CECAD4396";

    /**
     *  AES EBC 模式加密
     * @param sSrc  密文
     * @return
     */
    public String EncryptECB(String sSrc){
        if (sSrc == null) {
            System.out.print("Key为空null");
            return null;
        }
        return EncryptECB(sSrc,PUBLIC_KEY);
    }

    /**
     *  AES EBC 模式加密
     * @param sSrc
     * @param sKey
     * @return
     */
    public String EncryptECB(String sSrc, String sKey) {
        byte[] encrypted = new byte[0];
        try {
            if (sKey == null) {
                GHLog.e("AES加密失败","Key为空null");
                return null;
            }
            if ("-1".equals(sKey)) {
                GHLog.e("判断Key","Key 未获取成功，请检查RSA 解密");
                return null;
            }
            byte[] raw = sKey.getBytes("utf-8");
            SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
            Cipher cipher = Cipher.getInstance(CipherModeEBC);// "算法/模式/补码方式"
            cipher.init(Cipher.ENCRYPT_MODE, skeySpec);
            encrypted = cipher.doFinal(sSrc.getBytes("utf-8"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new Base64().encodeToString(encrypted);// 此处使用BASE64做转码功能，同时能起到2次加密的作用。
    }

    /**
     * AES 解密
     * @param sSrc
     * @return
     */
    public String DecryptECB(String sSrc){
            // 判断Key是否正确
            if (sSrc == null) {
                GHLog.e("判断Key","Key为空null");
                return null;
            }
            return DecryptECB(sSrc,PUBLIC_KEY);
        }

    /**
     *  AES EBC 模式解密
     * @param sSrc  需要加密的内容
     * @param sKey  加密密码
     * @return
     * @throws Exception
     */
    public String DecryptECB(String sSrc, String sKey){
        try {
            // 判断Key是否正确
            if (sKey == null) {
                GHLog.e("判断Key","Key为空null");
                return null;
            }
            if ("-1".equals(sKey)) {
                GHLog.e("判断Key","Key 未获取成功，请检查RSA 解密");
                return null;
            }
            byte[] raw = sKey.getBytes("utf-8");
            SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
            Cipher cipher = Cipher.getInstance(CipherModeEBC);
            cipher.init(Cipher.DECRYPT_MODE, skeySpec);
            byte[] encrypted1 = new Base64().decode(sSrc);// 先用base64解密
            try {
                byte[] original = cipher.doFinal(encrypted1);
                String originalString = new String(original, "utf-8");
                return originalString;
            } catch (Exception e) {
                GHLog.e("AES解密失败", e.toString());
                return null;
            }
        } catch (Exception ex) {
            GHLog.e("AES解密失败", ex.toString());
            return null;
        }
    }

    /**
     * 加密
     *
     * @param content
     *            需要加密的内容
     * @param password
     *            加密密码
     * @return
     */
    public byte[] encryptCBC(String content, String password) {
        try {
            KeyGenerator kgen = KeyGenerator.getInstance("AES");
            kgen.init(128, new SecureRandom(password.getBytes()));
            SecretKey secretKey = kgen.generateKey();
            byte[] enCodeFormat = secretKey.getEncoded();
            SecretKeySpec key = new SecretKeySpec(enCodeFormat, "AES");
            Cipher cipher = Cipher.getInstance(CipherModeCBC);// 创建密码器
            // "算法/模式/补码方式"
            IvParameterSpec iv = new IvParameterSpec(
                    IV_CBC.getBytes());// 使用CBC模式，需要一个向量iv，可增加加密算法的强度
            // 加密前要进行编码,否则js无法解码
            byte[] byteContent = content.getBytes("utf-8");
            cipher.init(Cipher.ENCRYPT_MODE, key, iv);// 初始化
            byte[] result = cipher.doFinal(byteContent);
            return result; // 加密
            // return Base64.encodeBase64String(result);//
            // 此处使用BAES64做转码功能，同时能起到2次加密的作用。
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 解密
     *
     * @param content
     *            待解密内容
     * @param password
     *            解密密钥
     * @return
     */
    public byte[] decryptCBC(byte[] content, String password) {
        try {
            KeyGenerator kgen = KeyGenerator.getInstance("AES");
            kgen.init(128, new SecureRandom(password.getBytes()));
            SecretKey secretKey = kgen.generateKey();
            byte[] enCodeFormat = secretKey.getEncoded();
            SecretKeySpec key = new SecretKeySpec(enCodeFormat, "AES");
            Cipher cipher = Cipher.getInstance(CipherModeCBC);// 创建密码器
            IvParameterSpec iv = new IvParameterSpec(
                    IV_CBC.getBytes());
            cipher.init(Cipher.DECRYPT_MODE, key, iv);// 初始化
            // byte[] encrypted = Base64.decodeBase64(content);// 先用bAES64解密
            byte[] result = cipher.doFinal(content);
            return result;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    // 生成随机密锁
    public String getKey(int length) {
        StringBuffer sb = new StringBuffer();
        Random random = new Random();
        // 参数length，表示生成几位随机数
        for (int i = 0; i < length; i++) {
            String charOrNum = random.nextInt(2) % 2 == 0 ? "char" : "num";
            // 输出字母还是数字
            if ("char".equalsIgnoreCase(charOrNum)) {
                // 输出是大写字母还是小写字母
                int temp = random.nextInt(2) % 2 == 0 ? 65 : 97;
                sb.append((char) (random.nextInt(26) + temp));
            } else if ("num".equalsIgnoreCase(charOrNum)) {
                sb.append(String.valueOf(random.nextInt(10)));
            }
        }

        try {
            return new String(sb.toString().getBytes(), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            GHLog.e("生成随机密锁", e.toString());
        }
        return "mapabc2014214yxj";
    }

    /**
     * 将二进制转换成16进制
     *
     * @param buf
     * @return
     */
    public String parseByte2HexStr(byte buf[]) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < buf.length; i++) {
            String hex = Integer.toHexString(buf[i] & 0xFF);
            if (hex.length() == 1) {
                hex = '0' + hex;
            }
            sb.append(hex.toUpperCase());
        }
        return sb.toString();
    }

    /*public static void main(String args[]) throws Exception {
        String ss = EncryptECB(
                "userId=80000405,coalSalesId=20000200,coalName=123,mineMouthId=10000058,categoryId=3,oneQuote=123,isShowOne=1,calorificValue=31,totalSulfur=11,quantity=123123,grainSize=12312,volatileValue=3123,totalMoisture=123,fixedCarbon=123123,quote=123,twoQuote=123123,isShowTwo=0,loadingExpense=123,description=123",
                AESUtils.PUBLIC_KEY);
        System.out.print(ss);

    }*/
}
