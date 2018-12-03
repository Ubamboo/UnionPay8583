package com.unionpay.sdk;

import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.util.Date;

import com.unionpay.sdk.eunm.UnionPayValueType;

public class UnionPayValue<T> {
	private UnionPayValueType dataType;
	private T value;
	private int length;

	public UnionPayValue(UnionPayValueType t, T value) {
		if (t.needsLength()) {
			throw new IllegalArgumentException("Fixed-value types must use constructor that specifies length");
		}
		dataType = t;
		this.value = value;
		if (dataType == UnionPayValueType.LLVAR || dataType == UnionPayValueType.LLLVAR) {
			length = value.toString().length();
			if (t == UnionPayValueType.LLVAR && length > 99) {
				throw new IllegalArgumentException("LLVAR can only hold values up to 99 chars");
			} else if (t == UnionPayValueType.LLLVAR && length > 999) {
				throw new IllegalArgumentException("LLLVAR can only hold values up to 999 chars");
			}
		} else {
			length = dataType.getLength();
		}
	}

	/** Creates a new instance that stores the specified value as the specified type.
	 * Useful for storing fixed-length value types. */
	public UnionPayValue(UnionPayValueType t, T val, int len) {
		dataType = t;
		value = val;
		length = len;
		if (length == 0 && t.needsLength()) {
			throw new IllegalArgumentException("Length must be greater than zero");
		} else if (t == UnionPayValueType.LLVAR || t == UnionPayValueType.LLLVAR) {
			length = val.toString().length();
			if (t == UnionPayValueType.LLVAR && length > 99) {
				throw new IllegalArgumentException("LLVAR can only hold values up to 99 chars");
			} else if (t == UnionPayValueType.LLLVAR && length > 999) {
				throw new IllegalArgumentException("LLLVAR can only hold values up to 999 chars");
			}
		}
	}

	/** Returns the cnType to which the value must be formatted. */
	public UnionPayValueType getType() {
		return dataType;
	}

	/** Returns the length of the stored value, of the length of the formatted value
	 * in case of NUMERIC or ALPHA. It doesn't include the field length header in case
	 * of LLVAR or LLLVAR. */
	public int getLength() {
		return length;
	}

	/** Returns the stored value without any conversion or formatting. */
	public T getValue() {
		return value;
	}

	/** Returns the formatted value as a String. The formatting depends on the type of the
	 * receiver. */
	public String toString() {
		if (value == null) {
			return "FieldValue<null>";
		}
		if (dataType == UnionPayValueType.NUMERIC || dataType == UnionPayValueType.AMOUNT) {
			if (dataType == UnionPayValueType.AMOUNT) {
				return dataType.format((BigDecimal)value, 12);
			} else if (value instanceof Number) {
				return dataType.format(((Number)value).longValue(), length);
			} else {
				return dataType.format(value.toString(), length);
			}
		} else if (dataType == UnionPayValueType.ALPHA) {
			return dataType.format(value.toString(), length);
		} else if (dataType == UnionPayValueType.LLLVAR || dataType == UnionPayValueType.LLLVAR) {
			return value.toString();
		} else if (value instanceof Date) {
			return dataType.format((Date)value);
		}
		return value.toString();
	}

	/** Returns a copy of the receiver that references the same value object. */
	@SuppressWarnings("unchecked")
	public UnionPayValue<T> clone() {
		return (UnionPayValue<T>)(new UnionPayValue(this.dataType, this.value, this.length));

	}

	/** Returns true of the other object is also an cnValue and has the same type and length,
	 * and if other.getValue().equals(getValue()) returns true. */
	public boolean equals(Object other) {
		if (other == null || !(other instanceof UnionPayValue)) {
			return false;
		}
		UnionPayValue comp = (UnionPayValue)other;
		return (comp.getType() == getType() && comp.getValue().equals(getValue()) && comp.getLength() == getLength());
	}

	/** Writes the formatted value to a stream, with the length header
	 * if it's a variable length type. */
	public void write(OutputStream outs, boolean binary) throws IOException {
		if (dataType == UnionPayValueType.LLLVAR || dataType == UnionPayValueType.LLVAR) {
			if (binary) {
				if (dataType == UnionPayValueType.LLLVAR) {
					outs.write(length / 100); //00 to 09 automatically in BCD
				}
				//BCD encode the rest of the length
				outs.write((((length % 100) / 10) << 4) | (length % 10));
			} else {
				//write the length in ASCII
				if (dataType == UnionPayValueType.LLLVAR) {
					outs.write((length / 100) + 48);
				}
				if (length >= 10) {
					outs.write(((length % 100) / 10) + 48);
				} else {
					outs.write(48);
				}
				outs.write((length % 10) + 48);
			}
		} else if (binary) {
			//numeric types in binary are coded like this
			byte[] buf = null;
			if (dataType == UnionPayValueType.NUMERIC) {
				buf = new byte[(length / 2) + (length % 2)];
			} else if (dataType == UnionPayValueType.AMOUNT) {
				buf = new byte[6];
			} else if (dataType == UnionPayValueType.DATE10 || dataType == UnionPayValueType.DATE4 || dataType == UnionPayValueType.DATE_EXP || dataType == UnionPayValueType.TIME) {
				buf = new byte[length / 2];
			}
			//Encode in BCD if it's one of these types
			if (buf != null) {
				toBcd(toString(), buf);
				outs.write(buf);
				return;
			}
		}
		//Just write the value as text
		outs.write(toString().getBytes());
	}

	/** Encode the value as BCD and put it in the buffer. The buffer must be big enough
	 * to store the digits in the original value (half the length of the string). */
	private void toBcd(String value, byte[] buf) {
		int charpos = 0; //char where we start
		int bufpos = 0;
		if (value.length() % 2 == 1) {
			//for odd lengths we encode just the first digit in the first byte
			buf[0] = (byte)(value.charAt(0) - 48);
			charpos = 1;
			bufpos = 1;
		}
		//encode the rest of the string
		while (charpos < value.length()) {
			buf[bufpos] = (byte)(((value.charAt(charpos) - 48) << 4)
					| (value.charAt(charpos + 1) - 48));
			charpos += 2;
			bufpos++;
		}
	}

}
