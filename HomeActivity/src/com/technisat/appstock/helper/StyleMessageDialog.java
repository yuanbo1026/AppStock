package com.technisat.appstock.helper;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.technisat.appstock.R;

public class StyleMessageDialog extends Dialog{

	private Button dismissBtn;
	private TextView messageTextView;
	private String messageString;

	public StyleMessageDialog(Context context, String message) {
		super(context);
		messageString = message;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		requestWindowFeature(Window.FEATURE_NO_TITLE);
	    setContentView(R.layout.dialog_error);
	    
	    messageTextView = (TextView) findViewById(R.id.error_message);
	    messageTextView.setText(messageString);
	    
	    dismissBtn = (Button) findViewById(R.id.btn_dismiss);
	    dismissBtn.setOnClickListener(new View.OnClickListener(){

			@Override
			public void onClick(View v) {
				StyleMessageDialog.this.dismiss();				
			}
	    	
	    });
	}
}