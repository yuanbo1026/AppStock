package com.technisat.appstock.utils;

import com.technisat.appstock.R;
import com.technisat.appstock.helper.StyleMessageDialog;

import android.content.Context;
import android.util.Log;

public class AlertDialogUtil {
	
	private static StyleMessageDialog sd;
	
	public static void show(final Context context)
    {
		if(sd == null || !sd.isShowing()) {
			sd = new StyleMessageDialog(context,context.getString(R.string.appstock_msg_noInternetConnection));
			sd.show();
		} else
			Log.d("JD", "aleady showing");
    }
}
