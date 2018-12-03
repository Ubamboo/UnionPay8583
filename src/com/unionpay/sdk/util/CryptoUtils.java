package com.unionpay.sdk.util;

/**
 * @{#} CryptoUtils.java Create on 2006-7-23 10:57:10
 *
 * Copyright (c) 2006 by WASU Inc.
 */


import java.io.ByteArrayOutputStream;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

/**
 * ���ܹ�����
 * 
 * @author <a href="mailto:tangfeng@chances.com.cn">Coffee</a>
 * @version 1.0
 * 
 */
public class CryptoUtils {

    private static final String DES = "DESede";

    static int readBufferSize = 8 * 1024;

    static String algorithm = "MD5";

    /**
     * ����3DES�ļ���
     * 
     * @param str
     *            ��������
     * @param strKey
     *            ����KEY
     * @return ���ܺ������
     */
    public static byte[] encrypt(byte str[], byte strKey[]) {
        try {
            javax.crypto.SecretKey key = new SecretKeySpec(strKey, DES);
            Cipher cipher = Cipher.getInstance(DES);
            cipher.init(Cipher.ENCRYPT_MODE, key);
            return cipher.doFinal(str);

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        }
        return null;

    }

    /**
     * ��������
     * 
     * @param str
     *            ���ܵ�����
     * @param strKey
     *            ���ܵ�KEY
     * @return ���ܺ������
     */

    public static byte[] decrypt(byte str[], byte strKey[]) {
        try {
            javax.crypto.SecretKey key = new SecretKeySpec(strKey, DES);
            Cipher cipher = Cipher.getInstance(DES);
            cipher.init(Cipher.DECRYPT_MODE, key);
            return cipher.doFinal(str);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * ͨ��3DES�㷨�����н���
     * 
     * @param str
     *            16���Ƶ��ַ���
     * @param strKey
     *            �����ֵ�16�����ַ���
     * @return ���ܺ������
     */
    public static String decrypt(String str, String strKey) {
        byte source[] = hexStr(str);
        byte keys[] = strKey.getBytes();
        byte result[] = new byte[64];
        for (int i = 0; i < 8; i++) {
            byte aSrc[] = new byte[8];
            byte aKey[] = new byte[8];
            System.arraycopy(source, i * 8, aSrc, 0, 8);
            System.arraycopy(keys, i, aKey, 0, 8);
            System.arraycopy(decrypt(aSrc, aKey), 0, result, i * 8, 8);
        }

        for (int i = 0; i < result.length; i++)
            if (result[i] == 0)
                return new String(result, 0, i);

        return "";
    }

    /**
     * ���ֽ�����ת��Ϊ16�����ַ���
     * 
     * @param data
     *            ����ת�����ֽ�����
     * @return 16���Ƶ��ַ���
     */
    public static String byte2HexString(byte[] data) {
        StringBuffer checksumSb = new StringBuffer();
        for (int i = 0; i < data.length; i++) {
            String hexStr = Integer.toHexString(0x00ff & data[i]);
            if (hexStr.length() < 2) {
                checksumSb.append("0");
            }
            checksumSb.append(hexStr);
        }
        return checksumSb.toString();
    }

    /**
     * 16�����ַ���ת��Ϊ�ֽ�����
     * 
     * @param digits
     *            16���Ƶ��ַ���
     * @return �ַ�����
     */
    public static byte[] hexStr(String digits) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        for (int i = 0; i < digits.length(); i += 2) {
            char c1 = digits.charAt(i);
            if ((i + 1) >= digits.length())
                throw new IllegalArgumentException();
            char c2 = digits.charAt(i + 1);
            byte b = 0;
            if ((c1 >= '0') && (c1 <= '9'))
                b += ((c1 - '0') * 16);
            else if ((c1 >= 'a') && (c1 <= 'f'))
                b += ((c1 - 'a' + 10) * 16);
            else if ((c1 >= 'A') && (c1 <= 'F'))
                b += ((c1 - 'A' + 10) * 16);
            else
                throw new IllegalArgumentException();
            if ((c2 >= '0') && (c2 <= '9'))
                b += (c2 - '0');
            else if ((c2 >= 'a') && (c2 <= 'f'))
                b += (c2 - 'a' + 10);
            else if ((c2 >= 'A') && (c2 <= 'F'))
                b += (c2 - 'A' + 10);
            else
                throw new IllegalArgumentException();
            baos.write(b);
        }
        return (baos.toByteArray());
    }

    /**
     * ����DES�ļ��ܼ�
     * 
     * @return ���ܼ�
     */
    public static String genKey() {
        KeyGenerator keygen;
        try {
            keygen = KeyGenerator.getInstance(DES);
            keygen.init(112);
            SecretKey key = keygen.generateKey();
            return byte2HexString(key.getEncoded());
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;

    }

    /**
     * ����MD5ժҪ
     * 
     * @param str
     *            ������ժҪ������
     * @return ����ժҪ16���ƴ�
     */
    public static String MD5(final String str) {
        if (str == null) {
            return null;
        }
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("MD5");
            return  byte2HexString(messageDigest.digest(str.getBytes()));
                   
        } catch (NoSuchAlgorithmException e) {
            throw new SecurityException(e.getMessage());
        }
    }
    
    public static void main(String[] args) {
    	String str="96D605EA176B7E9E";
    	String strkey="70F2A84CCBC2AE8625CBB0F2C22AB923";
		System.out.println(byte2HexString(decrypt(str.getBytes(), hexStr(strkey))));
	}
}