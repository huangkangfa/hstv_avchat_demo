package com.android.baselib.utils;

import com.android.baselib.utils.entry.CRC16;
import com.android.baselib.utils.entry.CRC8;
import com.android.baselib.utils.entry.CRC_CCITT;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.zip.CRC32;

import static com.android.baselib.utils.Convert.binary2Decimal;
import static com.android.baselib.utils.Convert.bytes2HexString;
import static com.android.baselib.utils.Convert.decimalToHex;
import static com.android.baselib.utils.Convert.hex2Bytes;
import static com.android.baselib.utils.Convert.hexToBinary;

/**
 * 常用数据计算方法
 * <p>
 * Created by huangkangfa on 2017/11/16.
 */
public class ComputingMethod {
    /**
     * 信号强度计算  db单位
     * 获取有符号的单字节数据:信号强度为0，信号强度总为负值，最高位代表符号位，1为负，数值越大信号越好，理论范围0~-127（上电初始上报为0）
     *
     * @param data
     * @return
     */
    public static String getSignData(String data) {
        String temp = hexToBinary(data, true);
        String t1 = temp.substring(0, 1);
        String t2 = "0" + temp.substring(1, temp.length());
        return "1".equals(t1) ? "-" + (128 - binary2Decimal(t2)) : "" + binary2Decimal(t2);
    }

    /**
     * 加法和校验值
     *
     * @param checkStr
     * @return
     */
    public static String getAddCheck(String checkStr) {
        byte[] data = hex2Bytes(checkStr);
        byte addSum = 0;
        for (int i = 0; i < data.length; i++) {
            addSum += data[i];
        }
        return String.format("%02x", addSum & 0xff);
    }

    /**
     * 异或和校验
     *
     * @param checkStr
     * @return
     */
    public static String getXorCheck(String checkStr) {
        byte[] data = hex2Bytes(checkStr);
        byte[] temp = new byte[1];
        temp[0] = data[0];
        for (int i = 1; i < data.length; i++) {
            temp[0] ^= data[i];
        }
        return bytes2HexString(temp);
    }

    /**
     * 计算产生 crc8 校验码
     *
     * @param data 需要校验的数据
     * @return 校验码
     */
    public static String getCrc8Check(byte[] data) {
        int crc = CRC8.calcCrc8(data);
        return decimalToHex(crc);
    }

    /**
     * 计算产生 crc16 校验码(Modbus)
     *
     * @param data 需要校验的数据
     * @return 校验码
     */
    public static String getCrc16Check(byte[] data) {
        int crc = CRC16.calcCrc16(data);
        return decimalToHex(crc);
    }

    /**
     * 计算产生 crc16 校验码(ccitt)
     *
     * @param data 需要校验的数据
     * @return 校验码
     */
    public static String getCrc16Check_CCITT(byte[] data, CRC_CCITT.Type type) {
        return CRC_CCITT.calcCrc16(data,type);
    }

    /**
     * 计算产生 crc32 校验码
     *
     * @param data 需要校验的数据
     * @return 校验码
     */
    public static String getCrc32Check(byte[] data) {
        CRC32 crc32 = new CRC32();
        crc32.update(data);
        return decimalToHex((int) crc32.getValue());
    }

    /**
     * 计算产生 crc32 校验码
     *
     * @param file 需要校验的数据
     * @return 校验码
     */
    public static String getCrc32Check(File file) {
        CRC32 crc32 = new CRC32();
        FileInputStream fileInputStream = null;
        try {
            fileInputStream = new FileInputStream(file);
            byte[] buffer = new byte[8192];
            int length;
            while ((length = fileInputStream.read(buffer)) != -1) {
                crc32.update(buffer, 0, length);
            }
            return decimalToHex((int) crc32.getValue());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } finally {
            try {
                if (fileInputStream != null)
                    fileInputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 子字符串在父字符串中出现的次数
     */
    public static int getSubNum(String str, String substr) {
        int index = 0;
        int count = 0;
        int fromindex = 0;
        while ((index = str.indexOf(substr, fromindex)) != -1) {
            fromindex = index + substr.length();
            count++;
        }
        return count;
    }
}
