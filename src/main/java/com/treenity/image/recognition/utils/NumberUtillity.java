package com.treenity.image.recognition.utils;

public class NumberUtillity {
	public static boolean isNumeric(String str) {
	    try {
	        Integer.parseInt(str);
	        return true;
	    } catch (NumberFormatException e) {
	        return false;
	    }
	}
}
