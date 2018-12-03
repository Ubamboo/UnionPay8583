package com.unionpay.sdk.util;

import javax.crypto.Cipher;


public class UnionPayMacUtil {

	 /**
    * 计算通信的MAC
    * @param buf
    * @param datasize
    * @param mackey
    * @return
	 * @throws Exception 
    */
   public static String upGetMac( String macDataSource,String macKey) throws Exception{
   	 // 拆分MAC数据源，每组16位hex(8 byte())  
       String[] ds = splitData(macDataSource);
      
       // 用于排查mac
       System.out.println("---参与生成mac的 hex数据-----");
       for (int i = 0; i < ds.length; i++) {
    	   System.out.println(ds[i]);		
       }
       
       System.out.println("---参与生成mac的 hex数据-----");
       
       String des = "";  
       
       for (int i = 0; i < ds.length; i++){  
           if (i == 0){  
               // 第一次只做DES加密  
               des = DesUtil.decEncNoPaddingDES(macKey, ds[i], Cipher.ENCRYPT_MODE).toUpperCase();  
           } else{  
               // 用上一次 DES加密结果对 第 i 组数据做异或  
               des = StringUtils.XOR(des, ds[i]);  
               // 对异或后的数据做DES加密  
               des = DesUtil.decEncNoPaddingDES(macKey, des, Cipher.ENCRYPT_MODE).toUpperCase();  
           }  
       }  
       
       return des;
   }
   
   /*  
    * 将hexMacDataSource进行分组 每 16 字符 8byte 一组  
    */  
   private static String[] splitData(String hexMacDataSource){  
       int len = 0;  
       int modValue = hexMacDataSource.length() % 16;  
 
       if (modValue != 0) { 
           int hexSrcDataLen = hexMacDataSource.length();  
           int totalLen = hexSrcDataLen + (16 - modValue);  
           hexMacDataSource = StringUtils.padding(hexMacDataSource, "right", "0", totalLen);  
           
       }  
       len = hexMacDataSource.length() / 16;  
 
       String[] ds = new String[len];  
 
       for (int i = 0; i < ds.length; i++)  
       {  
           if (hexMacDataSource.length() >= 16)  
           {  
               ds[i] = hexMacDataSource.substring(0, 16);  
               hexMacDataSource = hexMacDataSource.substring(16);  
           } else  
           {  
               throw new IllegalArgumentException("填充的数据非法!");  
           }  
       }  
       return ds;  
   }  
   public static void main(String[] args) {
	   	String m0=HexStringUtils.toHex("0800");
	   	String m1="82200000800008000400000100000001";
	   	String m2=HexStringUtils.toHex("1123150351");    	
	   	String m3=HexStringUtils.toHex("156842");
	   	String m4=HexStringUtils.toHex("0810000001");
	   	String m7=HexStringUtils.toHex("2000000000000000");
	   	String m11=HexStringUtils.toHex("101");
	   	String m12="1111111111111111";
	   	String spiltStr="";
	   	
	   	// 在域和域之间插入一个空格
			String macDataSource=m0+spiltStr+m1+spiltStr+m2+spiltStr+m3+spiltStr+m4+spiltStr+m7+spiltStr+m11+spiltStr+m12;
			
			//所有的小写字母转换成大写字母
			macDataSource=macDataSource.toUpperCase();
			//  除了字母(A-Z)，数字(0-9)，空格，逗号(，)和点号(.)以外的字符都删去
			//macDataSource = macDataSource.replaceAll("[^A-Z0-9., ]", "");
			//删去所有域的起始空格和结尾空格
			macDataSource=macDataSource.trim();
			
			// 多于一个的连续空格，由一个空格代替
			//macDataSource=macDataSource.replaceAll("\\s{1,}", " ");// 
			//macDataSource=macDataSource.replaceAll(" ", "");// 
			System.out.println(macDataSource);
			String mainKey="1234567890ABCDEF1234567890ABCDEF";
			String strdata="1111111111111111";
			byte[] bt = DesUtil.desDecrypt_3(DesUtil.hexStringToBytes(strdata), DesUtil.hexStringToBytes(mainKey));
			String macKey = DesUtil.bytesToHexString(bt);	
			System.out.println(macKey);
			try {
				System.out.println(upGetMac(macDataSource, macKey));
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	   }


}
