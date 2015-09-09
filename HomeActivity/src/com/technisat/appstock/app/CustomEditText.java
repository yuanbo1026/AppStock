package com.technisat.appstock.app;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.EditText;

public class CustomEditText extends EditText {

	/*
	 * Caches typefaces based on their file path and name, so that they don't have to be created every time when they are referenced.
	 */
	private static Typeface mTypeface;
	
	
	public CustomEditText(final Context context) {
	    super(context);
	    init(context);
	}
	
	public CustomEditText(final Context context, final AttributeSet attrs) {
	    super(context, attrs);
	    init(context);
	}
	
	public CustomEditText(final Context context, final AttributeSet attrs, final int defStyle) {
	    super(context, attrs, defStyle);
	    init(context);
	}
	
	private void init(Context context){
		if (mTypeface == null) {
	         mTypeface = Typeface.createFromAsset(context.getAssets(), "fonts/TitilliumWeb-Regular.ttf");
	    }
	    setTypeface(mTypeface);
	}
}
