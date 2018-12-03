package com.unionpay.sdk.message;

import java.io.FileInputStream;

import com.unionpay.sdk.UnionPayValue;
import com.unionpay.sdk.eunm.UnionPayValueType;
import com.unionpay.sdk.util.DesUtil;
import com.unionpay.sdk.util.HexBinary;
import com.unionpay.sdk.util.HexStringUtils;
import com.unionpay.sdk.util.Iso8583Util;
import com.unionpay.sdk.util.UnionPayMacUtil;

/**
 * 银联8583 消息类
 * 
 * @author zqq
 *
 */
public abstract class UnionPayMessage {

	public static String mainKey = "0123456789ABCDEF0123456789ABCDEF"; // 主秘钥
	/**
	 * 消息类型
	 */
	private String msgType;

	/**
	 * Returns the stored value in the field, without converting or formatting it.
	 * 
	 * @param fieldid
	 *            The field number. 1 is the secondary bitmap and is not returned as
	 *            such; real fields go from 2 to 128.
	 */
	public abstract Object getObjectValue(int fieldid);

	/**
	 * Returns the cnValue for the specified field.
	 * 
	 * @param fieldid
	 *            应该在2-128范围
	 */
	public abstract UnionPayValue<?> getField(int fieldid);

	/**
	 * Stored the field in the specified index. The first field is the secondary
	 * bitmap and has index 1, so the first valid value for index must be 2.
	 */
	public abstract void setField(int fieldid, UnionPayValue<?> field);

	/**
	 * Sets the specified value in the specified field, creating an cnValue
	 * internally.
	 * 
	 * @param fieldid
	 *            The field number (2 to 128)
	 * @param value
	 *            The value to be stored.
	 * @param t
	 *            The 8583 cntype.
	 * @param length
	 *            The length of the field, used for ALPHA and NUMERIC values only,
	 *            ignored with any other type.
	 */
	public abstract void setValue(int fieldid, Object value, UnionPayValueType t, int length);

	/**
	 * Returns true is the message has a value in the specified field.
	 * 
	 * @param fieldid
	 *            The field id.
	 */
	public abstract boolean hasField(int fieldid);

	public abstract boolean checkMacValue();

	public abstract String writeInternal();

	public abstract void parseMessage(byte[] recvsData, int msgheaderlength);

	public abstract byte[] getReceData();

	public abstract String serverCode();

	public static UnionPayMessage newMessagefrommsgType(String msgType) {
		String className = "com.unionpay.sdk.message.impl.UnionPay" + msgType + "Message";
		try {
			Class classType = Class.forName(className);
			return (UnionPayMessage) classType.newInstance();
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("业务未开通：" + msgType);
		}
		return null;

	}

	public byte[] getBitMapStr(byte[] recvsData, int pos) {
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

	public String upGetMac(byte[] recvsData) {

		try {
			
			// 根据报文长度获取数据
			byte[] buf =recvsData;
			int pos = 0;
			int length = ((buf[pos++] & 0x0f) * 1000) + ((buf[pos++] & 0x0f) * 100) + (((buf[pos++] & 0x0f)) * 10)
					+ (buf[pos++] & 0x0f);
			System.out.println("得到报文长度：" + length);
			
			int maclength=8;
			byte[] macSrc=new byte[length-maclength];
					
			System.arraycopy(buf, pos, macSrc, 0,macSrc.length);	
			
			String hexEncodeStr=HexBinary.encode(macSrc);
			
			// 读取mac工作秘钥
			StringBuilder sb = new StringBuilder();
			FileInputStream fin = new FileInputStream("mackey.txt");
			byte[] readBuf = new byte[1024];
			int len = 0;
			while ((len = fin.read(readBuf)) != -1) {
				sb.append(new String(readBuf, 0, len, "UTF-8"));
			}

			String mainKey = "1234567890ABCDEF1234567890ABCDEF";
			String macKey = "1111111111111111";
			System.out.println("macKey:"+macKey);
			byte[] bt = DesUtil.desDecrypt_3(DesUtil.hexStringToBytes(macKey), DesUtil.hexStringToBytes(mainKey));
			String desMacKey = DesUtil.bytesToHexString(bt);
			System.out.println("desMacKey:"+desMacKey);
			
			String mac = UnionPayMacUtil.upGetMac(hexEncodeStr, desMacKey);
			return mac;

		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("校验MAC异常，返回false");
		}
		return "";

	}

	// 输出一个报文内容
	public void print(UnionPayMessage m) {
		System.out.println("----------------------------------------------------- ");
		for (int i = 0; i <= 128; i++) {
			if (m.hasField(i)) {
				System.out.println("FieldID: " + i + " <" + m.getField(i).getType() + ">\t[" + m.getObjectValue(i) + "]"
						+ "\t[" + m.getField(i).toString() + "]");
			}
		}
	}

}
