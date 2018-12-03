package com.unionpay.sdk.service;

/**
 * 　域11  系统跟踪号 生成器
 *
 *
 */
public interface SystemTraceNumGenerator {

	/** 
	 * 返回下一个系统跟踪号
	 *  */
	public int nextTrace();

	/** 
	 * 返回生成的最后的一个跟踪号 
	 * 
	 * */
	public int getLastTrace();

}