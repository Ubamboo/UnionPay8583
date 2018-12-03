package com.unionpay.sdk.service;

import java.util.Map;

import com.unionpay.sdk.message.UnionPayMessage;

public interface UnionPayBM {
	
	public Map<String, Object> perform(UnionPayMessage message);
}
