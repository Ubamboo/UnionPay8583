
package com.unionpay.sdk.service.impl;

import com.unionpay.sdk.service.SystemTraceNumGenerator;

public class SimpleSystemTraceNumGen implements SystemTraceNumGenerator {

	private int value = 0;

	/** 
	 * ����Ψһ��ʶһ�ʽ��׵ı�ţ�ͬһ�������ڲ����ظ����ñ�Ž�ѭ���ظ�ʹ�ã���000000��999999��
	 * @param initialValue a number between 1 and 999999.
	 * @throws IllegalArgumentException if the number is less than 1 or greater than 999999.
	 * 
	 */
	public SimpleSystemTraceNumGen(int initialValue) {
		if (initialValue < 1 || initialValue > 999999) {
			throw new IllegalArgumentException("Initial value must be between 1 and 999999");
		}
		value = initialValue - 1;
	}

	public synchronized int getLastTrace() {
		return value;
	}

	public synchronized int nextTrace() {
		value++;
		if (value > 999999) {
			value = 1;
		}
		return value;
	}

}
