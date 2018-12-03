package com.unionpay.sdk.service.impl;

import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.Map;

import com.unionpay.sdk.eunm.UnionPayValueType;
import com.unionpay.sdk.message.UnionPayMessage;
import com.unionpay.sdk.service.UnionPayBM;

public class UnionPayBM080010 implements UnionPayBM {

	@Override
	public Map<String, Object> perform(UnionPayMessage message) {
		System.out.println("进入UnionPayBM080010处理类");
		Map<String, Object> map = new HashMap<>();
		// 初始化返回数据
		UnionPayMessage retMsg = UnionPayMessage.newMessagefrommsgType("0810");
		retMsg.setValue(7, message.getField(7), UnionPayValueType.DATE10, 0);
		retMsg.setValue(11, message.getField(11), UnionPayValueType.NUMERIC, 6);
		retMsg.setValue(33, message.getField(33), UnionPayValueType.LLVAR, 0);
		retMsg.setValue(53, message.getField(53), UnionPayValueType.NUMERIC, 16);
		retMsg.setValue(70, 101, UnionPayValueType.NUMERIC, 3);
		retMsg.setValue(128, "", UnionPayValueType.ALPHA, 16);

		try {
			if (message.checkMacValue()) {
				retMsg.setValue(39, "00", UnionPayValueType.NUMERIC, 2);
				// 保存mackey至本地文件
				FileOutputStream fout = new FileOutputStream("mackey.txt");
				fout.write(message.getField(96).toString().getBytes());
				fout.close();

			} else {
				System.out.println("校验mac未通过");
				retMsg.setValue(39, "F1", UnionPayValueType.NUMERIC, 2);

			}

		} catch (Exception e) {
			retMsg.setValue(39, "F1", UnionPayValueType.NUMERIC, 2);
			e.printStackTrace();
		} finally {			
			map.put("code", "00");
			map.put("msg", retMsg.writeInternal());
			return map;
		}

	}

}
