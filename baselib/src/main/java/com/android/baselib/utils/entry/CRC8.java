package com.android.baselib.utils.entry;

/**
 * CRC8相关计算
 * <p>
 * encode: utf-8
 *
 * @author trb
 * @date 2013-11-21
 */
public class CRC8 {

    public static int calcCrc8(byte[] buffer) {
        int crci = 0xFF;   //起始字节FF
        for (int j = 0; j < buffer.length; j++) {
            crci ^= buffer[j] & 0xFF;
            for (int i = 0; i < 8; i++) {
                if ((crci & 1) != 0) {
                    crci >>= 1;
                    crci ^= 0xB8;    //多项式当中的那个啥的，不同多项式不一样
                } else {
                    crci >>= 1;
                }
            }
        }
        return (crci&0xff);
    }
}
