package com.technisat.appstock.hash;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import android.util.Base64;

public class NexxooHash {
	
	/**
	 * Returns the Sha 256 Hash of the given password.
	 * @param passwordClear The password unhashed.
	 * @return The password hash as String or null if SHA-256 is not available or no password given.
	 */
	public static String hashPassword(String passwordClear){
		try {
			MessageDigest md = MessageDigest.getInstance("SHA-256");			
			md.update(passwordClear.getBytes());
			 
	        byte byteData[] = md.digest();
	 
	        //convert the byte to hex format method 1
	        StringBuffer sb = new StringBuffer();
	        for (int i = 0; i < byteData.length; i++) {
	         sb.append(Integer.toString((byteData[i] & 0xff) + 0x100, 16).substring(1));
	        }
	        
	        return sb.toString();
		} catch (NoSuchAlgorithmException e) {
			return null;
		}
        
	}
	
	/**
	 * Returns the MD5 hash value of the given deviceSerial id.
	 * @param deviceSerial The serial number to hash.
	 * @return The hash as String or null if MD5 is not available or no serial given.
	 */
	public static String hashDeviceId(String deviceSerial){
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");			
			md.update(deviceSerial.getBytes());
			 
	        byte byteData[] = md.digest();

	        StringBuffer sb = new StringBuffer();
	        for (int i = 0; i < byteData.length; i++) {
	         sb.append(Integer.toString((byteData[i] & 0xff) + 0x100, 16).substring(1));
	        }
	        
	        return sb.toString();
		} catch (NoSuchAlgorithmException e) {
			return null;
		}
	}
	
	/**
	 * Base64 encodes the given String.
	 * @param string The String to encode with base64.
	 * @return The base 64 encoded String or null if given String was null or String could not be encoded properly.
	 */
	public static String base64EncodeString(String string){
		byte[] resultByte = null;
		resultByte = Base64.encode(string.getBytes(), Base64.NO_WRAP);
		String result = null;
		try {
			result = new String(resultByte, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			//returning null
		}
		return result;
	}

}
