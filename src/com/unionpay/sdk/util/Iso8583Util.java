package com.unionpay.sdk.util;

import java.util.ArrayList;
import java.util.List;

/**
 * 位图生成与解析工具
 *
 */
public class Iso8583Util {


	/**
	 * 初始64和128域
	 * 
	 * @param d
	 * @return
	 * @throws Exception
	 */
	public static String initBitMap(int d) throws Exception {

		StringBuffer bf = new StringBuffer();
		if (d == 64 || d == 128) {
			for (int i = 0; i < d; i++) {
				bf.append("0");
			}
			if (d == 128) {
				bf.replace(0, 1, "1");
			}
		}else{
			throw new Exception("只能是64或者是128域初始化！");
		}
		return bf.toString();
	}

	public static String bitMapFormat(List<Integer> list) throws Exception {
		String res = "0000000000000000";
		// 默认域长度
		int defultMap = 64;
		// 注意，初始域图默认为128域
		String bitMaps = initBitMap(128);
		if (list != null) {
			for (int i : list) {
				if (i > 64) {
					defultMap = 128;
				}
				if (i > 128) {
					throw new Exception("不支持的域生成！");
				}
				bitMaps = change16bitMapFlag(i, bitMaps);
				res = getBitMapDataSource(defultMap, bitMaps);
			}
			if (defultMap == 64) {
				String btm64 = "0" + bitMaps.substring(0, 64).substring(1, bitMaps.substring(0, 64).length());
				bitMaps = btm64;
			}
			res = getBitMapDataSource(defultMap, bitMaps);
		}
		return res.toUpperCase();
	}

	/**
	 * 改变128位图中的标志为1
	 * 
	 * @param fieldNo
	 * @param res
	 * @return
	 */
	public static String change16bitMapFlag(int indexNo, String res) {
		res = res.substring(0, indexNo - 1) + "1" + res.substring(indexNo);
		return res;
	}

	/**
	 * 二进制字符串转十六进制
	 * 
	 * @param c
	 * @param bitMap
	 * @return
	 */
	public static String getBitMapDataSource(int c, String bitMap) {
		// 注意每4个二进制表示一位16进制
		int s = c / 4;
		StringBuffer bf = new StringBuffer();
		for (int i = 0; i < s; i++) {
			String two = bitMap.substring(i * 4, i * 4 + 4);
			if("0000".equals(two)) {
				bf.append("0");
			}else if("0001".equals(two)) {
				bf.append("1");
			}else if("0010".equals(two)) {
				bf.append("2");
			}else if("0011".equals(two)) {
				bf.append("3");
			}else if("0100".equals(two)) {
				bf.append("4");
			}else if("0101".equals(two)) {
				bf.append("5");
			}else if("0110".equals(two)) {
				bf.append("6");
			}else if("0111".equals(two)) {
				bf.append("7");
			}else if("1000".equals(two)) {
				bf.append("8");
			}else if("1001".equals(two)) {
				bf.append("9");
			}else if("1010".equals(two)) {
				bf.append("a");
			}else if("1011".equals(two)) {
				bf.append("b");
			}else if("1100".equals(two)) {
				bf.append("c");
			}else if("1101".equals(two)) {
				bf.append("d");
			}else if("1110".equals(two)) {
				bf.append("e");
			}else if("1111".equals(two)) {
				bf.append("f");
			}
		}
		return bf.toString();
	}
	
	
	/**
	 * 根据bitMap解析具体的域
	 * @param bitMapSource
	 * @return
	 */
	public static List<Integer> getBitMapNum(String bitMapSource){
		if(bitMapSource.contains(" ")){
			bitMapSource = bitMapSource.replaceAll(" ", "");
		}
		 List<Integer> list = new ArrayList<Integer>();
		//先转16位二进制
		String bits = hexStrToBinaryStr(bitMapSource);
		char[] strChar = bits.toCharArray();

		for(int i=0;i<strChar.length;i++){
			String s=String.valueOf(strChar[i]) ;
			if(s.equals("1")){
				list.add(i+1);
			}
		}
		return list;
	}

	

	/**
	 * 将十六进制的字符串转换成二进制的字符串
	 * 适用于转换bitMap
	 * @param hexString
	 * @return
	 */
	public static String hexStrToBinaryStr(String hexString) {
		if (hexString == null || hexString.equals("")) {
			return null;
		}
		StringBuffer sb = new StringBuffer();
		// 将每一个十六进制字符分别转换成一个四位的二进制字符
		for (int i = 0; i < hexString.length(); i++) {
			String indexStr = hexString.substring(i, i + 1);
			String binaryStr = Integer.toBinaryString(Integer.parseInt(indexStr, 16));
			while (binaryStr.length() < 4) {
				binaryStr = "0" + binaryStr;
			}
			sb.append(binaryStr);
		}

		return sb.toString();
	}

	public static void main(String[] args) throws Exception {
		System.out.println("64位图初始:" + initBitMap(64));
		System.out.println("128位图初始:" + initBitMap(128));
		//假设要在12467域填充数据
		List<Integer> l =new ArrayList<>();
		l.add(1);
		l.add(2);
		l.add(4);
		l.add(6);
		l.add(7);
		System.out.println("生成64域位图：" + bitMapFormat(l));
		//如果某个域大于64会生成16字节的位图
		List<Integer> l2 =new ArrayList<>();
		l2.add(1);
		l2.add(11);
		l2.add(12);
		l2.add(13);
		l2.add(49);
		l2.add(59);
		l2.add(60);
		l2.add(61);    
		l2.add(103);
		l2.add(128); 
		System.out.println("生成128域位图：" + bitMapFormat(l2));
		
		String btm ="00 00 44 00 08 00 21 00";
		System.out.println("解析64域图："+getBitMapNum(btm));
		
		String btm2="D6000000000000000010420200001521";
		System.out.println("解析128域图："+getBitMapNum(btm2));
	}

}
