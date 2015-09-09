package com.technisat.appstock.content.detail;

import com.technisat.appstock.R;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;

public class CommentDialog extends Dialog{
	CommentActionListener mListener;
	EditText mCommentInput;

	public CommentDialog(Context context, CommentActionListener listener) {
		super(context);
		mListener = listener;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		requestWindowFeature(Window.FEATURE_NO_TITLE);
	    setContentView(R.layout.dialog_comment);
	    
	    mCommentInput = (EditText) findViewById(R.id.et_comment_input);
	    
	    getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
	    
	    findViewById(R.id.btn_rate_with_comment).setOnClickListener(new View.OnClickListener(){
			@Override
			public void onClick(View v) {
				String comment = mCommentInput.getText().toString();
				mListener.onSend(CommentDialog.this, comment);
			}	    	
	    });
	    
	    findViewById(R.id.btn_rate_no_comment).setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				mListener.onSend(CommentDialog.this);	
			}
		});
	}
	
	public interface CommentActionListener {
		void onSend(CommentDialog dialog, String comment);
		void onSend(CommentDialog dialog);
	}
}
