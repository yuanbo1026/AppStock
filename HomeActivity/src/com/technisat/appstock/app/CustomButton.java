 package com.technisat.appstock.app;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.Button;

public class CustomButton extends Button {

	/*
	 * Caches typefaces based on their file path and name, so that they don't have to be created every time when they are referenced.
	 */
	private static Typeface mTypeface;
	private static Typeface mTypefaceBold;
	public CustomButton(final Context context) {
	    this(context, null);
	}
	
	public CustomButton(final Context context, final AttributeSet attrs) {
	    this(context, attrs, 0);
	}
	
	public CustomButton(final Context context, final AttributeSet attrs, final int defStyle) {
	    super(context, attrs, defStyle); 
	    int style = attrs.getAttributeIntValue("http://schemas.android.com/apk/res/android", "textStyle", 0);
	    Typeface tf = null;
	    
	    if(style == 1){
	    	if(mTypefaceBold == null){
	    		mTypefaceBold = Typeface.createFromAsset(context.getAssets(), "fonts/TitilliumWeb-Bold.ttf");
	    	}
	    	tf = mTypefaceBold;
	    }
	    else {
			if (mTypeface == null) {
				mTypeface = Typeface.createFromAsset(context.getAssets(), "fonts/TitilliumWeb-Regular.ttf");
			}
			tf = mTypeface;
	    }
	     setTypeface(tf);
	}

}
