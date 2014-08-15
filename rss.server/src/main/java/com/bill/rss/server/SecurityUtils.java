package com.bill.rss.server;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import com.bill.rss.domain.ReaderException;
import com.bill.rss.domain.User;

public class SecurityUtils {

	public static String encrypt(String string) {
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
	        md.update(string.getBytes());
	        byte byteData[] = md.digest();

	        //convert the byte to hex format method 1
	        StringBuilder stringBuilder = new StringBuilder();
	        for (int i = 0; i < byteData.length; i++) {
	        	stringBuilder.append(Integer.toString((byteData[i] & 0xff) + 0x100, 16).substring(1));
	        }
	        return stringBuilder.toString();
		} catch (NoSuchAlgorithmException e) {
			throw new ReaderException("Unable to encryt string " + string, e);
		}
	}


	public static User clearPassword(User user) {
        user.setPassword("");
        return user;
    }
}
