package com.technisat.constants;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

import android.content.Context;
import android.os.Build;
import android.util.DisplayMetrics;

public class Nexxoo {
	
	public static final String TAG = "Nexxoo";
	public static final String TAG2 = "nexxoo2";
	public static final String PAYPAL = "Paypal";
	
	public static final String REPLACE_1 = "###x###";
	public static final String REPLACE_2 = "###y###";
	
	public static String getDeviceId(){
		String id = Build.SERIAL;
		
		if (id == null)
			id = Build.FINGERPRINT;
		
		return id;
	}
	
	/**
	 * Calculates the dp from a given pixel-value depending on device configuration.
	 * @param ctx Context
	 * @param px Pixels to calculate dp from
	 * @return The dp value.
	 */
	public static int pxToDp(Context ctx, int px) {
	    DisplayMetrics displayMetrics = ctx.getResources().getDisplayMetrics();
	    int dp = Math.round(px / (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
	    return dp;
	}
	
	/**
	 * Calculates the pixel from a given dp-value depending on device configuration.
	 * @param ctx Context
	 * @param dp DP to calculate pixels from
	 * @return The pixel value.
	 */
	public static int dpToPx(Context ctx, int dp) {
	    DisplayMetrics displayMetrics = ctx.getResources().getDisplayMetrics();
	    int px = Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));       
	    return px;
	}
	
	public static String getLocalIps(){
		String result = "";
		try {
	        for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) {
	            NetworkInterface intf = en.nextElement();
	            for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();) {
	                InetAddress inetAddress = enumIpAddr.nextElement();
	                if (!inetAddress.isLoopbackAddress()) {
	                    result += inetAddress.getHostAddress() + "\n";
	                }
	            }
	        }
	    } catch (SocketException ex) {
	        ex.printStackTrace();
	    }
	    return result;
	}

}
