package com.unionpay.sdk.message;


import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;

import com.unionpay.sdk.UnionPayValue;
import com.unionpay.sdk.eunm.UnionPayValueType;
import com.unionpay.sdk.util.HexBinary;

/** ����һ���й����8583��ʽ��׼���࣬��ʼ������Դ����FieldParseInfo  <P/>
 * This class contains the information needed to parse a field from a message buffer.
 *
 * @author zyplanke
 */
public class UnionPayFieldParseInfo {

	private UnionPayValueType type;
	private int length;

	public UnionPayFieldParseInfo(UnionPayValueType t, int len) {
		if (t == null) {
			throw new IllegalArgumentException("UnionPayValueType cannot be null");
		}
		type = t;
		length = len;
	}
	public int getLength() {
		return length;
	}
	public UnionPayValueType getType() {
		return type;
	}

	public UnionPayValue<?> parse(byte[] buf, int pos) throws ParseException {
		if (type == UnionPayValueType.NUMERIC || type == UnionPayValueType.ALPHA) {
			return new UnionPayValue<String>(type, new String(buf, pos, length), length);
		} else if (type == UnionPayValueType.LLVAR) {
			length = ((buf[pos] - 48) * 10) + (buf[pos + 1] - 48);
			return new UnionPayValue<String>(type, new String(buf, pos + 2, length));
		} else if (type == UnionPayValueType.LLLVAR) {
			length = ((buf[pos] - 48) * 100) + ((buf[pos + 1] - 48) * 10) + (buf[pos + 2] - 48);
			return new UnionPayValue<String>(type, new String(buf, pos + 3, length));
		} else if (type == UnionPayValueType.AMOUNT) {
			byte[] c = new byte[13];
			System.arraycopy(buf, pos, c, 0, 10);
			System.arraycopy(buf, pos + 10, c, 11, 2);
			c[10] = '.';
			return new UnionPayValue<BigDecimal>(type, new BigDecimal(new String(c)));
		} else if (type == UnionPayValueType.DATE10) {
			Calendar cal = Calendar.getInstance();
			//Set the month in the date
			cal.set(Calendar.MONTH, ((buf[pos] - 48) * 10) + buf[pos + 1] - 49);
			cal.set(Calendar.DATE, ((buf[pos + 2] - 48) * 10) + buf[pos + 3] - 48);
			cal.set(Calendar.HOUR_OF_DAY, ((buf[pos + 4] - 48) * 10) + buf[pos + 5] - 48);
			cal.set(Calendar.MINUTE, ((buf[pos + 6] - 48) * 10) + buf[pos + 7] - 48);
			cal.set(Calendar.SECOND, ((buf[pos + 8] - 48) * 10) + buf[pos + 9] - 48);
			if (cal.getTime().after(new Date())) {
				cal.add(Calendar.YEAR, -1);
			}
			return new UnionPayValue<Date>(type, cal.getTime());
		} else if (type == UnionPayValueType.DATE4) {
			Calendar cal = Calendar.getInstance();
			cal.set(Calendar.HOUR, 0);
			cal.set(Calendar.MINUTE, 0);
			cal.set(Calendar.SECOND, 0);
			//Set the month in the date
			cal.set(Calendar.MONTH, ((buf[pos] - 48) * 10) + buf[pos + 1] - 49);
			cal.set(Calendar.DATE, ((buf[pos + 2] - 48) * 10) + buf[pos + 3] - 48);
			if (cal.getTime().after(new Date())) {
				cal.add(Calendar.YEAR, -1);
			}
			return new UnionPayValue<Date>(type, cal.getTime());
		} else if (type == UnionPayValueType.DATE_EXP) {
			Calendar cal = Calendar.getInstance();
			cal.set(Calendar.HOUR, 0);
			cal.set(Calendar.MINUTE, 0);
			cal.set(Calendar.SECOND, 0);
			cal.set(Calendar.DATE, 1);
			//Set the month in the date
			cal.set(Calendar.YEAR, cal.get(Calendar.YEAR) - (cal.get(Calendar.YEAR) % 100)
					+ ((buf[pos] - 48) * 10) + buf[pos + 1] - 48);
			cal.set(Calendar.MONTH, ((buf[pos + 2] - 48) * 10) + buf[pos + 3] - 49);
			return new UnionPayValue<Date>(type, cal.getTime());
		} else if (type == UnionPayValueType.TIME) {
			Calendar cal = Calendar.getInstance();
			cal.set(Calendar.HOUR_OF_DAY, ((buf[pos] - 48) * 10) + buf[pos + 1] - 48);
			cal.set(Calendar.MINUTE, ((buf[pos + 2] - 48) * 10) + buf[pos + 3] - 48);
			cal.set(Calendar.SECOND, ((buf[pos + 4] - 48) * 10) + buf[pos + 5] - 48);
			return new UnionPayValue<Date>(type, cal.getTime());
		}else if (type == UnionPayValueType.BINARY) {
			byte[] binaryByte=new byte[length];
			System.arraycopy(buf, pos, binaryByte, 0, length);
			return new UnionPayValue<String>(type, HexBinary.encode(binaryByte), length);
		}
		return null;
	}

