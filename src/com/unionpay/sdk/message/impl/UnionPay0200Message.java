package com.unionpay.sdk.message.impl;

import static java.lang.System.arraycopy;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import com.unionpay.sdk.UnionPayValue;
import com.unionpay.sdk.eunm.UnionPayValueType;
import com.unionpay.sdk.message.UnionPayFieldParseInfo;
import com.unionpay.sdk.message.UnionPayMessage;
import com.unionpay.sdk.util.DesUtil;
import com.unionpay.sdk.util.HexBinary;
import com.unionpay.sdk.util.HexStringUtils;
import com.unionpay.sdk.util.Iso8583Util;
import com.unionpay.sdk.util.UnionPayMacUtil;

public class UnionPay0200Message extends UnionPayMessage {
	private byte[] recvsData;
	private boolean isbinary;
	private Map<Integer, UnionPayFieldParseInfo> parseGuide=new HashMap<>();
	/**
	 * 消息域
	 */
	private Map<Integer, UnionPayValue<?>> fields = new ConcurrentHashMap<Integer, UnionPayValue<?>>();

	@Override
	public Object getObjectValue(int fieldid) {
		UnionPayValue<?> v = fields.get(fieldid);
		if (v == null) {
			return null;
		}
		return v.getValue();
	}

	@Override
	public void setField(int fieldId, UnionPayValue<?> field) {
		if (fieldId < 0 || fieldId > 128) {
			throw new IndexOutOfBoundsException("Field index must be between 0 and 128");
		}
		if (field == null) {
			fields.remove(fieldId);
		} else {
			fields.put(fieldId, field);
		}
	}

	@Override
	public void setValue(int fieldId, Object value, UnionPayValueType t, int length) {

		if (fieldId < 0 || fieldId > 128) {
			throw new IndexOutOfBoundsException("Field index must be between 0 and 128");
		}
		if (value == null) {
			fields.remove(fieldId);
		} else {
			UnionPayValue v = null;
			if (t.needsLength()) {
				v = new UnionPayValue<Object>(t, value, length);
			} else {
				v = new UnionPayValue<Object>(t, value);
			}
			fields.put(fieldId, v);
		}

	}

	@Override
	public boolean hasField(int fieldid) {
		return fields.get(fieldid) != null;
	}

	@Override
	public UnionPayValue<?> getField(int fieldid) {
		return fields.get(fieldid);
	}

	@Override
	public String serverCode() {
		if(fields.get(3)==null)
			return "";
		return fields.get(3).toString();
	}
	@Override
	public byte[] getReceData() {
		return this.recvsData;
	}

	@Override
	public boolean checkMacValue() {

		String mac = upGetMac(recvsData);
		String receiveMac = fields.get(128).toString();
		if (mac.equals(receiveMac)) {
			return true;
		}
		return false;
	}

	
	
