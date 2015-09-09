package com.technisat.appstock.app;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint.FontMetricsInt;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

public class CustomTextView extends TextView {

	/*
	 * Caches typefaces based on their file path and name, so that they don't have to be created every time when they are referenced.
	 */
	private static Typeface mTypeface;
	private static Typeface mTypefaceBold;
	
	public CustomTextView(final Context context) {
	    this(context, null);
	}
	
	public CustomTextView(final Context context, final AttributeSet attrs) {
	    this(context, attrs, 0);
	}
	
	public CustomTextView(final Context context, final AttributeSet attrs, final int defStyle) {
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
	
//	@Override
//	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
//		super.onMeasure(widthMeasureSpec, (int) (heightMeasureSpec * 0.5f));
//	}

	FontMetricsInt fontMetricsInt;
	@Override
	protected void onDraw(Canvas canvas) {
	    if (true){
	        if (fontMetricsInt == null){
	            fontMetricsInt = new FontMetricsInt();
	            getPaint().getFontMetricsInt(fontMetricsInt);
	        }
	        canvas.translate(0, (fontMetricsInt.top - fontMetricsInt.bottom) * 0.05f);
	    }
	    super.onDraw(canvas);
	}
}
