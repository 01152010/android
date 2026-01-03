package com.upad.utils;

import java.util.Arrays;

public class ByteUtil {
    public static String convertByte2HexString(byte b) {
        char u8 = convertByte2Uint8(b);
        return Integer.toHexString(u8);
    }

    public static String binaryToHex(String bin) {
        return String.format("%08X", Long.parseLong(bin,2)) ;
    }

    public static String transbit(int num){
        String temp = "00000000000000000000";
        char[] status = temp.toCharArray();
        status[temp.length()-num] = '1';
        String bin = Arrays.toString(status).replaceAll("[\\[\\]\\s,]", "");
        return binaryToHex(bin);
    }

    public static char convertByte2Uint8(byte b) {
        return (char) (b & 0xff);
    }

    public static int converByte2Int(byte b){
        return b & 0xff;
    }

    public static byte[] hexStringToBytes(String hex) {
        int len = (int) Math.ceil(hex.length() / 2.0);
        byte[] result = new byte[len];
        for(int i = 0; i < len; i++){
            int pos = i*2;
            if(i == len-1 && 0 != hex.length()%2){
                String temp = hex.substring(pos,pos+1);
                result[i] = (byte) Integer.parseInt(temp,16);
            }else{
                String temp = hex.substring(pos,pos+2);
                result[i] = (byte) Integer.parseInt(temp,16);
            }
        }
        return result;
    }


    public static byte[] copyArray(byte[] array1,byte[] array2){
        byte[] result= new byte[array1.length+array2.length];
        System.arraycopy(array1,0,result,0,array1.length);
        System.arraycopy(array2,0,result,array1.length,array2.length);
        return result;
    }
    public static String convertBytes2HexString(byte[] array){
        if(null == array){
            return null;
        }
        StringBuilder sb = new StringBuilder();
        for(byte bt : array){
            String hexString = convertByte2HexString(bt);
            if (hexString.length() == 1) {
                sb.append("0");
            }
            sb.append(hexString);
        }
        return sb.toString().toUpperCase();
    }

    public static String convertInt2HexString(int content){
        String str = Integer.toHexString(content);
        if(1 == str.length()){
            str = "0"+str;
        }
        return str.toUpperCase();
    }

    public static byte[] int2Bytes(int content){
        byte low = (byte)content;
        byte high = (byte)((content & 0xff00) >> 8);
        return new byte[]{high,low};
    }

    public static String tranHexString(String hexStr){
        String result = "";
        for(int i = 0; i < hexStr.length(); i++){
            if(0 == i){
                result += hexStr.substring(i,i+1);
                continue;
            }
            if(0 == i%2){
                result += " "+hexStr.charAt(i);
            }else{
                result += hexStr.substring(i,i+1);
            }
        }
        return result;
    }
}
