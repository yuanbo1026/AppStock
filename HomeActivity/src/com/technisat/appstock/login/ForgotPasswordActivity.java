package com.technisat.appstock.login;

import org.json.JSONObject;

import android.app.Activity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.technisat.appstock.R;
import com.technisat.appstock.webservice.NexxooWebservice;
import com.technisat.appstock.webservice.OnJSONResponse;

public class ForgotPasswordActivity extends Activity {
	
	private Button mBtnRequestEmail;
	private EditText mEditTextEmail;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_forgot_password);
		
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setHomeButtonEnabled(true);
		
    	mEditTextEmail = (EditText) findViewById(R.id.et_forgot_email);
    	
    	/****************************************************
    	 * user enters his email or user-name				*
    	 * and the web service will send an email to him	*
    	 * with a link to change his password				*
    	 ****************************************************/
    	mBtnRequestEmail = (Button) findViewById(R.id.btn_forgot_request_email);
    	mBtnRequestEmail.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				NexxooWebservice.getNewPasswordMail(true,
						mEditTextEmail.getText().toString(),
						new OnJSONResponse() {
					
							@Override
							public void onReceivedJSONResponse(JSONObject json) {
								Toast.makeText(getApplicationContext(), getString(R.string.appstock_msg_forgotpassword), Toast.LENGTH_LONG).show();
							}
							
							@Override
							public void onReceivedError(String msg, int code) {
								Toast.makeText(getApplicationContext(), getString(R.string.appstock_msg_forgotpassword_not_found), Toast.LENGTH_LONG).show();
							}
				});
			}
		});
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case android.R.id.home:
	        finish();
	        return true;
        default:
        	return true;
        }
	}
}