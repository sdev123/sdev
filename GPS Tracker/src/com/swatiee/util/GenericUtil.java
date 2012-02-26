package com.swatiee.util;

public class GenericUtil {
	public static String decryptPwd(String encryptedPwd) {
		StringBuffer sb = new StringBuffer();
		if (encryptedPwd!=null && encryptedPwd!=""){
			for(int i = 0; i<encryptedPwd.length();i++){
				sb.append(encryptedPwd.charAt(i));
				i = i+1;
			}
		}
		return sb.toString();
	}
	public static String decryptit(String encryptedPwd) {
		StringBuffer sb = new StringBuffer();
		if (encryptedPwd!=null && encryptedPwd!=""){
			for(int i = 0; i<encryptedPwd.length();i++){
				sb.append(encryptedPwd.charAt(i));
				i = i+1;
			}
		}
		return sb.toString();
	}

	public static String encryptPwd(String oriPwd) {
		String mycrptString = "^3n!S*I3d5h*m#xcM.n$?vm0v&g3dfg&(68vsc#jg9%$igefkca/>L@#r&(gkfvfcajskfejsgfajsdgcsyeka";
		StringBuffer sb = new StringBuffer();
		if (oriPwd!=null && oriPwd!=""){
			for (int i = 0; i<oriPwd.length();i++){
				sb.append(oriPwd.charAt(i));
				sb.append(mycrptString.charAt(i));
			}
		}
		return sb.toString();
	}
	
	public static boolean isBlank(String str) {
		return (str==null || str.trim().equals(""));
	}	
}
