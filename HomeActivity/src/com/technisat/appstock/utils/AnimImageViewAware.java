package com.technisat.appstock.utils;

import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.imageaware.ImageViewAware;

public class AnimImageViewAware extends ImageViewAware {

    public AnimImageViewAware(ImageView imageView) {
        super(imageView);
    }

    public AnimImageViewAware(ImageView imageView, boolean checkActualViewSize) {
        super(imageView, checkActualViewSize);
    }

    @Override
    protected void setImageDrawableInto(Drawable drawable, View view) {
        super.setImageDrawableInto(drawable, view);
        if (drawable instanceof AnimationDrawable) {
            ((AnimationDrawable) drawable).start();
        }
    }
}