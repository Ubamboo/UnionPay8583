package com.unionpay.sdk.util;

import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * DES 加密算法 DES和3DES ECB模式的加解密
 *
 */
public class DesUtil {

	public final static String DES = "DES";

	/**
	 * 加密
	 * 
	 * @param data
	 *            byte[]
	 * @param key
	 *            byte[]
	 * @return byte[]
	 */
	public static byte[] desEncrypt(byte[] data, byte[] key) {
		try {
			// 生成一个可信任的随机数源
			SecureRandom sr = new SecureRandom();

			// 从原始密钥数据创建DESKeySpec对象
			DESKeySpec dks = new DESKeySpec(key);

			// 创建一个密钥工厂，然后用它把DESKeySpec转换成SecretKey对象
			SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(DES);
			SecretKey securekey = keyFactory.generateSecret(dks);

			// Cipher对象实际完成加密操作
			Cipher cipher = Cipher.getInstance("DES/ECB/NoPadding");

			// 用密钥初始化Cipher对象
			cipher.init(Cipher.ENCRYPT_MODE, securekey, sr);

			return cipher.doFinal(data);
		} catch (Throwable e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 解密
	 * 
	 * @param data
	 *            byte[]
	 * @param key
	 *            byte[]
	 * @return byte[]
	 */
	public static byte[] desDecrypt(byte[] data, byte[] key) {
		try {
			// 生成一个可信任的随机数源
			SecureRandom sr = new SecureRandom();

			// 从原始密钥数据创建DESKeySpec对象
			DESKeySpec dks = new DESKeySpec(key);
			// 创建一个密钥工厂，然后用它把DESKeySpec转换成SecretKey对象
			SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(DES);
			SecretKey securekey = keyFactory.generateSecret(dks);
			// Cipher对象实际完成解密操作
			Cipher cipher = Cipher.getInstance("DES/ECB/NoPadding");
			// 用密钥初始化Cipher对象
			cipher.init(Cipher.DECRYPT_MODE, securekey, sr);
			return cipher.doFinal(data);
		} catch (Throwable e) {
			e.printStackTrace();
		}
		return null;

	}

	public static byte[] DES_encrypt_3(byte[] datasource, byte[] key) {

		if ((key.length != 16) || ((datasource.length % 8) != 0)) {
			return null;
		}
		byte[] Lkey = new byte[8];
		byte[] Rkey = new byte[8];
		System.arraycopy(key, 0, Lkey, 0, 8);
		System.arraycopy(key, 8, Rkey, 0, 8);

		byte[] outdata, tmpdata;

		outdata = desEncrypt(datasource, Lkey);// 加
		tmpdata = desDecrypt(outdata, Rkey);// 解
		outdata = desEncrypt(tmpdata, Lkey);// 加

		return outdata;

	}

	public static byte[] desDecrypt_3(byte[] datasource, byte[] key) {

		if ((key.length != 16) || ((datasource.length % 8) != 0)) {
			return null;
		}
		byte[] Lkey = new byte[8];
		byte[] Rkey = new byte[8];
		System.arraycopy(key, 0, Lkey, 0, 8);
		System.arraycopy(key, 8, Rkey, 0, 8);
		byte[] outdata, tmpdata;

		outdata = desDecrypt(datasource, Lkey);// 解
		tmpdata = desEncrypt(outdata, Rkey);// 加
		outdata = desDecrypt(tmpdata, Lkey);// 解
		return outdata;
	}

	private static byte charToByte(char c) {
		return (byte) "0123456789ABCDEF".indexOf(c);
	}

	public static String bytesToHexString(byte[] src) {
		StringBuilder stringBuilder = new StringBuilder("");
		if (src == null || src.length <= 0) {
			return null;
		}
		for (int i = 0; i < src.length; i++) {
			int v = src[i] & 0xFF;
			String hv = Integer.toHexString(v);
			if (hv.length() < 2) {
				stringBuilder.append(0);
			}
			stringBuilder.append(hv);
		}
		return stringBuilder.toString();
	}

	public static byte[] hexStringToBytes(String hexString) {
		if (hexString == null || hexString.equals("")) {
			return null;
		}
		hexString = hexString.toUpperCase();
		int length = hexString.length() / 2;
		char[] hexChars = hexString.toCharArray();
		byte[] d = new byte[length];
		for (int i = 0; i < length; i++) {
			int pos = i * 2;
			d[i] = (byte) (charToByte(hexChars[pos]) << 4 | charToByte(hexChars[pos + 1]));

		}
		return d;
	}
	/** 
     * DES加密数据非填充方式 
     *  
     * @param hexKey 
     * @param hexData 
     * @param mode 
     * @return 
     * @throws Exception 
     */  
	public static String decEncNoPaddingDES(String hexKey, String hexData, int mode) throws Exception  
    {  
        SecretKey desKey = new SecretKeySpec(HexBinary.decode(hexKey), "DES");  
  
        Cipher cp = Cipher.getInstance("DES/ECB/NoPadding");  
        cp.init(mode, desKey);  
        byte[] bytes = cp.doFinal(HexBinary.decode(hexData));  
  
        return HexBinary.encode(bytes);  
    }  
  
    public static String encrypt(String hexKey, String hexData) throws Exception  
    {  
        SecretKey desKey = new SecretKeySpec(HexBinary.decode(hexKey), "DES");  
  
        Cipher cp = Cipher.getInstance("DES");  
        cp.init(Cipher.ENCRYPT_MODE, desKey);  
        byte[] bytes = cp.doFinal(HexBinary.decode(hexData));  
  
        return HexBinary.encode(bytes);  
    }  
  
    /** 
     * 3Des加密非填充 
     *  
     * @param hexKey 
     * @param hexData 
     * @return 
     * @throws Exception 
     */  
    public static String encryptDesSede(String hexKey, String hexData) throws Exception  
    {  
        SecretKey desKey = new SecretKeySpec(HexBinary.decode(hexKey), "DESede");  
  
        Cipher cp = Cipher.getInstance("DESede/ECB/NoPadding");  
        cp.init(Cipher.ENCRYPT_MODE, desKey);  
        byte[] bytes = cp.doFinal(HexBinary.decode(hexData));  
  
        return HexBinary.encode(bytes);  
    }  

	public static void main(String[] args) {
		// 银联主密钥解密工作秘钥
		String strkey = "1234567890ABCDEF1234567890ABCDEF";
		String strdata = "1111111111111111";
		byte[] bt = desDecrypt_3(hexStringToBytes(strdata), hexStringToBytes(strkey));
		String out = bytesToHexString(bt);
		System.out.println("decryption_3结果为：" + out);
	}
}
