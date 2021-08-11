package org.dw.common.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MD5 {
	private final static String[] strDigits = { "0", "1", "2", "3", "4", "5","6", "7", "8", "9", "a", "b", "c", "d", "e", "f" };

    private static String byteToArrayString(byte bByte) {
        int iRet = bByte;
        iRet = iRet<0 ? iRet+256 : iRet;
        int iD1 = iRet / 16;
        int iD2 = iRet % 16;
        return strDigits[iD1] + strDigits[iD2];
    }

    private static String byteToString(byte[] bByte){
        StringBuffer stringBuffer = new StringBuffer();
        for (int i = 0; i < bByte.length; i++) {
            stringBuffer.append(byteToArrayString(bByte[i]));
        }
        return stringBuffer.toString();
    }

    public static String getMD5Code(String strObj) throws NoSuchAlgorithmException{
        MessageDigest md = MessageDigest.getInstance("MD5");
        String result = byteToString(md.digest(strObj.getBytes()));
        return result;
    }
}
