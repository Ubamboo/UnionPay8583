package com.unionpay.sdk.service;

/**
 * ����11  ϵͳ���ٺ� ������
 *
 *
 */
public interface SystemTraceNumGenerator {

	/** 
	 * ������һ��ϵͳ���ٺ�
	 *  */
	public int nextTrace();

	/** 
	 * �������ɵ�����һ�����ٺ� 
	 * 
	 * */
	public int getLastTrace();

}