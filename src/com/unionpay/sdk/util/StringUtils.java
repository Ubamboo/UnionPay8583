package com.unionpay.sdk.util;

import java.nio.ByteBuffer;  
import java.util.Arrays;  
  
public class StringUtils {  
  
    /**  
     * 抽取字符或是数字 若isNumber传入true则表示抽取数字，false则表示抽取字符  
     *   
     * @param result  
     * @param isNumber  
     * @return  
     */  
    public static String extract(String result, boolean isNumber)  
    {  
        if (null == result || result.equals("") || result.length() == 0)  
        {  
            throw new IllegalArgumentException("参数不正确，不能为空!");  
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
     * 减去10  
     *   
     * @param input  
     * @return  
     */  
    public static String divide(String input)  
    {  
  
        if (null == input || input.equals("") || input.length() == 0)  
        {  
            throw new IllegalArgumentException("参数不正确，不能为空!");  
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
     * 追加字符到指定长度的字符  
     *   
     * @param srcData  
     *            :原数据  
     * @param alignMode  
     *            :对齐方式  
     * @param paddCharacter  
     *            :填补的字符  
     * @param totalLen  
     *            :填充到的长度  
     * @return  
     */  
    public static String padding(String srcData, String alignMode, String paddCharacter, int totalLen)  
    {  
  
        if (srcData == null || null == alignMode || null == paddCharacter || totalLen == 0)  
        {  
            throw new IllegalArgumentException("传入的数据不能为空或0，请检查数据!");  
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
            throw new IllegalArgumentException("paddAlign  is not left or right，please check !");  
        }  
  
        return paddResultBuffer.toString();  
    }  
  
    /**  
     * 两个数据进行异或操作  
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
            throw new IllegalArgumentException("异或的两个数据长度不相等，请检查数据!");  
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
     * 按位取反操作  
     *   
     * @param hexSrcData  
     * @return  
     */  
    public static String reversBytes(String hexSrcData)  
    {  
        if (null == hexSrcData || hexSrcData.equals("") || hexSrcData.length() == 0)  
        {  
            throw new IllegalArgumentException("非法的按位取反的数据，请检查数据");  
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
