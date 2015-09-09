package com.technisat.appstock.app;

import android.graphics.Bitmap;
import android.view.View;
import android.widget.ProgressBar;

import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

public class ImageLoaderSpinner implements ImageLoadingListener {
	
	private final ProgressBar mSpinner;
	
	public ImageLoaderSpinner(ProgressBar pb){
		mSpinner = pb;
	}
	
	/**
	 * Safe version, sometimes spinner instance does not exist
	 */
	private void spinnerShow() {
		if(mSpinner != null)
			mSpinner.setVisibility(View.VISIBLE);
	}
	
	/**
	 * Same as above, sometimes mSpiner doesn't exist (invoked before UI is created).
	 */
	private void spinnerHide() {
		if(mSpinner != null)
			mSpinner.setVisibility(View.GONE);
	}
	
	@Override
    public void onLoadingStarted(String imageUri, View view) {
		spinnerShow();
    }

    @Override
    public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
    	spinnerHide();
    }

    @Override
    public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
    	spinnerHide();
    }

	@Override
	public void onLoadingCancelled(String imageUri, View view) {
		spinnerHide();
	}

}
