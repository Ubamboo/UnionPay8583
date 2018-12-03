package com.unionpay.sdk.util;

import java.util.ArrayList;
import java.util.List;

/**
 * λͼ�������������
 *
 */
public class Iso8583Util {


	/**
	 * ��ʼ64��128��
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
			throw new Exception("ֻ����64������128���ʼ����");
		}
		return bf.toString();
	}

	public static String bitMapFormat(List<Integer> list) throws Exception {
		String res = "0000000000000000";
		// Ĭ���򳤶�
		int defultMap = 64;
		// ע�⣬��ʼ��ͼĬ��Ϊ128��
		String bitMaps = initBitMap(128);
		if (list != null) {
			for (int i : list) {
				if (i > 64) {
					defultMap = 128;
				}
				if (i > 128) {
					throw new Exception("��֧�ֵ������ɣ�");
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
	 * �ı�128λͼ�еı�־Ϊ1
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
	 * �������ַ���תʮ������
	 * 
	 * @param c
	 * @param bitMap
	 * @return
	 */
	public static String getBitMapDataSource(int c, String bitMap) {
		// ע��ÿ4�������Ʊ�ʾһλ16����
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
	 * ����bitMap�����������
	 * @param bitMapSource
	 * @return
	 */
	public static List<Integer> getBitMapNum(String bitMapSource){
		if(bitMapSource.contains(" ")){
			bitMapSource = bitMapSource.replaceAll(" ", "");
		}
		 List<Integer> list = new ArrayList<Integer>();
		//��ת16λ������
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
	 * ��ʮ�����Ƶ��ַ���ת���ɶ����Ƶ��ַ���
	 * ������ת��bitMap
	 * @param hexString
	 * @return
	 */
	public static String hexStrToBinaryStr(String hexString) {
		if (hexString == null || hexString.equals("")) {
			return null;
		}
		StringBuffer sb = new StringBuffer();
		// ��ÿһ��ʮ�������ַ��ֱ�ת����һ����λ�Ķ������ַ�
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
		System.out.println("64λͼ��ʼ:" + initBitMap(64));
		System.out.println("128λͼ��ʼ:" + initBitMap(128));
		//����Ҫ��12467���������
		List<Integer> l =new ArrayList<>();
		l.add(1);
		l.add(2);
		l.add(4);
		l.add(6);
		l.add(7);
		System.out.println("����64��λͼ��" + bitMapFormat(l));
		//���ĳ�������64������16�ֽڵ�λͼ
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
		System.out.println("����128��λͼ��" + bitMapFormat(l2));
		
		String btm ="00 00 44 00 08 00 21 00";
		System.out.println("����64��ͼ��"+getBitMapNum(btm));
		
		String btm2="D6000000000000000010420200001521";
		System.out.println("����128��ͼ��"+getBitMapNum(btm2));
	}

}
