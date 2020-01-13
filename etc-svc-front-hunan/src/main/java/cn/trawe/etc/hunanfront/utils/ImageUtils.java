package cn.trawe.etc.hunanfront.utils;

public class ImageUtils {
	
	 public static Integer imageSize(String image){
	     String str=image.substring(22); 
	     Integer equalIndex= str.indexOf("=");
	     if(str.indexOf("=")>0) {
	       str=str.substring(0, equalIndex);
	     }
	     Integer strLength=str.length();
	     Integer size=strLength-(strLength/8)*2;
	     return size/1024;
	   }
	 
	 public static void main(String[] args) {
		
		 
		 
	}

}
