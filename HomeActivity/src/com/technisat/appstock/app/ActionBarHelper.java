package com.technisat.appstock.app;

import android.app.ActionBar;
import android.content.Context;
import android.text.Spannable;
import android.text.SpannableString;

public class ActionBarHelper {
	
	public static void setActionBarTitle(Context context, ActionBar actionBar, String title){
		SpannableString s = new SpannableString("   "+title.toUpperCase());
		s.setSpan(new TypefaceSpan(context, "TitilliumWeb-Regular.ttf"), 0, s.length(),
		        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		actionBar.setTitle(s);
	}
}
