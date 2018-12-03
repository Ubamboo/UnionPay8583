package com.unionpay.sdk;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.unionpay.sdk.eunm.UnionPayValueType;
import com.unionpay.sdk.message.UnionPayFieldParseInfo;
import com.unionpay.sdk.util.HexBinary;
import com.unionpay.sdk.util.Iso8583Util;

public class Test01 {
	private static  Map<Integer, UnionPayFieldParseInfo> parseGuide = new HashMap<>();
	private static boolean isbinary;
	
	public static void main(String[] args) {
		byte[] bitByte=HexBinary.decode("82200000800008000400000100000001");
		for (byte b : bitByte) {
			System.out.print(b+" ");
		}
		
		byte[] pHexBinary=new byte[1];
		pHexBinary[0]=-126;
		System.out.println(HexBinary.encode(pHexBinary));
		String pValue="3030383130383030822000008000080004000001000000013131323331313533333331353637313030383130303030303031323030303030303030303030303030303130316A5D90C152DF277825D7E3EBF28280CD";
		byte[]  data=HexBinary.decode(pValue);
		for (byte b : data) {
			System.out.print(b+" ");
		}
		System.out.println();
		byte[] buf = data;
		int pos = 0;
		int length = ((buf[pos++] & 0x0f) * 1000) + ((buf[pos++] & 0x0f) * 100) + (((buf[pos++] & 0x0f)) * 10)
				+ (buf[pos++] & 0x0f);
		System.out.println("得到报文长度：" + length);
		System.out.println("pos:"+pos);
		byte[] mystype=new byte[4];
		System.arraycopy(buf, pos, mystype, 0, 4);	
		pos+=4;
		String msgType = new String(mystype);// 消息类型		
		System.out.println(msgType);
		System.out.println("pos:"+pos);
		
		byte[]bitMap=getBitMapStr(buf, pos);
		String bitMapSource=HexBinary.encode(bitMap);
		System.out.println("解析得到位图数据:" + bitMapSource);
		// 更新取值位置
		pos += bitMap.length;
		
		System.out.println(msgType + "," + bitMapSource);
		parseGuide = initParseGuide();
		List<Integer> bitMapList = Iso8583Util.getBitMapNum(bitMapSource);
		for (Integer index : bitMapList) {
			
			UnionPayFieldParseInfo fpi = parseGuide.get(index);
			if (fpi != null) {
				System.out.println(index);
				try {
					UnionPayValue val = isbinary ? fpi.parseBinary(buf, pos) : fpi.parse(buf, pos);
					System.out.println(val);
					if (isbinary && !(val.getType() == UnionPayValueType.ALPHA
							|| val.getType() == UnionPayValueType.LLVAR || val.getType() == UnionPayValueType.LLLVAR)) {
						pos += (val.getLength() / 2) + (val.getLength() % 2);
					} else {
						pos += val.getLength();
					}
					// 跳过可变长度域的长度标识
					if (val.getType() == UnionPayValueType.LLVAR) {
						pos += isbinary ? 1 : 2;
					} else if (val.getType() == UnionPayValueType.LLLVAR) {
						pos += isbinary ? 2 : 3;
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

		}
		
		
		System.out.println("---------------------mac----");
		byte[] dataBuf =data;
		int index = 0;
		int dataLength = ((dataBuf[index++] & 0x0f) * 1000) + ((dataBuf[index++] & 0x0f) * 100) + (((dataBuf[index++] & 0x0f)) * 10)
				+ (dataBuf[index++] & 0x0f);
		System.out.println("得到报文长度：" + dataLength);
		
		int maclength=8;
		byte[] macSrc=new byte[dataLength-maclength];
				
		System.arraycopy(dataBuf, index, macSrc, 0, dataLength-maclength);	
		
		System.out.println(HexBinary.encode(macSrc));
		
	}
	
	public static byte[] getBitMapStr(byte[] recvsData, int pos) {
		byte[] bitMap=new byte[8];
		bitMap[0]=recvsData[pos];
		
		String bitMapFirstStr = HexBinary.encode(bitMap);
		char[] strChar = Iso8583Util.hexStrToBinaryStr(bitMapFirstStr).toCharArray();
		
		if (strChar[0] == '1') {
			bitMap=new byte[16];
		} 
		System.arraycopy(recvsData, pos, bitMap, 0, bitMap.length);	
		return bitMap;

	}
	public static  Map<Integer, UnionPayFieldParseInfo> initParseGuide() {
		parseGuide.put(7, new UnionPayFieldParseInfo(UnionPayValueType.DATE10, 0));
		parseGuide.put(11, new UnionPayFieldParseInfo(UnionPayValueType.NUMERIC, 6));
		parseGuide.put(33, new UnionPayFieldParseInfo(UnionPayValueType.LLVAR, 0));
		parseGuide.put(53, new UnionPayFieldParseInfo(UnionPayValueType.NUMERIC, 16));
		parseGuide.put(70, new UnionPayFieldParseInfo(UnionPayValueType.NUMERIC, 3));
		parseGuide.put(96, new UnionPayFieldParseInfo(UnionPayValueType.BINARY, 8));
		parseGuide.put(128, new UnionPayFieldParseInfo(UnionPayValueType.BINARY, 8));
		return parseGuide;
	}
}
