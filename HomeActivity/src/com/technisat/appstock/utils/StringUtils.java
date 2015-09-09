package com.technisat.appstock.utils;

public class StringUtils {
	public static boolean hasText(String str) {
		if (str == null) {
			return false;
		} else if (str.length() < 1) {
			return false;
		} else {
			return true;
		}
	}
	
	public static String stringOrText(String str, String replacement) {
		return hasText(str) ? str : replacement;
	}
}
