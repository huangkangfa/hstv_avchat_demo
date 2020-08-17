package com.android.baselib.utils.entry;


import com.android.baselib.utils.Convert;

/**
 * Created by huangkangfa on 2017/11/19.
 */

public class CRC_CCITT {

    public enum Type {
        TYPE_Kermit,
        TYPE_XModem,
        TYPE_OxFFFF,
        TYPE_Ox1D0F
    }

    private static String getCrcCheck_Kermit(byte[] data) {
        int MASK = 0x0001, CRCSEED = 0x0810;
        int remain = 0;

        byte val;
        for (int i = 0; i < data.length; i++) {
            val = data[i];
            for (int j = 0; j < 8; j++) {
                if (((val ^ remain) & MASK) != 0) {
                    remain ^= CRCSEED;
                    remain >>= 1;
                    remain |= 0x8000;
                } else {
                    remain >>= 1;
                }
                val >>= 1;
            }
        }

        byte[] crcByte = new byte[2];
        crcByte[1] = (byte) ((remain >> 8) & 0xff);
        crcByte[0] = (byte) (remain & 0xff);
        return Convert.bytes2HexString(crcByte);
    }

    private static String getCrcCheck_Others(byte[] bytes, int crc) {
        int polynomial = 0x1021; // poly value
        for (int index = 0; index < bytes.length; index++) {
            byte b = bytes[index];
            for (int i = 0; i < 8; i++) {
                boolean bit = ((b >> (7 - i) & 1) == 1);
                boolean c15 = ((crc >> 15 & 1) == 1);
                crc <<= 1;
                if (c15 ^ bit)
                    crc ^= polynomial;
            }
        }
        crc &= 0xffff;
        return Integer.toHexString(crc).toUpperCase();
    }

    public static String calcCrc16(byte[] data,Type type) {
        if(type== Type.TYPE_Kermit){
            return getCrcCheck_Kermit(data);
        }else{
            switch (type){
                case TYPE_XModem:
                    return getCrcCheck_Others(data, 0x0000);
                case TYPE_OxFFFF:
                    return getCrcCheck_Others(data, 0xffff);
                case TYPE_Ox1D0F:
                    return getCrcCheck_Others(data, 0x1d0f);
            }
            return null;
        }
    }

}
