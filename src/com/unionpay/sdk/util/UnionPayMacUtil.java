package com.unionpay.sdk.util;

import javax.crypto.Cipher;


public class UnionPayMacUtil {

	 /**
    * ����ͨ�ŵ�MAC
    * @param buf
    * @param datasize
    * @param mackey
    * @return
	 * @throws Exception 
    */
   public static String upGetMac( String macDataSource,String macKey) throws Exception{
   	 // ���MAC����Դ��ÿ��16λhex(8 byte())  
       String[] ds = splitData(macDataSource);
      
       // �����Ų�mac
       System.out.println("---��������mac�� hex����-----");
       for (int i = 0; i < ds.length; i++) {
    	   System.out.println(ds[i]);		
       }
       
       System.out.println("---��������mac�� hex����-----");
       
       String des = "";  
       
       for (int i = 0; i < ds.length; i++){  
           if (i == 0){  
               // ��һ��ֻ��DES����  
               des = DesUtil.decEncNoPaddingDES(macKey, ds[i], Cipher.ENCRYPT_MODE).toUpperCase();  
           } else{  
               // ����һ�� DES���ܽ���� �� i �����������  
               des = StringUtils.XOR(des, ds[i]);  
               // �������������DES����  
               des = DesUtil.decEncNoPaddingDES(macKey, des, Cipher.ENCRYPT_MODE).toUpperCase();  
           }  
       }  
       
       return des;
   }
   
   /*  
    * ��hexMacDataSource���з��� ÿ 16 �ַ� 8byte һ��  
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
               throw new IllegalArgumentException("�������ݷǷ�!");  
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
	   	
	   	// �������֮�����һ���ո�
			String macDataSource=m0+spiltStr+m1+spiltStr+m2+spiltStr+m3+spiltStr+m4+spiltStr+m7+spiltStr+m11+spiltStr+m12;
			
			//���е�Сд��ĸת���ɴ�д��ĸ
			macDataSource=macDataSource.toUpperCase();
			//  ������ĸ(A-Z)������(0-9)���ո񣬶���(��)�͵��(.)������ַ���ɾȥ
			//macDataSource = macDataSource.replaceAll("[^A-Z0-9., ]", "");
			//ɾȥ���������ʼ�ո�ͽ�β�ո�
			macDataSource=macDataSource.trim();
			
			// ����һ���������ո���һ���ո����
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
