package com.nvlad.tinypng.util;

public class ZipSignUtil {
    public static boolean isHaveSign(byte[] fileByteArray) {
        if (fileByteArray == null) throw new NullPointerException();
        int len = fileByteArray.length;
        return fileByteArray[len-2] == 0x50 &&
                fileByteArray[len-3] == 0x49 &&
                fileByteArray[len-4] == 0x5A &&
                fileByteArray[len-5] == 0x54;
    }

    public static int getZipCount(byte[] fileByteArray){
        if (fileByteArray == null) return -1;
        if (!isHaveSign(fileByteArray)) return 0;
        return fileByteArray[fileByteArray.length-1];
    }

    public static byte[] addZipSign(byte[] sourceByte, byte[] compressByte){
        if (sourceByte == null || compressByte == null) return compressByte == null ? sourceByte : compressByte;
        byte[] newByteArray;
        int length = compressByte.length;
        newByteArray = new byte[length + 5];
        System.arraycopy(compressByte, 0, newByteArray, 0, compressByte.length);
        if (isHaveSign(sourceByte)){
            System.arraycopy(sourceByte, sourceByte.length - 5, newByteArray, newByteArray.length - 5, 5);
            newByteArray[newByteArray.length - 1]++;
        }else{
            newByteArray[length] = 0x54;
            newByteArray[length + 1] = 0x5A;
            newByteArray[length + 2] = 0x49;
            newByteArray[length + 3] = 0x50;
            newByteArray[length + 4] = 0x01;
        }
        return newByteArray;
    }
}