	@Override
	public String writeInternal() {
		ByteArrayOutputStream bout = new ByteArrayOutputStream();
				
		// 1、msgtype
		try {
			bout.write("0200".getBytes());
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		// 2、Bitmap
		ArrayList<Integer> keys = new ArrayList<Integer>();
		keys.addAll(fields.keySet());
		Collections.sort(keys);
		String bitMap="";
		try {
			 bitMap=Iso8583Util.bitMapFormat(keys);
			bout.write(HexBinary.decode(bitMap));
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		// 3、Fields
		for (Integer i : keys) {
			if(i==128) {
				continue;
			}
			UnionPayValue v = fields.get(i);			
			try {				
				v.write(bout, isbinary);
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
		//4、计算mac
		StringBuilder macDataSour=new StringBuilder();
		// 存储转为16进制数据
		macDataSour.append(HexStringUtils.toHex("0200"));	
		fields.put(0, new UnionPayValue<Object>(UnionPayValueType.NUMERIC, "0200", 4));
		// 生成bitmap时已经转16进制，此处不需要再转换
		macDataSour.append(bitMap);
		fields.put(1, new UnionPayValue<Object>(UnionPayValueType.ALPHA, bitMap, bitMap.length()));
		
		String macdata=bout.toString().substring(4+bitMap.length(), bout.size());
		macDataSour.append(HexStringUtils.toHex(macdata));
		
		String strkey = "1234567890ABCDEF1234567890ABCDEF";
		String strdata = "1111111111111111";
		byte[] bt = DesUtil.desDecrypt_3(DesUtil.hexStringToBytes(strdata), DesUtil.hexStringToBytes(strkey));
		String macKey = DesUtil.bytesToHexString(bt);
		
		try {
			String mac=UnionPayMacUtil.upGetMac(macDataSour.toString(), macKey);
			fields.put(128, new UnionPayValue<Object>(UnionPayValueType.ALPHA, mac, mac.length()));
			bout.write(mac.getBytes());
		} catch (Exception e) {
			e.printStackTrace();
		}
			
		
		// 5、报文长度
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		int size=bout.size();	
		
		out.write((size / 1000) + 48);
		out.write(((size % 1000) / 100) + 48);
		out.write(((size % 100) / 10) + 48);
		out.write((size % 10) + 48);	
		
		
		return  HexBinary.encode(out.toByteArray())+HexBinary.encode(bout.toByteArray());
	}

	/**8583报文
	 * 解析
	 */
	@Override
    public void parseMessage(byte[] recvsData,int msgheaderlength) {
		setRecvsData(recvsData);
		// 根据报文长度获取数据
		byte[] buf = recvsData;
		int pos = 0;
		int length = ((buf[pos++] & 0x0f) * 1000) + ((buf[pos++] & 0x0f) * 100) + (((buf[pos++] & 0x0f)) * 10)
				+ (buf[pos++] & 0x0f);
		System.out.println("得到报文长度：" + length);
		
		// 获取消息类型，将数据指针后移
		byte[] mystype=new byte[4];
		System.arraycopy(buf, pos, mystype, 0,mystype.length);	
		pos+=4;
		String msgType = new String(mystype);// 消息类型	
		this.setField(0, new UnionPayValue<Object>(UnionPayValueType.LLVAR, msgType));
		
		// 计算获取位图
		byte[] bitMap=getBitMapStr(buf, pos);
		String bitMapSource=HexBinary.encode(bitMap);
		System.out.println("解析得到位图数据:" + bitMapSource);
		// 更新取值位置
		pos += bitMap.length;
		
		this.setField(1, new UnionPayValue<Object>(UnionPayValueType.LLVAR, bitMapSource));

		System.out.println(msgType + "," + bitMapSource);
		parseGuide = this.initParseGuide();
		List<Integer> bitMapList = Iso8583Util.getBitMapNum(bitMapSource);
		for (Integer index : bitMapList) {
			UnionPayFieldParseInfo fpi = parseGuide.get(index);
			if (fpi != null) {
				try {
					UnionPayValue val = isbinary ? fpi.parseBinary(buf, pos) : fpi.parse(buf, pos);
					this.setField(index, val);
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

	}
	/**
	 * 初始化解析域
	 * @return
	 */
	private Map<Integer, UnionPayFieldParseInfo> initParseGuide() {
		parseGuide.put(2, new UnionPayFieldParseInfo(UnionPayValueType.LLVAR, 0));
		parseGuide.put(3, new UnionPayFieldParseInfo(UnionPayValueType.NUMERIC, 6));
		parseGuide.put(4, new UnionPayFieldParseInfo(UnionPayValueType.NUMERIC, 12));		
		parseGuide.put(7, new UnionPayFieldParseInfo(UnionPayValueType.DATE10, 0));
		parseGuide.put(11,new UnionPayFieldParseInfo(UnionPayValueType.NUMERIC, 6));
		parseGuide.put(12, new UnionPayFieldParseInfo(UnionPayValueType.TIME, 0));
		parseGuide.put(13,new UnionPayFieldParseInfo( UnionPayValueType.DATE4, 0));
		parseGuide.put(15,new UnionPayFieldParseInfo(UnionPayValueType.DATE4, 0));
		parseGuide.put(18, new UnionPayFieldParseInfo(UnionPayValueType.NUMERIC, 4));
		parseGuide.put(22, new UnionPayFieldParseInfo(UnionPayValueType.NUMERIC, 3));
		parseGuide.put(25, new UnionPayFieldParseInfo(UnionPayValueType.NUMERIC, 2));
		parseGuide.put(32, new UnionPayFieldParseInfo(UnionPayValueType.LLVAR, 0));
		parseGuide.put(33, new UnionPayFieldParseInfo(UnionPayValueType.LLVAR, 0));
		parseGuide.put(37, new UnionPayFieldParseInfo(UnionPayValueType.NUMERIC, 12));
		parseGuide.put(41, new UnionPayFieldParseInfo(UnionPayValueType.NUMERIC, 8));
		parseGuide.put(42, new UnionPayFieldParseInfo(UnionPayValueType.NUMERIC, 15));
		parseGuide.put(48, new UnionPayFieldParseInfo(UnionPayValueType.LLLVAR, 0));
		parseGuide.put(49, new UnionPayFieldParseInfo(UnionPayValueType.NUMERIC, 3));
		parseGuide.put(54, new UnionPayFieldParseInfo(UnionPayValueType.LLLVAR, 0));		
		parseGuide.put(59, new UnionPayFieldParseInfo(UnionPayValueType.LLLVAR, 0));
		parseGuide.put(60, new UnionPayFieldParseInfo(UnionPayValueType.LLLVAR, 0));
		parseGuide.put(63, new UnionPayFieldParseInfo( UnionPayValueType.LLLVAR, 0));
		parseGuide.put(100, new UnionPayFieldParseInfo( UnionPayValueType.LLLVAR, 0));
		parseGuide.put(103, new UnionPayFieldParseInfo(UnionPayValueType.LLVAR, 0));
		parseGuide.put(128,new UnionPayFieldParseInfo( UnionPayValueType.ALPHA, 16));
		return parseGuide;
	}

	private void UnionPay0100Message() {

	}

	public void setBinary(boolean flag) {
		isbinary = flag;
	}

	public boolean isBinary() {
		return isbinary;
	}

	public byte[] getRecvsData() {
		return recvsData;
	}

	public void setRecvsData(byte[] recvsData) {
		this.recvsData = recvsData;
	}
	

	

}
