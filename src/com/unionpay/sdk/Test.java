package com.unionpay.sdk;

import java.util.Date;

import com.unionpay.sdk.eunm.UnionPayValueType;
import com.unionpay.sdk.message.UnionPayMessage;
import com.unionpay.sdk.util.DesUtil;
import com.unionpay.sdk.util.HexBinary;
import com.unionpay.sdk.util.HexStringUtils;

public class Test {
	public static void main0100(String[] args) {
		UnionPayMessage message=UnionPayMessage.newMessagefrommsgType("0100");
		message.setValue(3, 310000, UnionPayValueType.NUMERIC, 6);
		message.setValue(7, new Date(), UnionPayValueType.DATE10, 0);
		message.setValue(11, 930908, UnionPayValueType.NUMERIC, 6);
		message.setValue(12, new Date(), UnionPayValueType.TIME, 0);
		message.setValue(13, new Date(), UnionPayValueType.DATE4, 0);
		message.setValue(15, new Date(), UnionPayValueType.DATE4, 0);
		message.setValue(18, 4814, UnionPayValueType.NUMERIC, 4);
		message.setValue(22, 012, UnionPayValueType.NUMERIC, 3);
		message.setValue(25, 92, UnionPayValueType.NUMERIC, 2);
		message.setValue(32, "00163000", UnionPayValueType.LLVAR, 0);
		message.setValue(33, "M0000202", UnionPayValueType.LLVAR, 0);
		message.setValue(37, "171608086527", UnionPayValueType.NUMERIC, 12);
		message.setValue(41, 2, UnionPayValueType.NUMERIC, 8);
		message.setValue(42, "300000054113002", UnionPayValueType.NUMERIC, 15);
		message.setValue(48, "FDDH18800000001         0000000042235#", UnionPayValueType.LLLVAR, 0);
		message.setValue(49, 156, UnionPayValueType.NUMERIC, 3);
		message.setValue(59, "BI2011110200#", UnionPayValueType.LLLVAR, 0);
		message.setValue(60, "00000000160000", UnionPayValueType.LLLVAR, 0);
		message.setValue(61, "999", UnionPayValueType.LLLVAR, 0);
		message.setValue(103, "DUZF3054", UnionPayValueType.LLLVAR, 0);
		message.setValue(128, "", UnionPayValueType.LLVAR, 0);
		
		
		String sendMsg=message.writeInternal();
		System.out.println(sendMsg);
		message.print(message);
		
		String recMsg=sendMsg;
		UnionPayMessage message1=UnionPayMessage.newMessagefrommsgType("0100");
		message1.parseMessage(recMsg.getBytes(),0);
		System.out.println("校验mac:"+message1.checkMacValue());
		message1.print(message1);

	}
	
	
	public static void main0800(String[] args) {
		UnionPayMessage message=UnionPayMessage.newMessagefrommsgType("0800");
		message.setValue(7, new Date(), UnionPayValueType.DATE10, 0);
		message.setValue(11, 930908, UnionPayValueType.NUMERIC, 6);
		message.setValue(33, "M0000202", UnionPayValueType.LLVAR, 0);
		message.setValue(53, "1000000000000000", UnionPayValueType.NUMERIC, 16);
		message.setValue(70, 101, UnionPayValueType.NUMERIC, 3);
		message.setValue(96, "s3fssgww366s4cer33ckj5k2m", UnionPayValueType.LLVAR, 0);
		message.setValue(128, "", UnionPayValueType.ALPHA, 16);
		
		String sendMsg=message.writeInternal();
		System.out.println(sendMsg);
		message.print(message);
		
		String recMsg=sendMsg;
		UnionPayMessage message1=UnionPayMessage.newMessagefrommsgType("0800");
		message1.parseMessage(recMsg.getBytes(),0);
		System.out.println("校验mac:"+message1.checkMacValue());
		message1.print(message1);
	}
	
	public static void main0810(String[] args) {
		UnionPayMessage message=UnionPayMessage.newMessagefrommsgType("0810");
		message.setValue(7, new Date(), UnionPayValueType.DATE10, 0);
		message.setValue(11, 930908, UnionPayValueType.NUMERIC, 6);
		message.setValue(33, "M0000202", UnionPayValueType.LLVAR, 0);
		message.setValue(39, "0F", UnionPayValueType.NUMERIC, 2);
		message.setValue(53, "1000000000000000", UnionPayValueType.NUMERIC, 16);
		message.setValue(70, 101, UnionPayValueType.NUMERIC, 3);
		message.setValue(96, "s3fssgww366s4cer33ckj5k2m", UnionPayValueType.LLVAR, 0);
		message.setValue(128, "2342342342342342342343", UnionPayValueType.LLVAR, 0);
		
		//print(message);
		System.out.println(message.writeInternal());
		
		UnionPayMessage message1=UnionPayMessage.newMessagefrommsgType("0810");
		message1.parseMessage(message.writeInternal().getBytes(),0);
		System.out.println("------");
		//print(message1);
	}
	