	/** Parses binary data from the buffer, creating and returning an UnionPayValue of the configured
	 * type and length. */
	public UnionPayValue<?> parseBinary(byte[] buf, int pos) throws ParseException {
		if (type == UnionPayValueType.ALPHA) {

			return new UnionPayValue<String>(type, new String(buf, pos, length), length);

		} else if (type == UnionPayValueType.NUMERIC) {

			//A long covers up to 18 digits
			if (length < 19) {
				long l = 0;
				int power = 1;
				for (int i = pos + (length / 2) + (length % 2) - 1; i >= pos; i--) {
					l += (buf[i] & 0x0f) * power;
					power *= 10;
					l += ((buf[i] & 0xf0) >> 4) * power;
					power *= 10;
				}
				return new UnionPayValue<Number>(UnionPayValueType.NUMERIC, l, length);
			} else {
				//Use a BigInteger
				char[] digits = new char[length];
				int start = 0;
				for (int i = pos; i < pos + (length / 2) + (length % 2); i++) {
					digits[start++] = (char)(((buf[i] & 0xf0) >> 4) + 48);
					digits[start++] = (char)((buf[i] & 0x0f) + 48);
				}
				return new UnionPayValue<Number>(UnionPayValueType.NUMERIC, new BigInteger(new String(digits)), length);
			}

		} else if (type == UnionPayValueType.LLVAR) {

			length = (((buf[pos] & 0xf0) >> 4) * 10) + (buf[pos] & 0x0f);
			return new UnionPayValue<String>(type, new String(buf, pos + 1, length));

		} else if (type == UnionPayValueType.LLLVAR) {

			length = ((buf[pos] & 0x0f) * 100) + (((buf[pos + 1] & 0xf0) >> 4) * 10) + (buf[pos + 1] & 0x0f);
			return new UnionPayValue<String>(type, new String(buf, pos + 2, length));

		} else if (type == UnionPayValueType.AMOUNT) {

			char[] digits = new char[13];
			digits[10] = '.';
			int start = 0;
			for (int i = pos; i < pos + 6; i++) {
				digits[start++] = (char)(((buf[i] & 0xf0) >> 4) + 48);
				digits[start++] = (char)((buf[i] & 0x0f) + 48);
				if (start == 10) {
					start++;
				}
			}
			return new UnionPayValue<BigDecimal>(UnionPayValueType.AMOUNT, new BigDecimal(new String(digits)));
		} else if (type == UnionPayValueType.DATE10 || type == UnionPayValueType.DATE4 || type == UnionPayValueType.DATE_EXP
				|| type == UnionPayValueType.TIME) {

			int[] tens = new int[(type.getLength() / 2) + (type.getLength() % 2)];
			int start = 0;
			for (int i = pos; i < pos + tens.length; i++) {
				tens[start++] = (((buf[i] & 0xf0) >> 4) * 10) + (buf[i] & 0x0f);
			}
			Calendar cal = Calendar.getInstance();
			if (type == UnionPayValueType.DATE10) {
				//A SimpleDateFormat in the case of dates won't help because of the missing data
				//we have to use the current date for reference and change what comes in the buffer
				//Set the month in the date
				cal.set(Calendar.MONTH, tens[0] - 1);
				cal.set(Calendar.DATE, tens[1]);
				cal.set(Calendar.HOUR_OF_DAY, tens[2]);
				cal.set(Calendar.MINUTE, tens[3]);
				cal.set(Calendar.SECOND, tens[4]);
				if (cal.getTime().after(new Date())) {
					cal.add(Calendar.YEAR, -1);
				}
				return new UnionPayValue<Date>(type, cal.getTime());
			} else if (type == UnionPayValueType.DATE4) {
				cal.set(Calendar.HOUR, 0);
				cal.set(Calendar.MINUTE, 0);
				cal.set(Calendar.SECOND, 0);
				//Set the month in the date
				cal.set(Calendar.MONTH, tens[0] - 1);
				cal.set(Calendar.DATE, tens[1]);
				if (cal.getTime().after(new Date())) {
					cal.add(Calendar.YEAR, -1);
				}
				return new UnionPayValue<Date>(type, cal.getTime());
			} else if (type == UnionPayValueType.DATE_EXP) {
				cal.set(Calendar.HOUR, 0);
				cal.set(Calendar.MINUTE, 0);
				cal.set(Calendar.SECOND, 0);
				cal.set(Calendar.DATE, 1);
				//Set the month in the date
				cal.set(Calendar.YEAR, cal.get(Calendar.YEAR)
						- (cal.get(Calendar.YEAR) % 100) + tens[0]);
				cal.set(Calendar.MONTH, tens[1] - 1);
				return new UnionPayValue<Date>(type, cal.getTime());
			} else if (type == UnionPayValueType.TIME) {
				cal.set(Calendar.HOUR_OF_DAY, tens[0]);
				cal.set(Calendar.MINUTE, tens[1]);
				cal.set(Calendar.SECOND, tens[2]);
				return new UnionPayValue<Date>(type, cal.getTime());
			}
			return new UnionPayValue<Date>(type, cal.getTime());
		}
		return null;
	}

}
