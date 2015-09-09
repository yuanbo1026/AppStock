package com.technisat.appstock.login;

import com.technisat.appstock.R;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

public class ValidationErrorDialog extends Dialog{

	private Button dismissBtn;
	private TextView errorTextView;
	private String errorString;

	public ValidationErrorDialog(Context context, String error) {
		super(context);
		errorString = error;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		requestWindowFeature(Window.FEATURE_NO_TITLE);
	    setContentView(R.layout.dialog_error);
	    
	    errorTextView = (TextView) findViewById(R.id.error_message);
	    errorTextView.setText(errorString);
	    
	    dismissBtn = (Button) findViewById(R.id.btn_dismiss);
	    dismissBtn.setOnClickListener(new View.OnClickListener(){

			@Override
			public void onClick(View v) {
				ValidationErrorDialog.this.dismiss();				
			}
	    	
	    });
	}
}

