package com.technisat.appstock.utils;

import android.graphics.Bitmap;
import android.opengl.Visibility;
import android.view.View;
import android.widget.ProgressBar;

import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.technisat.appstock.R;

public class ProgressBarImageLoadingListener extends SimpleImageLoadingListener {

	private ProgressBar pb;

	public ProgressBarImageLoadingListener(ProgressBar pb) {
		this.pb = pb;
	}

	@Override
	public void onLoadingStarted(String imageUri, View view) {
		if (this.pb != null)
			this.pb.setVisibility(View.VISIBLE);
		super.onLoadingStarted(imageUri, view);
	}

	@Override
	public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
		if (this.pb != null)
			this.pb.setVisibility(View.GONE);
	}
}