	public static void main0200(String[] args) {
		UnionPayMessage message=UnionPayMessage.newMessagefrommsgType("0200");
		
		message.setValue(2,"6221235200000000001", UnionPayValueType.LLVAR, 0);
		message.setValue(3, "190000",UnionPayValueType.NUMERIC, 6);
		message.setValue(4, "4993",UnionPayValueType.NUMERIC, 12);		
		message.setValue(7, "1031220015",UnionPayValueType.DATE10, 0);
		message.setValue(11,"128973",UnionPayValueType.NUMERIC, 6);
		message.setValue(12, "220015",UnionPayValueType.TIME, 0);
		message.setValue(13, "1025",UnionPayValueType.DATE4, 0);
		message.setValue(15,"1025",UnionPayValueType.DATE4, 0);
		message.setValue(18,"4814", UnionPayValueType.NUMERIC, 4);
		message.setValue(22,"012", UnionPayValueType.NUMERIC, 3);
		message.setValue(25,"81", UnionPayValueType.NUMERIC, 2);
		message.setValue(32, "48021910",UnionPayValueType.LLVAR, 0);
		message.setValue(33,"M0000184", UnionPayValueType.LLVAR, 0);
		message.setValue(37, "181031220015",UnionPayValueType.NUMERIC, 12);
		message.setValue(41, "01007611",UnionPayValueType.NUMERIC, 8);
		message.setValue(42, "898150148141650",UnionPayValueType.NUMERIC, 15);
		message.setValue(48, "PAI013621775132         000000006000050#",UnionPayValueType.LLLVAR, 0);
		message.setValue(49, "156",UnionPayValueType.NUMERIC, 3);
		message.setValue(60, "00000000030000",UnionPayValueType.LLLVAR, 0);
		message.setValue(103, "000M0000184",UnionPayValueType.LLVAR, 0);
		message.setValue(128, "",UnionPayValueType.LLVAR, 0);
		
		String sendStr=message.writeInternal();
		System.out.println(sendStr);
		
		//print(message);
		
		/*String receiveMsg="02610200F23A448188C180100000000002000001196221235200000000001190000000000004993103122001512897322001510251025481401281084802191008M000018418103122001501007611898150148141650040PAI013621775132         000000006000050#1560140000000003000011000M0000184694AC4AF4F2D6010";
		UnionPayMessage message1=UnionPayMessage.newMessagefrommsgType("0200");
		message1.parseMessage(receiveMsg,0);
		System.out.println("------");
		print(message1);
		
		System.out.println("mac校验："+message1.checkMacValue());*/
	}
	public static void main0210(String[] args) {
		UnionPayMessage message=UnionPayMessage.newMessagefrommsgType("0210");
		
		message.setValue(2,"6221235200000000001", UnionPayValueType.LLVAR, 0);
		message.setValue(3, "190000",UnionPayValueType.NUMERIC, 6);
		message.setValue(4, "4993",UnionPayValueType.NUMERIC, 12);		
		message.setValue(7, "1031220015",UnionPayValueType.DATE10, 0);
		message.setValue(11,"128973",UnionPayValueType.NUMERIC, 6);
		message.setValue(12, "220015",UnionPayValueType.TIME, 0);
		message.setValue(13, "1025",UnionPayValueType.DATE4, 0);
		message.setValue(15,"1025",UnionPayValueType.DATE4, 0);
		message.setValue(18,"4814", UnionPayValueType.NUMERIC, 4);
		message.setValue(25,"81", UnionPayValueType.NUMERIC, 2);
		message.setValue(32, "48021910",UnionPayValueType.LLVAR, 0);
		message.setValue(33,"M0000184", UnionPayValueType.LLVAR, 0);
		message.setValue(38, "000000",UnionPayValueType.NUMERIC, 6);
		message.setValue(39, "00",UnionPayValueType.ALPHA, 2);
		message.setValue(41, "01007611",UnionPayValueType.NUMERIC, 8);
		message.setValue(42, "898150148141650",UnionPayValueType.NUMERIC, 15);
		message.setValue(48, "PA086221235200000000001 000000#",UnionPayValueType.LLLVAR, 0);
		message.setValue(49, "156",UnionPayValueType.NUMERIC, 3);
		message.setValue(60, "00000000030000",UnionPayValueType.LLLVAR, 0);
		message.setValue(63, "999",UnionPayValueType.LLLVAR, 0);
		message.setValue(103, "000M0000184",UnionPayValueType.LLVAR, 0);
		message.setValue(128, "",UnionPayValueType.LLVAR, 0);
		
		String sendStr=message.writeInternal();
		System.out.println(sendStr);
		
		message.print(message);
		
		String receiveMsg=sendStr;
		UnionPayMessage message1=UnionPayMessage.newMessagefrommsgType("0210");
		message1.parseMessage(receiveMsg.getBytes(),0);
		message1.print(message1);
		
		System.out.println("mac校验："+message1.checkMacValue());
	}
	
	public static void main(String[] args) {
		String msg="F23A40818AC184300000000002000001";
		byte[] byts=DesUtil.hexStringToBytes(msg);
		System.out.println(byts.length);
		for (int i = 0; i < byts.length; i++) {
			System.out.print(byts[i]+" ");
		}
		System.out.println(DesUtil.bytesToHexString(byts));
		
	}

}
