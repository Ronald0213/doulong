package com.hotyi.hotyi.utils;

import android.util.Base64;
import android.util.Log;

import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.Cipher;

/**
 * Created by HOTYI on 2017/8/28.
 */

public class RSAUtil {

    public static final String RSA = "RSA";
    public static final String ECB_PKCS1_PADDING = "RSA/ECB/PKCS1Padding";//加密填充方式
    private  static final String public_key = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQDgxOFIHXabcBxZOFBWz55WI18tQDTfHrU2IeJz6MVMgVv3yPXorrc8XucjPr16uMPy77qFGpJ2exOXpbzL0Rrmq9KzMDBdEWMttcppKVi+oTbz3xRGONyq3Gi22fNOiRPOOWO5NeuYMNNo7iV3egNgt2kHsW86/XAwB9Cbr0GUyQIDAQAB";
    private  static final String private_key = "MIICeAIBADANBgkqhkiG9w0BAQEFAASCAmIwggJeAgEAAoGBAODE4UgddptwHFk4UFbPnlYjXy1ANN8etTYh4nPoxUyBW/fI9eiutzxe5yM+vXq4w/LvuoUaknZ7E5elvMvRGuar0rMwMF0RYy21ymkpWL6hNvPfFEY43KrcaLbZ806JE845Y7k165gw02juJXd6A2C3aQexbzr9cDAH0JuvQZTJAgMBAAECgYEAl9nrKUFehBz1ygEVpdCWdDNpdbTPA35Hhs7VouE7ijhK3dhS6mQ/PvYOyez1Lhftqg7zwED3ejwkPGuoZTpcJP62hauoZooR8XCAJ6ZHEZpuJEwUH6DUiujmitkXzXXkZ+DhFEvWzLY4AfiuOLv/Az+MHVHbofAl4F9BI0fmr0ECQQD62BAzNU2ePqYLzodLjjM4y1ndH/XrMhdeo7nhUY47vHOl5uxiE+KvXuuZ29kUH7hMsG/nUiovPRsOT88kzTatAkEA5WOcYlxA0CLeGd3E62u4zH7CNrquZ3+423OCyjjx7JxPa9Fq2nxIPqiAp9i/Y2qFBErY4egI/VWzdj61aDzGDQJAShu/XYGn9tKHeAGCUz4lv+fEGuIwY1YfNWSlq/3OSbO5bxA0Uh2R4UHn1ULwdVORvYZ66RqLP/2LmsTVbAf82QJBALSX86rMjopOqSUcH8hoipkUwrprxprdRyAelL24j16EwVJVERbp+ca6ym9aiXMvjYGPm6hfEZTBQAS74f4qupECQQD6Qq+wmcDB/qGtNIFUTCMj5L1ozZujmP6w1TmyLnlJqIkNbVp5QCXIvb7dSZA4LG7IvwkcP++49C66IHjIMKWf";


    //**********公钥加密
    public static String encryptByPublicKey(String str) throws Exception {
        try {
            byte[] publicKey = Base64.decode(public_key,Base64.DEFAULT);
            byte[] data = str.getBytes();
//             得到公钥
            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(publicKey);
            KeyFactory kf = KeyFactory.getInstance(RSA);
            PublicKey keyPublic = kf.generatePublic(keySpec);
//             加密数据
            Cipher cp = Cipher.getInstance(ECB_PKCS1_PADDING);
            cp.init(Cipher.ENCRYPT_MODE, keyPublic);
            String str_ss = Base64.encodeToString(cp.doFinal(data),Base64.DEFAULT);
            return str_ss;
        } catch (Exception e) {
            Log.e("RSA", e.toString());
            return null;
        }

    }


    //*********私钥解密
    public static String decryptByPrivateKey(String str) throws Exception {
        byte[] privateKey = Base64.decode(private_key, Base64.DEFAULT);
        // 得到私钥
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(privateKey);
        KeyFactory kf = KeyFactory.getInstance(RSA);
        PrivateKey keyPrivate = kf.generatePrivate(keySpec);
        byte[] encrypted = str.getBytes();
        // 解密数据
        Cipher cp = Cipher.getInstance(ECB_PKCS1_PADDING);
        cp.init(Cipher.DECRYPT_MODE, keyPrivate);
        byte[] arr = cp.doFinal(encrypted);
        String str_ss = Base64.encodeToString(cp.doFinal(arr), Base64.DEFAULT);
        return str_ss;
    }

    //**********私钥加密（签名）
    public static String encryptByPrivateKey(String str) throws Exception {
        try {
            byte[] data = str.getBytes();
//             得到公钥
            byte[] privateKey = Base64.decode(private_key, Base64.DEFAULT);
            // 得到私钥
            PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(privateKey);
            KeyFactory kf = KeyFactory.getInstance(RSA);
            PrivateKey keyPrivate = kf.generatePrivate(keySpec);
//             加密数据
            Cipher cp = Cipher.getInstance(ECB_PKCS1_PADDING);
            cp.init(Cipher.ENCRYPT_MODE, keyPrivate);
            String str_ss = Base64.encodeToString(cp.doFinal(data),Base64.DEFAULT);
            return str_ss;
        } catch (Exception e) {
            Log.e("RSA", e.toString());
            return null;
        }

    }

}
