package com.all.client.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public final class PasswordDigest {
	
	private PasswordDigest() {
	}
	
	public static String md5(byte[] password) {
		MessageDigest md5;
		try {
			md5 = MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException e) {
			return new String(password);
		}
		md5.reset();
		md5.update(password);
		StringBuilder sb = new StringBuilder();
		for(byte b:md5.digest()) {
			String hexString = Integer.toHexString(0xFF&b);
			if(hexString.length()==1) {
				sb.append('0'); 
			}
			sb.append(hexString);  
		}
		return sb.toString();
	}

}
