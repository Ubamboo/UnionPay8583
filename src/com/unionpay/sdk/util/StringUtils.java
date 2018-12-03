package com.unionpay.sdk.util;

import java.nio.ByteBuffer;  
import java.util.Arrays;  
  
public class StringUtils {  
  
    /**  
     * ��ȡ�ַ��������� ��isNumber����true���ʾ��ȡ���֣�false���ʾ��ȡ�ַ�  
     *   
     * @param result  
     * @param isNumber  
     * @return  
     */  
    public static String extract(String result, boolean isNumber)  
    {  
        if (null == result || result.equals("") || result.length() == 0)  
        {  
            throw new IllegalArgumentException("��������ȷ������Ϊ��!");  
        }  
        StringBuffer resultBuffer = new StringBuffer();  
  
        char[] chars = result.toUpperCase().toCharArray();  
  
        for (char c : chars)  
        {  
            boolean flag = Character.isDigit(c);  
  
            if (isNumber && flag)  
            {  
                resultBuffer.append(c);  
            }  
  
            if (!flag && !isNumber)  
            {  
                resultBuffer.append(c);  
            }  
  
        }  
        return resultBuffer.toString();  
  
    }  
  
    /**  
     * ��ȥ10  
     *   
     * @param input  
     * @return  
     */  
    public static String divide(String input)  
    {  
  
        if (null == input || input.equals("") || input.length() == 0)  
        {  
            throw new IllegalArgumentException("��������ȷ������Ϊ��!");  
        }  
  
        char[] output = new char[input.length()];  
  
        for (int i = 0; i < input.length(); i++)  
        {  
            if (output[i] > 96)  
            {  
                output[i] = (char) (output[i] - 49);  
  
            } else if (output[i] > 64)  
            {  
  
                output[i] = (char) (output[i] - 17);  
  
            } else  
            {  
                output[i] = output[i];  
            }  
        }  
        return Arrays.toString(output);  
    }  
  
    /**  
     * ׷���ַ���ָ�����ȵ��ַ�  
     *   
     * @param srcData  
     *            :ԭ����  
     * @param alignMode  
     *            :���뷽ʽ  
     * @param paddCharacter  
     *            :����ַ�  
     * @param totalLen  
     *            :��䵽�ĳ���  
     * @return  
     */  
    public static String padding(String srcData, String alignMode, String paddCharacter, int totalLen)  
    {  
  
        if (srcData == null || null == alignMode || null == paddCharacter || totalLen == 0)  
        {  
            throw new IllegalArgumentException("��������ݲ���Ϊ�ջ�0����������!");  
        }  
  
        int paddLen = totalLen - srcData.length();  
  
        StringBuffer paddResultBuffer = new StringBuffer();  
  
        if (alignMode.equalsIgnoreCase("left"))  
        {  
            for (int i = 0; i < paddLen; i++)  
            {  
                paddResultBuffer.append(paddCharacter);  
            }  
            paddResultBuffer.append(srcData);  
        } else if (alignMode.equalsIgnoreCase("right"))  
        {  
            paddResultBuffer.append(srcData);  
            for (int i = 0; i < paddLen; i++)  
            {  
                paddResultBuffer.append(paddCharacter);  
            }  
  
        } else  
        {  
            throw new IllegalArgumentException("paddAlign  is not left or right��please check !");  
        }  
  
        return paddResultBuffer.toString();  
    }  
  
    /**  
     * �������ݽ���������  
     *   
     * @param hexSrcData1  
     *            :32CB95B36D89477C  
     * @param hexSrcData2  
     *            :3030000000000000  
     * @return  
     */  
    public static String XOR(String hexSrcData1, String hexSrcData2)  
    {  
  
        if (hexSrcData1.length() != hexSrcData2.length())  
        {  
            throw new IllegalArgumentException("�����������ݳ��Ȳ���ȣ���������!");  
        }  
  
        byte[] bytes1 = HexBinary.decode(hexSrcData1);  
  
        byte[] bytes2 = HexBinary.decode(hexSrcData2);  
  
        ByteBuffer buffer = ByteBuffer.allocate(bytes2.length);  
  
        for (int i = 0; i < bytes2.length; i++)  
        {  
            byte temp = (byte) ((int) bytes1[i] ^ (int) bytes2[i]);  
            buffer.put(temp);  
        }  
  
        return HexBinary.encode(buffer.array());  
    }  
  
    /**  
     * ��λȡ������  
     *   
     * @param hexSrcData  
     * @return  
     */  
    public static String reversBytes(String hexSrcData)  
    {  
        if (null == hexSrcData || hexSrcData.equals("") || hexSrcData.length() == 0)  
        {  
            throw new IllegalArgumentException("�Ƿ��İ�λȡ�������ݣ���������");  
        }  
  
        byte[] srcBytes = HexBinary.decode(hexSrcData);  
  
        ByteBuffer destBuffer = ByteBuffer.allocate(srcBytes.length);  
  
        for (int i = 0; i < srcBytes.length; i++)  
        {  
  
            byte temp = (byte) (~(int) srcBytes[i]);  
  
            destBuffer.put(temp);  
        }  
  
        return HexBinary.encode(destBuffer.array());  
    }  
  
}  
