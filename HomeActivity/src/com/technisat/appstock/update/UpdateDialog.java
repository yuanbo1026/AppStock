package com.technisat.appstock.update;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ProgressBar;

import com.technisat.appstock.R;
import com.technisat.appstock.app.CustomTextView;

public class UpdateDialog extends Dialog {
	UpdateActionListener mListener;
	String mCurrentVersion;
	String mLastestVersion;
	CustomTextView mCurrentVersionTv;
	CustomTextView mLastestVersionTv;
	CustomTextView mUpdateTitle;
	ProgressBar mProgressBar;

	public UpdateDialog(Context context, UpdateActionListener listener) {
		super(context);
		mListener = listener;
	}
	
	public UpdateDialog(Context context,String currentVersion,String lastestVersion,UpdateActionListener listener) {
		super(context);
		mListener = listener;
		mCurrentVersion = currentVersion;
		mLastestVersion = lastestVersion;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.dialog_update);
		
		mUpdateTitle = (CustomTextView) findViewById(R.id.tv_update_title);
		
		mCurrentVersionTv = (CustomTextView) findViewById(R.id.tv_current_version_code_text);
		mLastestVersionTv = (CustomTextView) findViewById(R.id.tv_lastest_version_code_text);
		mProgressBar = (ProgressBar) findViewById(R.id.update_progress_spinner);
		
		mCurrentVersionTv.setText("Current Version: "+mCurrentVersion);
		mLastestVersionTv.setText("Lastest Veriosn: "+mLastestVersion);

		findViewById(R.id.btn_update).setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						mListener.onSend(UpdateDialog.this,true);
					}
				});

		findViewById(R.id.btn_no_update).setOnClickListener(
				new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						mListener.onSend(UpdateDialog.this,false);
					}
				});
	}

	public interface UpdateActionListener {

		void onSend(UpdateDialog dialog, Boolean updateble);
	}
}
