package cn.trawe.etc.hunanfront.utils;

import java.io.UnsupportedEncodingException;

public class HexUtils {
	 public static byte[] hexStringToBytes(String hexString) {  
	     if (hexString == null || hexString.equals("")) {  
	         return null;  
	     }  
	   hexString = hexString.toUpperCase();  
	    int length = hexString.length() / 2;  
	    char[] hexChars = hexString.toCharArray();  
	    byte[] d = new byte[length];  
	    for (int i = 0; i < length; i++) {  
	        int pos = i * 2;  
	         d[i] = (byte) (charToByte(hexChars[pos]) << 4 | charToByte(hexChars[pos + 1]));  
	    }  
	     return d;  
	}

	 private static byte charToByte(char c) {  
		     return (byte) "0123456789ABCDEF".indexOf(c);  
		 } 
	
	 public static  String bytesToHexString(byte[] bArray) {
	   	  StringBuffer sb = new StringBuffer(bArray.length);
	   	  String sTemp;
	   	  for (int i = 0; i < bArray.length; i++) {
	   	   sTemp = Integer.toHexString(0xFF & bArray[i]);
	   	   if (sTemp.length() < 2)
	   	    sb.append(0);
	   	   sb.append(sTemp.toUpperCase());
	   	  }
	   	  return sb.toString();
	   	 }
	 
	 public static void main(String[] args) throws UnsupportedEncodingException {
		 byte[] hexStringToBytes = hexStringToBytes("bafec4cf");
		 System.out.println(new String(hexStringToBytes,"GBK"));
	}
}
